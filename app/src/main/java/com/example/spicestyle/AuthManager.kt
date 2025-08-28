package com.example.spicestyle

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Base64
import androidx.browser.customtabs.CustomTabsIntent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Json
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.concurrent.TimeUnit

/**
 * Single source of truth for Spotify authentication (PKCE) and token storage.
 * - Launches auth via Custom Tabs
 * - Handles redirect
 * - Exchanges auth code for tokens
 * - Refreshes tokens when expired
 */
object AuthManager {

    // TODO: put your actual Spotify app client id here
    private const val CLIENT_ID = "YOUR_SPOTIFY_CLIENT_ID"

    // Must match AndroidManifest intent-filter and strings.xml
    private const val REDIRECT_URI = "spicestyle://callback"

    // Request what you need. You can expand later.
    private const val SCOPES = listOf(
        "user-read-currently-playing",
        "user-read-playback-state",
        "user-modify-playback-state",
        "app-remote-control",
        "streaming"
    ).joinToString(" ")

    // Prefs
    private const val PREFS = "spotify_auth_prefs"
    private const val KEY_ACCESS = "access_token"
    private const val KEY_REFRESH = "refresh_token"
    private const val KEY_EXPIRES_AT = "expires_at" // epoch seconds
    private const val KEY_VERIFIER = "code_verifier"

    // --- Public API ---

    fun isLoggedIn(context: Context): Boolean {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return p.getString(KEY_ACCESS, null) != null && p.getString(KEY_REFRESH, null) != null
    }

    fun startLogin(activity: Activity) {
        val codeVerifier = generateCodeVerifier()
        val codeChallenge = codeChallengeS256(codeVerifier)
        // Save verifier so we can exchange later
        activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY_VERIFIER, codeVerifier).apply()

        val uri = Uri.Builder()
            .scheme("https")
            .authority("accounts.spotify.com")
            .path("authorize")
            .appendQueryParameter("client_id", CLIENT_ID)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("redirect_uri", REDIRECT_URI)
            .appendQueryParameter("code_challenge_method", "S256")
            .appendQueryParameter("code_challenge", codeChallenge)
            .appendQueryParameter("scope", SCOPES)
            .build()

        CustomTabsIntent.Builder().build().launchUrl(activity, uri)
    }

    /**
     * Call from your AuthCallbackActivity when you receive the redirect.
     */
    suspend fun handleRedirect(context: Context, data: Uri?): Boolean = withContext(Dispatchers.IO) {
        if (data == null) return@withContext false
        val code = data.getQueryParameter("code") ?: return@withContext false

        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val codeVerifier = prefs.getString(KEY_VERIFIER, null) ?: return@withContext false

        val service = tokenService()
        val resp = service.exchangeCode(
            clientId = CLIENT_ID,
            grantType = "authorization_code",
            code = code,
            redirectUri = REDIRECT_URI,
            codeVerifier = codeVerifier
        )

        saveTokens(context, resp)
        // clear one-time verifier
        prefs.edit().remove(KEY_VERIFIER).apply()
        true
    }

    /**
     * Returns a valid access token, refreshing if necessary.
     */
    suspend fun getValidAccessToken(context: Context): String? = withContext(Dispatchers.IO) {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val access = p.getString(KEY_ACCESS, null)
        val refresh = p.getString(KEY_REFRESH, null)
        val expiresAt = p.getLong(KEY_EXPIRES_AT, 0L)
        val now = System.currentTimeMillis() / 1000L

        if (access != null && now < expiresAt - 30) {
            return@withContext access
        }
        // need refresh
        if (refresh.isNullOrEmpty()) return@withContext null

        val service = tokenService()
        val resp = service.refreshToken(
            clientId = CLIENT_ID,
            grantType = "refresh_token",
            refreshToken = refresh
        )
        saveTokens(context, resp, keepRefresh = refresh)
        return@withContext context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_ACCESS, null)
    }

    fun logout(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().clear().apply()
    }

    // --- Internals ---

    private fun saveTokens(context: Context, resp: TokenResponse, keepRefresh: String? = null) {
        val now = System.currentTimeMillis() / 1000L
        val expiresAt = now + (resp.expiresIn ?: 3600)
        val refresh = resp.refreshToken ?: keepRefresh
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(KEY_ACCESS, resp.accessToken)
            .apply {
                if (!refresh.isNullOrEmpty()) putString(KEY_REFRESH, refresh)
            }
            .putLong(KEY_EXPIRES_AT, expiresAt)
            .apply()
    }

    private fun tokenService(): SpotifyTokenService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://accounts.spotify.com/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(SpotifyTokenService::class.java)
    }

    // PKCE helpers
    private fun generateCodeVerifier(): String {
        val bytes = ByteArray(32)
        SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }

    private fun codeChallengeS256(verifier: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(verifier.toByteArray(Charsets.US_ASCII))
        return Base64.encodeToString(digest, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }
}

// Retrofit token API
interface SpotifyTokenService {
    @FormUrlEncoded
    @POST("api/token")
    suspend fun exchangeCode(
        @Field("client_id") clientId: String,
        @Field("grant_type") grantType: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("code_verifier") codeVerifier: String
    ): TokenResponse

    @FormUrlEncoded
    @POST("api/token")
    suspend fun refreshToken(
        @Field("client_id") clientId: String,
        @Field("grant_type") grantType: String,
        @Field("refresh_token") refreshToken: String
    ): TokenResponse
}

data class TokenResponse(
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "token_type") val tokenType: String?,
    @Json(name = "expires_in") val expiresIn: Long?,
    @Json(name = "refresh_token") val refreshToken: String?
)

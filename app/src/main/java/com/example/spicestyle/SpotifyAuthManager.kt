package com.example.spicestyle

import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Central place for Spotify OAuth.
 * Replace CLIENT_ID and REDIRECT_URI with your values (and update the Manifest data-scheme/host).
 */
object SpotifyAuthManager {
    // TODO: set your real client id and (optionally) client secret if you use the code+secret exchange.
    const val CLIENT_ID: String = "YOUR_SPOTIFY_CLIENT_ID"
    // On mobile, PKCE is recommended; do NOT ship client secret in-app.

    const val SCOPES: String =
        "user-read-playback-state user-modify-playback-state user-read-currently-playing"

    // This must match the intent filter in AndroidManifest.xml
    const val REDIRECT_SCHEME = "spicestyle"
    const val REDIRECT_HOST = "callback"
    val REDIRECT_URI: String = "$REDIRECT_SCHEME://$REDIRECT_HOST"

    private const val ACCOUNTS_BASE = "https://accounts.spotify.com/"

    fun buildAuthorizeUri(state: String): Uri {
        return Uri.parse("${ACCOUNTS_BASE}authorize")
            .buildUpon()
            .appendQueryParameter("client_id", CLIENT_ID)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("redirect_uri", REDIRECT_URI)
            .appendQueryParameter("scope", SCOPES)
            .appendQueryParameter("state", state)
            // For production, implement PKCE: add code_challenge & code_challenge_method=S256
            .build()
    }

    // Retrofit API for token exchange
    interface SpotifyAuthApi {
        @FormUrlEncoded
        @POST("api/token")
        suspend fun exchangeCodeForToken(
            @Field("grant_type") grantType: String = "authorization_code",
            @Field("code") code: String,
            @Field("redirect_uri") redirectUri: String,
            @Field("client_id") clientId: String,
            // If using PKCE: add @Field("code_verifier") codeVerifier: String
        ): TokenResponse
    }

    data class TokenResponse(
        val access_token: String,
        val token_type: String,
        val expires_in: Long,
        val refresh_token: String?
    )

    private val api: SpotifyAuthApi by lazy {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()

        Retrofit.Builder()
            .baseUrl(ACCOUNTS_BASE)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(SpotifyAuthApi::class.java)
    }

    suspend fun exchangeCode(code: String): TokenResponse =
        withContext(Dispatchers.IO) {
            api.exchangeCodeForToken(
                code = code,
                redirectUri = REDIRECT_URI,
                clientId = CLIENT_ID
            )
        }
}

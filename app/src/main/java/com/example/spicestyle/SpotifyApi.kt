package com.example.spicestyle

import com.google.gson.annotations.SerializedName
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

/** --- Models --- **/
data class MeResponse(
    @SerializedName("display_name") val displayName: String?,
    val email: String?
)
data class PlaylistsResponse(
    val items: List<PlaylistItem>,
    val total: Int
)
data class PlaylistItem(
    val id: String,
    val name: String
)
data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("expires_in") val expiresIn: Int,
    @SerializedName("refresh_token") val refreshToken: String?
)

/** --- Auth (token) service --- **/
interface SpotifyAuthService {
    @FormUrlEncoded
    @POST(AuthManager.TOKEN_ENDPOINT)
    suspend fun getTokens(
        @FieldMap fields: Map<String, @JvmSuppressWildcards String>
    ): TokenResponse
}

/** --- Web API service --- **/
interface SpotifyService {
    @GET("me")
    suspend fun me(): MeResponse

    @GET("me/playlists")
    suspend fun playlists(@Query("limit") limit: Int = 20): PlaylistsResponse
}

/** --- Builders --- **/
object SpotifyClient {
    fun authed(accessToken: String): SpotifyService {
        val auth = Interceptor { chain ->
            val req: Request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
            chain.proceed(req)
        }
        val ok = OkHttpClient.Builder()
            .addInterceptor(auth)
            .build()
        return Retrofit.Builder()
            .baseUrl("https://api.spotify.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(ok)
            .build()
            .create(SpotifyService::class.java)
    }

    fun auth(): SpotifyAuthService {
        // Spotify token endpoint is absolute URL; Retrofit will still honor it
        val ok = OkHttpClient.Builder().build()
        return Retrofit.Builder()
            .baseUrl("https://accounts.spotify.com/") // base not used directly
            .addConverterFactory(GsonConverterFactory.create())
            .client(ok)
            .build()
            .create(SpotifyAuthService::class.java)
    }
}

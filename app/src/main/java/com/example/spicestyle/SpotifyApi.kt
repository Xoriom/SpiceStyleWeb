package com.example.spicestyle

import com.google.gson.annotations.SerializedName
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

/** -------- Models you already had -------- */
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

/** -------- New models for Now Playing -------- */
data class CurrentlyPlayingResponse(
    @SerializedName("is_playing") val isPlaying: Boolean?,
    @SerializedName("progress_ms") val progressMs: Long?,
    val item: Track?,
    val device: Device?
)
data class Device(
    val id: String?,
    val name: String?,
    @SerializedName("is_active") val isActive: Boolean?
)
data class Track(
    val id: String?,
    val name: String?,
    val artists: List<Artist>?,
    val album: Album?,
    @SerializedName("duration_ms") val durationMs: Long?
)
data class Artist(val name: String?)
data class Album(
    val name: String?,
    val images: List<SpotifyImage>?
)
data class SpotifyImage(val url: String?, val width: Int?, val height: Int?)

/** -------- Auth (token) service -------- */
interface SpotifyAuthService {
    @FormUrlEncoded
    @POST(AuthManager.TOKEN_ENDPOINT)
    suspend fun getTokens(@FieldMap fields: Map<String, @JvmSuppressWildcards String>): TokenResponse
}

/** -------- Web API service -------- */
interface SpotifyService {
    // existing
    @GET("me") suspend fun me(): MeResponse
    @GET("me/playlists") suspend fun playlists(@Query("limit") limit: Int = 20): PlaylistsResponse

    // now playing / playback state
    @GET("me/player/currently-playing")
    suspend fun currentlyPlaying(): Response<CurrentlyPlayingResponse> // 204 when nothing

    @GET("me/player")
    suspend fun player(): Response<CurrentlyPlayingResponse> // broader state; also 204 possible

    // controls (require Premium + active device)
    @PUT("me/player/pause")
    suspend fun pause(): Response<Unit>

    @PUT("me/player/play")
    suspend fun play(): Response<Unit>

    @POST("me/player/next")
    suspend fun next(): Response<Unit>

    @POST("me/player/previous")
    suspend fun previous(): Response<Unit>

    @PUT("me/player/seek")
    suspend fun seek(@Query("position_ms") positionMs: Long): Response<Unit>

    @PUT("me/player/shuffle")
    suspend fun shuffle(@Query("state") enabled: Boolean): Response<Unit>

    // state: "track" | "context" | "off"
    @PUT("me/player/repeat")
    suspend fun repeat(@Query("state") state: String): Response<Unit>
}

/** -------- Builders -------- */
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
        val ok = OkHttpClient.Builder().build()
        return Retrofit.Builder()
            .baseUrl("https://accounts.spotify.com/") // base; endpoint is absolute
            .addConverterFactory(GsonConverterFactory.create())
            .client(ok)
            .build()
            .create(SpotifyAuthService::class.java)
    }
}

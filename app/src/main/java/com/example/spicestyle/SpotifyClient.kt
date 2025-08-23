package com.example.spicestyle

import retrofit2.Response

// --- minimal data models used by NowPlayingActivity ---
data class Image(val url: String? = null, val width: Int? = null, val height: Int? = null)
data class Album(val name: String? = null, val images: List<Image>? = null)
data class Artist(val name: String? = null)
data class Track(
    val name: String? = null,
    val durationMs: Long? = 0L,
    val album: Album? = null,
    val artists: List<Artist>? = null
)
data class CurrentlyPlaying(
    val item: Track? = null,
    val isPlaying: Boolean? = false,
    val progressMs: Long? = 0L
)

// --- stubbed client so the project builds and UI works ---
class SpotifyClient private constructor(private val token: String) {

    companion object {
        fun authed(token: String): SpotifyClient = SpotifyClient(token)
    }

    suspend fun previous() { /* TODO real call */ }
    suspend fun next() { /* TODO real call */ }
    suspend fun pause() { /* TODO real call */ }
    suspend fun play() { /* TODO real call */ }
    suspend fun shuffle(on: Boolean) { /* TODO real call */ }
    suspend fun repeat(mode: String) { /* TODO real call */ }
    suspend fun seek(ms: Long) { /* TODO real call */ }

    suspend fun currentlyPlaying(): Response<CurrentlyPlaying> {
        // placeholder data so UI shows something
        val demo = CurrentlyPlaying(
            item = Track(
                name = "Demo Track",
                durationMs = 240_000,
                album = Album(
                    name = "Demo Album",
                    images = listOf(Image("https://i.scdn.co/image/ab67616d0000b273a0a0a0a0a0a0a0a0a0a0a0a0", 640, 640))
                ),
                artists = listOf(Artist("Demo Artist"))
            ),
            isPlaying = true,
            progressMs = 45_000
        )
        return Response.success(demo)
    }
}

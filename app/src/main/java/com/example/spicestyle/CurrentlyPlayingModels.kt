package com.example.spicestyle

data class CurrentlyPlayingResponse(
    val is_playing: Boolean? = null,
    val progress_ms: Int? = null,
    val item: Track? = null
)

data class Track(
    val name: String? = null,
    val artists: List<Artist>? = null,
    val album: Album? = null,
    val duration_ms: Int? = null
)

data class Artist(
    val name: String? = null
)

data class Album(
    val images: List<Image>? = null,
    val name: String? = null
)

data class Image(
    val url: String? = null,
    val height: Int? = null,
    val width: Int? = null
)

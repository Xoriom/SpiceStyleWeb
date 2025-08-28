package com.example.spicestyle

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface SpotifyApi {

    @GET("me/player/currently-playing")
    suspend fun getCurrentlyPlaying(
        @Header("Authorization") bearer: String
    ): Response<CurrentlyPlayingResponse>

    @PUT("me/player/pause")
    suspend fun pause(
        @Header("Authorization") bearer: String
    ): Response<Unit>

    @PUT("me/player/play")
    suspend fun play(
        @Header("Authorization") bearer: String
    ): Response<Unit>

    @POST("me/player/next")
    suspend fun next(
        @Header("Authorization") bearer: String
    ): Response<Unit>

    @POST("me/player/previous")
    suspend fun previous(
        @Header("Authorization") bearer: String
    ): Response<Unit>

    @PUT("me/player/shuffle")
    suspend fun shuffle(
        @Header("Authorization") bearer: String,
        @Query("state") state: Boolean
    ): Response<Unit>

    // state = "track" | "context" | "off"
    @PUT("me/player/repeat")
    suspend fun repeat(
        @Header("Authorization") bearer: String,
        @Query("state") state: String
    ): Response<Unit>
}

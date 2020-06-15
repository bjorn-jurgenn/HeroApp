package cz.jiricerveny.heroapp.spacex

import cz.jiricerveny.heroapp.spacex.launches.database.Launch
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SpaceXEndpoints {
    @GET("v3/landpads")
    fun getLandPads(): Call<List<LandPadData>>

    @GET("v3/launches")
    fun getLaunches(
        @Query("launch_year") launch_year: Int?,
        @Query("launch_success") launch_success: Boolean?
    ): Call<List<Launch>>
}
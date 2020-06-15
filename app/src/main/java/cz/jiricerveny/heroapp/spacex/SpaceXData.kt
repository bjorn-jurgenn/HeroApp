package cz.jiricerveny.heroapp.spacex

import com.google.gson.annotations.SerializedName

data class LandPadData(
    val id: String,
    @SerializedName("full_name")
    val name: String,
    val status: String,
    val location: LandPadLocation,
    @SerializedName("landing_type")
    val type: String,
    @SerializedName("successful_landings")
    val successfulLandings: Int,
    @SerializedName("attempted_landings")
    val totalLandings: Int,
    val wikipedia: String,
    val details: String
)

data class LandPadLocation(
    val name: String,
    val region: String,
    val latitude: String,
    val longitude: String
)
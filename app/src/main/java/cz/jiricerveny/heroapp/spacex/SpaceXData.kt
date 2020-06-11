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

data class
LaunchesData(
    @SerializedName("flight_number")
    val flightNumber: String,
    @SerializedName("mission_name")
    val missionName: String,
    val upcoming: Boolean,
    @SerializedName("launch_year")
    val launchYear: Int,
    @SerializedName("launch_date_local")
    val launchDate: String,
    val rocket: LaunchRocket,
    @SerializedName("launch_success")
    val success: Boolean?,
    @SerializedName("launch_site")
    val launchSite: LaunchSite,
    val detail: String?,
    val wikipedia: String?
)

data class LaunchRocket(
    @SerializedName("rocket_id")
    val id: String,
    @SerializedName("rocket_name")
    val name: String,
    @SerializedName("rocket_type")
    val type: String

)

data class LaunchSite(
    @SerializedName("site_name_long")
    val name: String
)
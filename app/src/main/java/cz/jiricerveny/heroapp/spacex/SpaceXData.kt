package cz.jiricerveny.heroapp.spacex

import com.google.gson.annotations.SerializedName

data class LandPadData(
    val id: String,
    val full_name: String,
    val status: String,
    val location: LandPadLocation,
    val landing_type: String,
    val successful_landings: Int,
    val attempted_landings: Int,
    val wikipedia: String,
    val details: String
)

data class LandPadLocation(
    val name: String,
    val region: String,
    val latitude: String,
    val longitude: String
)

data class LaunchesData(
    val flight_number: String,
    val mission_name: String,
    val upcoming: Boolean,
    val launch_year: Int,
    val launch_date_local: String,
    val rocket: LaunchRocket,
    val launch_success: Boolean?,
    val launch_site: LaunchSite,
    val detail: String,
    val wikipedia: String
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
package cz.jiricerveny.heroapp.spacex.launches.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = "launch_database")
data class Launch(
    @PrimaryKey(autoGenerate = true)
    val flight_number: Int,
    @SerializedName("mission_name")
    @ColumnInfo(name = "mission_name")
    val missionName: String,
    @ColumnInfo(name = "upcoming")
    val upcoming: Boolean,
    @SerializedName("launch_year")
    @ColumnInfo(name = "launch_year")
    val launchYear: Int,
    @SerializedName("launch_date_local")
    @ColumnInfo(name = "launch_date_local")
    val launchDateLocal: String,
    @SerializedName("rocket")
    @ColumnInfo(name = "rocket")
    val rocket: LaunchRocket,
    @SerializedName("launch_success")
    @ColumnInfo(name = "launch_success")
    val launchSuccess: Boolean?,
    @SerializedName("launch_site")
    @ColumnInfo(name = "launch_site")
    val launchSite: LaunchSite,
    @SerializedName("detail")
    @ColumnInfo(name = "detail")
    val detail: String?,
    @ColumnInfo(name = "wikipedia")
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
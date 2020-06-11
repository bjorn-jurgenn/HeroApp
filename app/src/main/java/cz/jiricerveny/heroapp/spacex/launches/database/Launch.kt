package cz.jiricerveny.heroapp.spacex.launches.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

// TODO remove and move annotations to LaunchItem
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
    @SerializedName("rocket_name")
    @ColumnInfo(name = "rocket_name")
    val rocketName: String,
    @SerializedName("launch_success")
    @ColumnInfo(name = "launch_success")
    val launchSuccess: Boolean?,
    @SerializedName("launch_site")
    @ColumnInfo(name = "launch_site")
    val launchSite: String,
    @SerializedName("detail")
    @ColumnInfo(name = "detail")
    val detail: String?,
    @ColumnInfo(name = "wikipedia")
    val wikipedia: String?
)

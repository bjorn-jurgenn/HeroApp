package cz.jiricerveny.heroapp.spacex.launches.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "launch_database")
data class Launch(
    @PrimaryKey(autoGenerate = true)
    val flight_number: Int,
    @ColumnInfo(name = "mission_name")
    val missionName: String,
    @ColumnInfo(name = "upcoming")
    val upcoming: Boolean,
    @ColumnInfo(name = "launch_year")
    val launchYear: Int,
    @ColumnInfo(name = "launch_date_local")
    val launchDateLocal: String,
    @ColumnInfo(name = "rocket_name")
    val rocketName: String,
    @ColumnInfo(name = "launch_success")
    val launchSuccess: Boolean?,
    @ColumnInfo(name = "launch_site")
    val launchSite: String,
    @ColumnInfo(name = "detail")
    val detail: String,
    @ColumnInfo(name = "wikipedia")
    val wikipedia: String
)

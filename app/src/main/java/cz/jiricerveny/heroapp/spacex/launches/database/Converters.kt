package cz.jiricerveny.heroapp.spacex.launches.database

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromLaunchRocket(launchRocket: LaunchRocket): String {
        return launchRocket.name
    }

    @TypeConverter
    fun toLaunchRocket(name: String): LaunchRocket {
        return LaunchRocket(name, name, name)
    }

    @TypeConverter
    fun fromLaunchSite(launchSite: LaunchSite): String {
        return launchSite.name
    }

    @TypeConverter
    fun toLaunchSite(name: String): LaunchSite {
        return LaunchSite(name)
    }
}
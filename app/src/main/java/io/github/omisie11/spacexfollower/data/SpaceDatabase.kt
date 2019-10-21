package io.github.omisie11.spacexfollower.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.omisie11.spacexfollower.data.converters.*
import io.github.omisie11.spacexfollower.data.dao.*
import io.github.omisie11.spacexfollower.data.model.*
import io.github.omisie11.spacexfollower.data.model.launch.UpcomingLaunch

@Database(
    entities = [Capsule::class, Core::class, Company::class, UpcomingLaunch::class,
        LaunchPad::class],
    version = 2
)
@TypeConverters(
    value = [MissionsConverter::class, HeadquarterConverter::class,
        RocketConverter::class, LaunchSiteConverter::class, JsonArrayToStringConverter::class,
        LaunchPadLocationConverter::class]
)
abstract class SpaceDatabase : RoomDatabase() {

    abstract fun capsulesDao(): CapsulesDao
    abstract fun coresDao(): CoresDao
    abstract fun companyDao(): CompanyDao
    abstract fun upcomingLaunchesDao(): UpcomingLaunchesDao
    abstract fun launchPadsDao(): LaunchPadsDao
}
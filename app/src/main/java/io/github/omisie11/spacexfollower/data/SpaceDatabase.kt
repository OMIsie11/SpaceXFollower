package io.github.omisie11.spacexfollower.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.omisie11.spacexfollower.data.converters.*
import io.github.omisie11.spacexfollower.data.dao.*
import io.github.omisie11.spacexfollower.data.model.*
import io.github.omisie11.spacexfollower.data.model.launch.Launch

@Database(
    entities = [Capsule::class, Core::class, Company::class, Launch::class,
        LaunchPad::class],
    version = 3
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
    abstract fun upcomingLaunchesDao(): AllLaunchesDao
    abstract fun launchPadsDao(): LaunchPadsDao
}
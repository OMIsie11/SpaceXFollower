package io.github.omisie11.spacexfollower.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.omisie11.spacexfollower.data.local.converters.HeadquarterConverter
import io.github.omisie11.spacexfollower.data.local.converters.JsonArrayToStringConverter
import io.github.omisie11.spacexfollower.data.local.converters.LaunchLinksConverter
import io.github.omisie11.spacexfollower.data.local.converters.LaunchPadLocationConverter
import io.github.omisie11.spacexfollower.data.local.converters.LaunchSiteConverter
import io.github.omisie11.spacexfollower.data.local.converters.MissionsConverter
import io.github.omisie11.spacexfollower.data.local.converters.RocketConverter
import io.github.omisie11.spacexfollower.data.local.dao.AllLaunchesDao
import io.github.omisie11.spacexfollower.data.local.dao.CapsulesDao
import io.github.omisie11.spacexfollower.data.local.dao.CompanyDao
import io.github.omisie11.spacexfollower.data.local.dao.CoresDao
import io.github.omisie11.spacexfollower.data.local.dao.LaunchPadsDao
import io.github.omisie11.spacexfollower.data.local.model.Capsule
import io.github.omisie11.spacexfollower.data.local.model.Company
import io.github.omisie11.spacexfollower.data.local.model.Core
import io.github.omisie11.spacexfollower.data.local.model.LaunchPad
import io.github.omisie11.spacexfollower.data.local.model.launch.Launch

@Database(
    entities = [Capsule::class, Core::class, Company::class, Launch::class,
        LaunchPad::class],
    version = 4
)
@TypeConverters(
    value = [MissionsConverter::class, HeadquarterConverter::class,
        RocketConverter::class, LaunchSiteConverter::class, JsonArrayToStringConverter::class,
        LaunchPadLocationConverter::class, LaunchLinksConverter::class]
)
abstract class SpaceDatabase : RoomDatabase() {

    abstract fun capsulesDao(): CapsulesDao
    abstract fun coresDao(): CoresDao
    abstract fun companyDao(): CompanyDao
    abstract fun upcomingLaunchesDao(): AllLaunchesDao
    abstract fun launchPadsDao(): LaunchPadsDao
}

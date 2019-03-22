package io.github.omisie11.spacexfollower.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.omisie11.spacexfollower.data.dao.CapsulesDao
import io.github.omisie11.spacexfollower.data.dao.CompanyDao
import io.github.omisie11.spacexfollower.data.dao.CoresDao
import io.github.omisie11.spacexfollower.data.model.Capsule
import io.github.omisie11.spacexfollower.data.model.Company
import io.github.omisie11.spacexfollower.data.model.Core
import io.github.omisie11.spacexfollower.data.converters.HeadquarterConverter
import io.github.omisie11.spacexfollower.data.converters.MissionsConverter
import io.github.omisie11.spacexfollower.data.converters.NextLaunchLaunchSiteConverter
import io.github.omisie11.spacexfollower.data.converters.NextLaunchRocketConverter
import io.github.omisie11.spacexfollower.data.dao.NextLaunchDao
import io.github.omisie11.spacexfollower.data.model.NextLaunch

@Database(entities = [Capsule::class, Core::class, Company::class, NextLaunch::class], version = 1)
@TypeConverters(
    value = [MissionsConverter::class, HeadquarterConverter::class,
        NextLaunchRocketConverter::class, NextLaunchLaunchSiteConverter::class]
)
abstract class SpaceDatabase : RoomDatabase() {

    abstract fun capsulesDao(): CapsulesDao
    abstract fun coresDao(): CoresDao
    abstract fun companyDao(): CompanyDao
    abstract fun nextLaunchDao(): NextLaunchDao

}
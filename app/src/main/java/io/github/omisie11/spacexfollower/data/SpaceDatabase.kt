package io.github.omisie11.spacexfollower.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.omisie11.spacexfollower.data.dao.CapsulesDao
import io.github.omisie11.spacexfollower.data.dao.CoresDao
import io.github.omisie11.spacexfollower.data.model.Capsule
import io.github.omisie11.spacexfollower.data.model.Core
import io.github.omisie11.spacexfollower.data.type_converters.MissionsConverter

@Database(entities = [Capsule::class, Core::class], version = 1)
@TypeConverters(value = [MissionsConverter::class])
abstract class SpaceDatabase : RoomDatabase() {

    abstract fun capsulesDao(): CapsulesDao
    abstract fun coresDao(): CoresDao

    /* Not needed, Koin is doing that now
    companion object {
        private var dbInstance: SpaceDatabase? = null

        fun getDatabase(context: Context): SpaceDatabase? {
            if (dbInstance == null) {

                dbInstance = Room.databaseBuilder<SpaceDatabase>(
                    context.applicationContext,
                    SpaceDatabase::class.java, "space_data.db"
                )
                    .build()
            }
            return dbInstance
        }
    }
    */
}
package io.github.omisie11.spacexfollower.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.omisie11.spacexfollower.data.model.Capsule

@Database(entities = [Capsule::class], version = 1)
@TypeConverters(value = [MissionsConverter::class])
abstract class SpaceDatabase : RoomDatabase() {

    abstract fun capsulesDao(): CapsulesDao

    /*
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
package org.juseni.daytoday.data.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.juseni.daytoday.data.db.dao.UserDao
import org.juseni.daytoday.data.db.entities.UserEntity

internal const val DATABASE_NAME = "day_to_day.db"

@Database(
    entities = [
        UserEntity::class,
    ],
    version = 1,
    exportSchema = true
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class DayToDayDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<DayToDayDatabase> {
    override fun initialize(): DayToDayDatabase
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<DayToDayDatabase>
): DayToDayDatabase {
    return builder
        // You have to implement MIGRATIONS to enable this
        // .addMigrations(MIGRATIONS)
        .fallbackToDestructiveMigrationOnDowngrade(dropAllTables = true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
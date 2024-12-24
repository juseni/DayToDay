package org.juseni.daytoday.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<DayToDayDatabase> {
    val dbFile = context.getDatabasePath(DATABASE_NAME)
    return Room.databaseBuilder(
        context = context,
        name = dbFile.absolutePath
    )
}
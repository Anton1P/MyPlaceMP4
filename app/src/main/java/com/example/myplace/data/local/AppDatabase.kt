package com.example.myplace.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PlaceEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun placeDao(): PlaceDao
}
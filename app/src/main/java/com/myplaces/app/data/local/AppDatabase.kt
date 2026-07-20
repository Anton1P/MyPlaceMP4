package com.myplaces.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.myplaces.app.data.local.dao.PlaceDao
import com.myplaces.app.data.local.entity.PlaceEntity

@Database(entities = [PlaceEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun placeDao(): PlaceDao
}

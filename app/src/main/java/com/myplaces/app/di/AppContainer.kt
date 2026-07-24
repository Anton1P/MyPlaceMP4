package com.myplaces.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.myplaces.app.data.local.AppDatabase
import com.myplaces.app.data.local.dao.PlaceDao
import com.myplaces.app.data.remote.api.GeocodingApiService
import com.myplaces.app.data.repository.PlaceRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "myplaces_prefs")

class AppContainer(context: Context) {

    // === Database ===
    private val database: AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "myplaces_database"
    ).build()

    // === DAO ===
    val placeDao: PlaceDao = database.placeDao()

    // === Retrofit ===
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(GeocodingApiService.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val geocodingApi: GeocodingApiService = retrofit.create(GeocodingApiService::class.java)

    // === Repository ===
    val placeRepository: PlaceRepository = PlaceRepository(placeDao, geocodingApi)

    // === DataStore ===
    val dataStore: DataStore<Preferences> = context.dataStore
}

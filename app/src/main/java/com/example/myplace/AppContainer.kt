package com.example.myplace

import android.content.Context
import androidx.room.Room
import com.example.myplace.data.local.AppDatabase
import com.example.myplace.data.local.PlaceDao
import com.example.myplace.data.remote.GeocodingApiService
import com.example.myplace.data.repository.PlaceRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(context: Context) {

    // === Database Room ===
    private val database: AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "myplaces_database"
    ).build()

    // === DAO ===
    val placeDao: PlaceDao = database.placeDao()

    // === Retrofit — Reverse Geocoding ===
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(GeocodingApiService.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val geocodingApi: GeocodingApiService = retrofit.create(GeocodingApiService::class.java)

    // === Repository ===
    val placeRepository: PlaceRepository = PlaceRepository(placeDao, geocodingApi)
}
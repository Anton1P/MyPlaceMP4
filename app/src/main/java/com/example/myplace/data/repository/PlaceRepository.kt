package com.example.myplace.data.repository

import com.example.myplace.data.local.PlaceDao
import com.example.myplace.data.local.PlaceEntity
import com.example.myplace.data.remote.GeocodingApiService
import kotlinx.coroutines.flow.Flow

class PlaceRepository(
    private val placeDao: PlaceDao,
    private val geocodingApi: GeocodingApiService
) {

    // Récupérer tous les lieux
    fun getAllPlaces(): Flow<List<PlaceEntity>> = placeDao.getAllPlaces()

    // Récupérer un lieu par id
    suspend fun getPlaceById(id: Long): PlaceEntity? = placeDao.getPlaceById(id)

    // Insérer un lieu
    suspend fun insertPlace(place: PlaceEntity): Long = placeDao.insertPlace(place)

    // Supprimer un lieu
    suspend fun deletePlace(place: PlaceEntity) = placeDao.deletePlace(place)

    // Reverse geocoding : coordonnées → adresse textuelle
    suspend fun resolveAddress(latitude: Double, longitude: Double): String {
        return try {
            val response = geocodingApi.reverseGeocode(longitude, latitude)
            response.features.firstOrNull()?.properties?.label ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    // Import : insérer un lieu seulement s'il n'existe pas déjà (anti-doublon)
    suspend fun insertIfNotDuplicate(place: PlaceEntity): Boolean {
        val count = placeDao.countDuplicate(
            lat = place.latitude,
            lon = place.longitude,
            authorId = place.authorId,
            createdAt = place.createdAt
        )
        return if (count == 0) {
            placeDao.insertPlace(place)
            true
        } else {
            false
        }
    }
}
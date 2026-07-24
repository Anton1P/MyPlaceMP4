package com.myplaces.app.data.repository

import com.myplaces.app.data.local.dao.PlaceDao
import com.myplaces.app.data.local.entity.PlaceEntity
import com.myplaces.app.data.remote.api.GeocodingApiService
import kotlinx.coroutines.flow.Flow

class PlaceRepository(
    private val placeDao: PlaceDao,
    private val geocodingApi: GeocodingApiService
) {

    // === LECTURE ===

    fun getAllPlaces(): Flow<List<PlaceEntity>> = placeDao.getAllPlaces()

    fun getMyPlaces(): Flow<List<PlaceEntity>> = placeDao.getMyPlaces()

    fun getImportedPlaces(): Flow<List<PlaceEntity>> = placeDao.getImportedPlaces()

    suspend fun getPlaceById(id: Long): PlaceEntity? = placeDao.getPlaceById(id)

    // === ECRITURE ===

    suspend fun insertPlace(place: PlaceEntity): Long = placeDao.insertPlace(place)

    suspend fun updatePlace(place: PlaceEntity) = placeDao.updatePlace(place)

    suspend fun deletePlaceById(placeId: Long) = placeDao.deletePlaceById(placeId)

    suspend fun deletePlace(place: PlaceEntity) = placeDao.deletePlace(place)

    // === GEOCODING ===

    suspend fun resolveAddress(lat: Double, lng: Double): String? {
        return try {
            val response = geocodingApi.reverseGeocode(longitude = lng, latitude = lat)
            response.features.firstOrNull()?.properties?.label
        } catch (e: Exception) {
            null // En cas d'erreur reseau, on stocke null (pas bloquant)
        }
    }

    // === EXPORT ===

    suspend fun getAllMyPlacesForExport(): List<PlaceEntity> =
        placeDao.getAllMyPlacesForExport()

    // === IMPORT avec anti-doublon ===

    suspend fun importPlaces(places: List<PlaceEntity>): Int {
        var importedCount = 0
        for (place in places) {
            val exists = placeDao.placeExists(place.authorId, place.createdAt)
            if (exists == 0) {
                placeDao.insertPlace(place.copy(isImported = true, photoPath = null))
                importedCount++
            }
        }
        return importedCount
    }
}

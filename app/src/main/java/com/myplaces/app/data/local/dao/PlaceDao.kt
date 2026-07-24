package com.myplaces.app.data.local.dao

import androidx.room.*
import com.myplaces.app.data.local.entity.PlaceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {

    // === LECTURE ===

    @Query("SELECT * FROM places ORDER BY created_at DESC")
    fun getAllPlaces(): Flow<List<PlaceEntity>>

    @Query("SELECT * FROM places WHERE is_imported = 0 ORDER BY created_at DESC")
    fun getMyPlaces(): Flow<List<PlaceEntity>>

    @Query("SELECT * FROM places WHERE is_imported = 1 ORDER BY created_at DESC")
    fun getImportedPlaces(): Flow<List<PlaceEntity>>

    @Query("SELECT * FROM places WHERE id = :placeId")
    suspend fun getPlaceById(placeId: Long): PlaceEntity?

    // === ECRITURE ===

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: PlaceEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaces(places: List<PlaceEntity>)

    @Update
    suspend fun updatePlace(place: PlaceEntity)

    @Delete
    suspend fun deletePlace(place: PlaceEntity)

    @Query("DELETE FROM places WHERE id = :placeId")
    suspend fun deletePlaceById(placeId: Long)

    // === EXPORT ===

    @Query("SELECT * FROM places WHERE is_imported = 0")
    suspend fun getAllMyPlacesForExport(): List<PlaceEntity>

    // === IMPORT (anti-doublon base sur authorId + createdAt) ===

    @Query("SELECT COUNT(*) FROM places WHERE author_id = :authorId AND created_at = :createdAt")
    suspend fun placeExists(authorId: String, createdAt: Long): Int
}

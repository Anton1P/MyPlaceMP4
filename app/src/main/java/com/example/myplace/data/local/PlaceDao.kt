package com.example.myplace.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {

    // Récupérer tous les lieux (Flow pour mise à jour automatique de l'UI)
    @Query("SELECT * FROM places ORDER BY createdAt DESC")
    fun getAllPlaces(): Flow<List<PlaceEntity>>

    // Récupérer un lieu par son id
    @Query("SELECT * FROM places WHERE id = :id")
    suspend fun getPlaceById(id: Long): PlaceEntity?

    // Insérer un lieu
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: PlaceEntity): Long

    // Supprimer un lieu
    @Delete
    suspend fun deletePlace(place: PlaceEntity)

    // Vérifier si un lieu importé existe déjà (anti-doublon)
    // Un lieu importé est identifié par ses coordonnées + auteur + date
    @Query("SELECT COUNT(*) FROM places WHERE latitude = :lat AND longitude = :lon AND authorId = :authorId AND createdAt = :createdAt")
    suspend fun countDuplicate(lat: Double, lon: Double, authorId: String, createdAt: Long): Int
}
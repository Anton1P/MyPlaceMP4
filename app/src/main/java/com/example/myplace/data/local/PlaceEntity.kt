package com.example.myplace.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "places")
data class PlaceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Informations du lieu
    val title: String,
    val description: String,
    val emoji: String,

    // Coordonnées GPS
    val latitude: Double,
    val longitude: Double,

    // Adresse récupérée via reverse geocoding
    val address: String = "",

    // Photo — chemin vers le fichier interne (pas de Base64)
    val photoPath: String = "",

    // Date d'ajout (timestamp)
    val createdAt: Long = System.currentTimeMillis(),

    // Auteur — pour différencier mes lieux des lieux importés
    val authorId: String,
    val authorName: String,

    // True si ce lieu a été importé depuis un ami
    val isImported: Boolean = false
)
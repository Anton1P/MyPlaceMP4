package com.myplaces.app.util

import android.content.Context
import android.net.Uri
import com.google.gson.Gson
import com.myplaces.app.data.local.entity.PlaceEntity
import com.myplaces.app.data.repository.PlaceRepository

object JsonImporter {

    private val gson = Gson()

    suspend fun importFromUri(
        context: Context,
        uri: Uri,
        repository: PlaceRepository
    ): Int {
        return try {
            val json = context.contentResolver.openInputStream(uri)?.bufferedReader()?.readText()
                ?: return 0
            val exportData = gson.fromJson(json, ExportData::class.java)
            val places = exportData.places.map { p ->
                PlaceEntity(
                    title = p.title,
                    description = p.description,
                    latitude = p.latitude,
                    longitude = p.longitude,
                    address = p.address,
                    emoji = p.emoji,
                    photoPath = null,
                    authorId = p.author_id,
                    authorName = p.author_name,
                    createdAt = p.created_at,
                    isImported = true
                )
            }
            repository.importPlaces(places)
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }
}

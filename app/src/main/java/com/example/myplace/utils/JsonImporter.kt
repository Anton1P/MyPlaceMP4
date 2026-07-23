package com.example.myplace.utils

import android.content.Context
import android.net.Uri
import com.example.myplace.data.local.PlaceEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object JsonImporter {

    private val gson = Gson()

    fun importPlaces(context: Context, uri: Uri): List<PlaceEntity> {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val json = inputStream?.bufferedReader()?.readText() ?: ""
            inputStream?.close()

            val type = object : TypeToken<List<PlaceEntity>>() {}.type
            val places: List<PlaceEntity> = gson.fromJson(json, type)

            // Marquer tous les lieux importés comme isImported = true
            places.map { it.copy(isImported = true, id = 0) }

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
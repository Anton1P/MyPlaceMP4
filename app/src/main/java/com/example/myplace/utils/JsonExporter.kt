package com.example.myplace.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.myplace.data.local.PlaceEntity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File

object JsonExporter {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    fun exportPlaces(context: Context, places: List<PlaceEntity>) {
        try {
            // Sérialiser en JSON
            val json = gson.toJson(places)

            // Écrire dans le cache
            val file = File(context.cacheDir, "places_export.json")
            file.writeText(json)

            // Partager via Intent
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Exporter mes lieux"))

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
package com.myplaces.app.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.myplaces.app.data.local.entity.PlaceEntity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ExportData(
    val export_version: Int = 1,
    val author_id: String,
    val author_name: String,
    val exported_at: String,
    val places: List<PlaceExport>
)

data class PlaceExport(
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val emoji: String,
    val created_at: Long,
    val author_id: String,
    val author_name: String
)

object JsonExporter {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    fun exportPlaces(
        context: Context,
        places: List<PlaceEntity>,
        authorId: String,
        authorName: String
    ) {
        val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            .format(Date())

        val exportData = ExportData(
            author_id = authorId,
            author_name = authorName,
            exported_at = now,
            places = places.map { p ->
                PlaceExport(
                    title = p.title,
                    description = p.description,
                    latitude = p.latitude,
                    longitude = p.longitude,
                    address = p.address,
                    emoji = p.emoji,
                    created_at = p.createdAt,
                    author_id = p.authorId,
                    author_name = p.authorName
                )
            }
        )

        val json = gson.toJson(exportData)
        val cacheFile = File(context.cacheDir, "places_export.json")
        cacheFile.writeText(json)

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            cacheFile
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Mon journal My Places")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Partager mon journal"))
    }
}

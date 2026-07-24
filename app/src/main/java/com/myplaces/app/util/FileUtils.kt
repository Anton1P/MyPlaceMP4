package com.myplaces.app.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object FileUtils {

    /** Cree le dossier de photos internes si necessaire et retourne le File. */
    private fun getPhotosDir(context: Context): File {
        val dir = File(context.filesDir, "photos")
        if (!dir.exists() && !dir.mkdirs()) {
            // Log or handle the failure to create directory if needed
        }
        return dir
    }

    /** Cree un fichier vide dans le stockage interne avec un nom unique. */
    fun createPhotoFile(context: Context): File {
        val dir = getPhotosDir(context)
        return File(dir, "photo_${System.currentTimeMillis()}.jpg")
    }

    /** Copie un URI (galerie) vers le stockage interne. Retourne le chemin ou null. */
    fun copyUriToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val destFile = createPhotoFile(context)
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            destFile.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    /** Supprime un fichier photo du stockage interne. */
    fun deletePhoto(photoPath: String?) {
        if (!photoPath.isNullOrBlank()) {
            val file = File(photoPath)
            if (file.exists()) file.delete()
        }
    }
}

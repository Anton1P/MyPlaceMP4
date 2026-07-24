package com.myplaces.app.util

import android.content.Context
import android.net.Uri
import android.util.Base64
import java.io.File
import java.io.FileOutputStream

object FileUtils {

    /** Crée le dossier de photos internes si nécessaire et retourne le File. */
    private fun getPhotosDir(context: Context): File {
        val dir = File(context.filesDir, "photos")
        if (!dir.exists() && !dir.mkdirs()) {
            // Log or handle the failure to create directory if needed
        }
        return dir
    }

    /** Crée un fichier vide dans le stockage interne avec un nom unique. */
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

    /** Convertit un fichier physique en chaîne Base64 pour l'export JSON. */
    fun fileToBase64(filePath: String?): String? {
        if (filePath.isNullOrBlank()) return null
        return try {
            val file = File(filePath)
            if (file.exists()) {
                val bytes = file.readBytes()
                // On utilise NO_WRAP pour éviter les sauts de ligne dans le JSON
                Base64.encodeToString(bytes, Base64.NO_WRAP)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /** Convertit une chaîne Base64 en fichier physique et retourne son chemin absolu pour Room. */
    fun base64ToFile(context: Context, base64Str: String?): String? {
        if (base64Str.isNullOrBlank()) return null
        return try {
            val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
            val destFile = createPhotoFile(context)
            FileOutputStream(destFile).use { output ->
                output.write(decodedBytes)
            }
            destFile.absolutePath // On retourne bien le chemin d'accès sous forme de String !
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
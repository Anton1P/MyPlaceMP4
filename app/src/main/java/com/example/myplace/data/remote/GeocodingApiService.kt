package com.example.myplace.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

// Réponse de l'API adresse.data.gouv.fr
data class GeocodingResponse(
    val features: List<Feature>
)

data class Feature(
    val properties: Properties
)

data class Properties(
    val label: String = "",
    val city: String = "",
    val postcode: String = ""
)

interface GeocodingApiService {

    companion object {
        const val BASE_URL = "https://api-adresse.data.gouv.fr/"
    }

    // Reverse geocoding : coordonnées → adresse
    @GET("reverse/")
    suspend fun reverseGeocode(
        @Query("lon") longitude: Double,
        @Query("lat") latitude: Double
    ): GeocodingResponse
}
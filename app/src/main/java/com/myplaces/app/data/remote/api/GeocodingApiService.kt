package com.myplaces.app.data.remote.api

import com.myplaces.app.data.remote.model.GeocodingResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApiService {

    @GET("reverse/")
    suspend fun reverseGeocode(
        @Query("lon") longitude: Double,
        @Query("lat") latitude: Double
    ): GeocodingResponse

    companion object {
        const val BASE_URL = "https://api-adresse.data.gouv.fr/"
    }
}

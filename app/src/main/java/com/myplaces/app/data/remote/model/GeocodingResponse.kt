package com.myplaces.app.data.remote.model

data class GeocodingResponse(
    val features: List<Feature>
)

data class Feature(
    val properties: Properties
)

data class Properties(
    val label: String?,
    val housenumber: String?,
    val street: String?,
    val postcode: String?,
    val city: String?,
    val context: String?
)

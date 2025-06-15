package com.tontowi0086.assesment3.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Barang(
    @Json(name = "id") val id: Long,
    @Json(name = "nama") val nama: String,
    @Json(name = "deskripsi") val deskripsi: String,
    @Json(name = "gambar") val gambar: String,
    @Json(name = "createdAt") val created_at: String,
    @Json(name = "updatedAt") val updated_at: String
)
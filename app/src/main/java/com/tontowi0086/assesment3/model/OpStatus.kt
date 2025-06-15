package com.tontowi0086.assesment3.model
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OpStatus(val message: String)
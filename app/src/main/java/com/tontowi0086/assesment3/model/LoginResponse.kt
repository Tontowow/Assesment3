package com.tontowi0086.assesment3.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    val message: String,
    val user: User,
    val token: String
)

@JsonClass(generateAdapter = true)
data class User(
    val id: Long,
    val googleId: String,
    val displayName: String,
    val email: String
)
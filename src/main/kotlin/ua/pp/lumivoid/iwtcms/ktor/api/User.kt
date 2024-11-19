package ua.pp.lumivoid.iwtcms.ktor.api

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val username: String,
    val password: String,
    val permits: Map<String, Boolean>
)
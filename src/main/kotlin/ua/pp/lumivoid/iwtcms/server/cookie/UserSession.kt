package ua.pp.lumivoid.iwtcms.server.cookie

import kotlinx.serialization.Serializable

@Serializable
data class UserSession(
    val name: String,
    val id: String,
)

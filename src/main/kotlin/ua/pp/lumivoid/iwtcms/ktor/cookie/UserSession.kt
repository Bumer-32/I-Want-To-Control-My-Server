package ua.pp.lumivoid.iwtcms.ktor.cookie

import kotlinx.serialization.Serializable

@Serializable
data class UserSession(val id: String)
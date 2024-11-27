package ua.pp.lumivoid.iwtcms.util

import kotlinx.serialization.Serializable
import ua.pp.lumivoid.iwtcms.ktor.api.User

@Serializable
data class ConfigData(
    val ip: String,
    val port: Int,
    val logLevel: String,
    val useSSL: Boolean,
    val customSertificate: Boolean,
    val sslAlias: String,
    val sslPass: String,
    val useAuthentication: Boolean,
    val users: List<User>
)

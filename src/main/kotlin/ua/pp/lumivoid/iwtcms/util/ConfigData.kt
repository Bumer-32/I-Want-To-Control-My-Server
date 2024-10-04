package ua.pp.lumivoid.iwtcms.util

import kotlinx.serialization.Serializable

@Serializable
data class ConfigData(
    val ip: String,
    val port: Int,
    val logLevel: String,
    val useSSL: Boolean,
    val useAuthentication: Boolean,
    val authenticationPassword: String
)

package ua.pp.lumivoid.util

import kotlinx.serialization.Serializable

@Serializable
data class ConfigData(val ip: String, val port: Int, val logLevel: Int)

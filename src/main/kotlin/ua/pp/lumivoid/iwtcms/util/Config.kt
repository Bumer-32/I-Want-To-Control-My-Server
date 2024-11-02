package ua.pp.lumivoid.iwtcms.util

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ua.pp.lumivoid.iwtcms.Constants
import java.io.File

object Config {
    private val logger = Constants.LOGGER
    private val json = Json { prettyPrint = true }
    private var cachedConfig: ConfigData? = null

    init {
        if (!File(Constants.CONFIG_FOLDER).exists()) File(Constants.CONFIG_FOLDER).mkdirs()
        val configFile = File(Constants.CONFIG_FILE)
        if (!configFile.exists()) configFile.createNewFile()
    }

    fun readConfig(): ConfigData {
        if (cachedConfig != null) return cachedConfig!!

        val file = File(Constants.CONFIG_FILE)
        if (file.exists()) {
            val jsonData = file.readText()
            try {
                return json.decodeFromString<ConfigData>(jsonData)
            } catch (e: RuntimeException) {
                logger.error("Error while reading config file, rewriting")
                e.stackTrace.forEach { logger.error(it.toString()) }
            }
        }

        val data = ConfigData(ip = "127.0.0.1", port = 25566, logLevel = "INFO", useSSL = false, useAuthentication = false, authenticationPassword = "")
        writeConfig(data)

        return data
    }

    fun writeConfig(data: ConfigData) {
        cachedConfig = data
        val file = File(Constants.CONFIG_FILE)
        file.writeText(json.encodeToString(data))
    }
}
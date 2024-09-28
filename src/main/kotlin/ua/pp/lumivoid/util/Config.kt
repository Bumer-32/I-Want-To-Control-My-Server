package ua.pp.lumivoid.util

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.fabricmc.loader.api.FabricLoader
import ua.pp.lumivoid.Constants
import java.io.File

object Config {
    private val logger = Constants.LOGGER
    private val json = Json { prettyPrint = true }
    private var cachedConfig: ConfigData? = null

    init {
        if (!File(FabricLoader.getInstance().configDir.toString().toString()).exists()) File(FabricLoader.getInstance().configDir.toString().toString()).mkdirs()
        val configFile = File(FabricLoader.getInstance().configDir.toString().toString() + "\\iwtcms.json")
        if (!configFile.exists()) configFile.createNewFile()
    }

    fun readConfig(): ConfigData {
        if (cachedConfig != null) return cachedConfig!!

        val file = File(FabricLoader.getInstance().configDir.toString() + "\\iwtcms.json")
        if (file.exists()) {
            val jsonData = file.readText()
            try {
                return json.decodeFromString<ConfigData>(jsonData)
            } catch (e: RuntimeException) {
                logger.error("Error while reading config file, rewriting")
                e.stackTrace.forEach { logger.error(it.toString()) }
            }
        }

        val data = ConfigData(ip = "127.0.0.1", port = 25566, logLevel = 3)
        writeConfig(data)

        return data
    }

    fun writeConfig(data: ConfigData) {
        cachedConfig = data
        val file = File(FabricLoader.getInstance().configDir.toString() + "\\iwtcms.json")
        file.writeText(json.encodeToString(data))
    }
}
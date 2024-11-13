package ua.pp.lumivoid.iwtcms.util

import com.typesafe.config.ConfigException
import com.typesafe.config.ConfigFactory
import ua.pp.lumivoid.iwtcms.Constants
import java.io.File

object Config {
    private val logger = Constants.LOGGER
    private var cachedConfig: ConfigData? = null
    private val defaultConfig = this.javaClass.getResource(Constants.CONFIG_FILE.replace(Constants.CONFIG_FOLDER, ""))!!
    private val configFile = File(Constants.CONFIG_FILE)

    init {
        if (!File(Constants.CONFIG_FOLDER).exists()) File(Constants.CONFIG_FOLDER).mkdirs()

        if (!configFile.exists()) {
            configFile.writeText(defaultConfig.readText(), Charsets.UTF_8)
        }
    }

    @Suppress("DuplicatedCode")
    fun readConfig(): ConfigData {
        if (cachedConfig != null) return cachedConfig!!

        try {
            val config = ConfigFactory.parseFile(configFile)
            val data = ConfigData(
                ip = config.getString("server.ip"),
                port = config.getInt("server.port"),
                logLevel = config.getString("server.logLevel"),
                useSSL = config.getBoolean("ssl.useSSL"),
                sslAlias = config.getString("ssl.sslAlias"),
                sslPass = config.getString("ssl.sslPass"),
                useAuthentication = config.getBoolean("auth.useAuthentication"),
                authenticationPassword = config.getString("auth.authenticationPassword")
            )

            cachedConfig = data
            return data
        } catch (e: ConfigException) {
            logger.error("Error while reading config file: ${e.message}")
            logger.error("Using default config")

            val config = ConfigFactory.parseFile(File(defaultConfig.file))
            val data = ConfigData(
                ip = config.getString("server.ip"),
                port = config.getInt("server.port"),
                logLevel = config.getString("server.logLevel"),
                useSSL = config.getBoolean("ssl.useSSL"),
                sslAlias = config.getString("ssl.sslAlias"),
                sslPass = config.getString("ssl.sslPass"),
                useAuthentication = config.getBoolean("auth.useAuthentication"),
                authenticationPassword = config.getString("auth.authenticationPassword")
            )

            cachedConfig = data
            return data
        }
    }
}
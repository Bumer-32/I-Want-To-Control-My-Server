package ua.pp.lumivoid.iwtcms.server.util

import com.typesafe.config.Config
import com.typesafe.config.ConfigException
import com.typesafe.config.ConfigFactory
import kotlinx.serialization.Serializable
import ua.pp.lumivoid.iwtcms.Constants
import java.io.File
import kotlin.system.exitProcess

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

    fun readConfig(): ConfigData {
        if (cachedConfig != null) return cachedConfig!!

        try {
            val config = ConfigFactory.parseFile(configFile)
            val data = createConfigData(config)

            cachedConfig = data
            return data
        } catch (e: ConfigException) {
            ErrorMessages.printStackTrace(logger, e)
            ErrorMessages.BAD_CONFIG.launch(logger)
            exitProcess(1)
        }
    }

    private fun createConfigData(config: Config): ConfigData {
        try {
            return ConfigData(
                ip = config.getString("server.ip"),
                port = config.getInt("server.port"),
                logLevel = config.getString("server.log Level"),
                databaseUser = config.getString("server.database user"),
                databasePassword = config.getString("server.database password"),
                useSSL = config.getBoolean("ssl.use SSL"),
                customCertificate = config.getBoolean("ssl.custom Certificate"),
                sslAlias = config.getString("ssl.ssl Alias"),
                sslPass = config.getString("ssl.ssl Pass"),
                statisticsPeriod = config.getInt("stuff.statistics period"),
                playerInfoPeriod = config.getInt("stuff.player info period"),
                enableIWTCMSControlPanel = config.getBoolean("web.enable IWTCMS control panel"),
                autoOpenIWTCMSPageOnStartup = config.getBoolean("web.auto open IWTCMS page on startup"),

                // dev
                devMode = config.getBooleanOrDefault("dev.dev mode", false),
                autoOpenVite = config.getBooleanOrDefault("dev.auto open vite", false),
                enableH2WebServer = config.getBooleanOrDefault("dev.enable h2 web server", false),
                useExternalDb = config.getBooleanOrDefault("dev.use external db", false),
                externalDbDriver = DbDriver.entries.find { config.getStringOrDefault("dev.external db driver", "MariaDB") == it.named } ?: DbDriver.MariaDB,
                externalDbIWTCMSName = config.getStringOrDefault("external db iwtcms name", "iwtcms"),
                externalDbIp = config.getStringOrDefault("dev.external db ip", "localhost"),
                externalDbPort = config.getIntOrDefault("dev.external db port", 9092),
            )
        } catch (e: ConfigException) {

            ErrorMessages.printStackTrace(logger, e)
            ErrorMessages.BAD_CONFIG.launch(logger)
            exitProcess(1)
        }
    }

    private fun Config.getBooleanOrDefault(key: String, default: Boolean): Boolean {
        return if (this.hasPath(key)) this.getBoolean(key) else default
    }
    private fun Config.getStringOrDefault(key: String, default: String): String {
        return if (this.hasPath(key)) this.getString(key) else default
    }
    private fun Config.getIntOrDefault(key: String, default: Int): Int {
        return if (this.hasPath(key)) this.getInt(key) else default
    }

    @Serializable
    data class ConfigData(
        val ip: String,
        val port: Int,
        val logLevel: String,
        val databaseUser: String,
        val databasePassword: String,
        val useSSL: Boolean,
        val customCertificate: Boolean,
        val sslAlias: String,
        val sslPass: String,
        val statisticsPeriod: Int,
        val playerInfoPeriod: Int,
        val enableIWTCMSControlPanel: Boolean,
        val autoOpenIWTCMSPageOnStartup: Boolean,

        val devMode: Boolean,
        val autoOpenVite: Boolean,
        val enableH2WebServer: Boolean,
        val useExternalDb: Boolean,
        val externalDbDriver: DbDriver,
        val externalDbIWTCMSName: String,
        val externalDbIp: String,
        val externalDbPort: Int,
    )

    enum class DbDriver(val named: String, val driver: String, val url: String) {
        Oracle("Oracle", "oracle", "oracle"),
        //    H2("H2", "h2"), // in some reason can't connect by tcp and gives error
        MariaDB("MariaDB", "mariadb", "mariadb"),
        MSSQL("MSSQL", "sqlserver", "mssql"),
        MYSQL("MySQL", "mysql", "mysql"),
        POSTGRESQL("PostgreSQL", "postgresql", "postgresql"),
    }
}
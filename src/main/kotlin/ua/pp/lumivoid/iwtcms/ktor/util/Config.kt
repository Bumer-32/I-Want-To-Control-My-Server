package ua.pp.lumivoid.iwtcms.ktor.util

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
            val data = createConfigData(config)!!

            cachedConfig = data
            return data
        } catch (e: ConfigException) {
            badConfig(e)
            return createConfigData(ConfigFactory.empty())!! // never be launched
        }
    }

    private fun createConfigData(config: Config): ConfigData? {
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
                enableIWTCMSControlPanel = config.getBoolean("web.enable IWTCMS control panel"),
                autoOpenIWTCMSPageOnStartup = config.getBoolean("web.auto open IWTCMS page on startup"),
                devMode = config.getBoolean("dev.dev mode"),
                autoOpenVite = config.getBoolean("dev.auto open vite"),
                enableH2WebServer = config.getBoolean("dev.enable h2 web server"),
                useExternalH2Db = config.getBoolean("dev.use external h2 db"),
                externalH2DbIp = config.getString("dev.external h2 db ip"),
                externalH2DbPort = config.getInt("dev.external h2 db port"),
            )
        } catch (e: ConfigException) {
            badConfig(e)
            return null
        }
    }

    private fun badConfig(e: ConfigException? = null) {
        logger.error("Error while reading config file: ${e?.message}")

        logger.error("###########################################################################################")

        logger.info("Renaming config file to ${Constants.CONFIG_FILE}-BAD")
        if (File("${Constants.CONFIG_FILE}-BAD").exists()) File(Constants.CONFIG_FILE).delete()
        File(Constants.CONFIG_FILE).renameTo(File("${Constants.CONFIG_FILE}-BAD"))

        logger.info("Generating new config file")
        if (File(Constants.CONFIG_FILE).exists()) File(Constants.CONFIG_FILE).delete()
        File(Constants.CONFIG_FILE).writeText(defaultConfig.readText(), Charsets.UTF_8)

        if (e != null) ErrorMessages.printStackTrace(logger, e)

        ErrorMessages.BAD_CONFIG.launch(logger)

        exitProcess(1)
    }
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
    val enableIWTCMSControlPanel: Boolean,
    val autoOpenIWTCMSPageOnStartup: Boolean,
    val devMode: Boolean,
    val autoOpenVite: Boolean,
    val enableH2WebServer: Boolean,
    val useExternalH2Db: Boolean,
    val externalH2DbIp: String,
    val externalH2DbPort: Int,
)

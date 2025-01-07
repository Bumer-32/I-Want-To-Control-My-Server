package ua.pp.lumivoid.iwtcms.util

import com.typesafe.config.Config
import com.typesafe.config.ConfigException
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigObject
import kotlinx.serialization.Serializable
import org.apache.commons.codec.digest.DigestUtils
import ua.pp.lumivoid.iwtcms.Constants
import ua.pp.lumivoid.iwtcms.ktor.api.User
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
            return badConfig(e)
        }
    }

    private fun createConfigData(config: Config): ConfigData {
        try {
            val users: MutableList<User> = mutableListOf()

            config.getList("auth.users").forEach { configUser ->
                val user = (configUser as ConfigObject).toConfig()

                val username = user.getString("name")
                val password = if (user.hasPath("password") && user.getString("password").isNotEmpty()) {
                    user.getString("password")
                } else {
                    null
                }


                val permits: MutableMap<String, Boolean> = user.getConfig("permits").entrySet().associate {
                    it.key.replace("\"", "") to it.value.unwrapped() as Boolean
                } as MutableMap<String, Boolean>

                println(permits)

                val id: String = DigestUtils.sha256Hex((username + password.toString()))

                val newUser = User(id, username, password, permits)
                users.add(newUser)
            }

            return ConfigData(
                ip = config.getString("server.ip"),
                port = config.getInt("server.port"),
                logLevel = config.getString("server.log Level"),
                useSSL = config.getBoolean("ssl.use SSL"),
                customSertificate = config.getBoolean("ssl.custom Sertificate"),
                sslAlias = config.getString("ssl.ssl Alias"),
                sslPass = config.getString("ssl.ssl Pass"),
                statisticsPeriod = config.getInt("stuff.statistics period"),
                enableIWTCMSControlPanel = config.getBoolean("web.enable IWTCMS control panel"),
                useAuthentication = config.getBoolean("auth.use Authentication"),
                users = users,
                devMode = config.getBoolean("dev.dev mode"),
            )
        } catch (e: ConfigException) {
            return badConfig(e)
        }
    }

    private fun badConfig(e: ConfigException? = null): ConfigData {
        logger.error("Error while reading config file: ${e?.message}")
        logger.error("STOPPING SERVER BECAUSE IT CAN CAUSE SECURITY ISSUES")

        logger.error("###########################################################################################")

        logger.info("Renaming config file to ${Constants.CONFIG_FILE}-BAD")
        if (File("${Constants.CONFIG_FILE}-BAD").exists()) File(Constants.CONFIG_FILE).delete()
        File(Constants.CONFIG_FILE).renameTo(File("${Constants.CONFIG_FILE}-BAD"))

        logger.info("Generating new config file")
        if (File(Constants.CONFIG_FILE).exists()) File(Constants.CONFIG_FILE).delete()
        File(Constants.CONFIG_FILE).writeText(defaultConfig.readText(), Charsets.UTF_8)

        logger.error("###########################################################################################")

        logger.error("")
        logger.error("")
        logger.error("")
        logger.error("")
        logger.error("")

        logger.error("+-----------------------------------------------------------------------------------------+")
        logger.error("|                                                                                         |")
        logger.error("|                       IWTCMS STOPPED SERVER FOR SECURITY REASONS                        |")
        logger.error("|                              ERROR: CAN'T READ CONFIG FILE                              |")
        logger.error("|                        FIX CONFIG FILE AND THEN RESTART SERVER                          |")
        logger.error("|                                                                                         |")
        logger.error("|                     OLD CONFIG FILE RENAMED WITH *-BAD SUFFIX                           |")
        logger.error("|                              NEW CONFIG MUST BE GENERATED                               |")
        logger.error("|                                                                                         |")
        logger.error("|                IF YOU FIXED THE CONFIG FILE BUT STILL GET THIS ERROR,                   |")
        logger.error("|                          CONTACT THE DEVELOPERS ON GITHUB                               |")
        logger.error("|                                                                                         |")
        logger.error("|                          IWTCMS CARES ABOUT YOUR SECURITY!                              |")
        logger.error("|                                                                                         |")
        logger.error("+-----------------------------------------------------------------------------------------+")

        exitProcess(1) // STOP

        return createConfigData(ConfigFactory.parseFile(File(defaultConfig.file))) // it never will be launched
    }
}

data class TESDFSDFS(val s: String,  val i: Boolean)

@Serializable
data class ConfigData(
    val ip: String,
    val port: Int,
    val logLevel: String,
    val useSSL: Boolean,
    val customSertificate: Boolean,
    val sslAlias: String,
    val sslPass: String,
    val statisticsPeriod: Int,
    val enableIWTCMSControlPanel: Boolean,
    val useAuthentication: Boolean,
    val users: List<User>,
    val devMode: Boolean,
)

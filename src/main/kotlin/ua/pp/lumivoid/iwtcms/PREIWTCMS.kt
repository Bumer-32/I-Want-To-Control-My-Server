package ua.pp.lumivoid.iwtcms

import kotlinx.coroutines.runBlocking
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint
import org.apache.commons.codec.digest.DigestUtils
import org.jetbrains.exposed.v1.core.StdOutSqlLogger
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.SchemaUtils
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.slf4j.LoggerFactory
import ua.pp.lumivoid.iwtcms.ktor.KtorServer
import ua.pp.lumivoid.iwtcms.ktor.tables.UserPermissionsTable
import ua.pp.lumivoid.iwtcms.ktor.tables.UsersTable
import ua.pp.lumivoid.iwtcms.ktor.util.Config
import ua.pp.lumivoid.iwtcms.ktor.util.ErrorMessages
import ua.pp.lumivoid.iwtcms.util.CustomLogger
import java.io.File
import java.util.TimeZone
import kotlin.system.exitProcess

object PREIWTCMS : PreLaunchEntrypoint {
    private val logger = LoggerFactory.getLogger("iwtcms pre launch")

    override fun onPreLaunch() {
        logger.info("Initialize pre launch iwtcms")

        if (TimeZone.getDefault().id == "Europe/Kiev") TimeZone.setDefault(TimeZone.getTimeZone("Europe/Kyiv")) // shit coded thing only because kyiv not kiev for postgres

        val config = Config.readConfig()

        if (config.devMode) {
            if (File("${Constants.CONFIG_FOLDER}/../../gradlew.bat").exists()) { // Check is mod launched in "developer environment"
                ErrorMessages.DEV_MODE.launch(logger)
            } else {
                ErrorMessages.NOT_A_DEVELOPER.launch(logger)
                exitProcess(1)
            }
        }

        CustomLogger.setup()

        val dbUrl: String = if (config.useExternalDb) {
            "${config.externalDbDriver.url}://${config.externalDbIp}:${config.externalDbPort}/${config.externalDbIWTCMSName}"
        } else {
            "h2:file:///${Constants.DB_FILE.replace("\\", "/")}"
        }
        val dbDriver = if (config.useExternalDb) config.externalDbDriver.driver else "h2"

        logger.info("Connecting to DB r2dbc:$dbUrl with driver: $dbDriver")

        R2dbcDatabase.connect(
            url = "r2dbc:$dbUrl",
            driver = dbDriver,
            user = config.databaseUser,
            password = config.databasePassword,
        )

        runBlocking { suspendTransaction {
            if (config.devMode) addLogger(StdOutSqlLogger)

            SchemaUtils.create(
                UsersTable,
                UserPermissionsTable,
            )

            if (UsersTable.selectAll().empty()) {
                UsersTable.insert {
                    it[username] = "admin"
                    it[passwordHash] = DigestUtils.sha256Hex("iwtcms" + "ySXBvMifqXULEm1uRKP91ctmL6tCwCMi").toString()
                    it[salt] = "ySXBvMifqXULEm1uRKP91ctmL6tCwCMi"
                    it[uniqueId] = DigestUtils.sha256Hex("admin+iwtcms").toString()
                    it[admin] = true
                }
            }
        }}

        KtorServer.setup()
    }
}

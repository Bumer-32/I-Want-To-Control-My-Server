package ua.pp.lumivoid.iwtcms

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint
import org.apache.commons.codec.digest.DigestUtils
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import ua.pp.lumivoid.iwtcms.ktor.KtorServer
import ua.pp.lumivoid.iwtcms.ktor.tables.UserPermissions
import ua.pp.lumivoid.iwtcms.ktor.tables.Users
import ua.pp.lumivoid.iwtcms.ktor.util.Config
import ua.pp.lumivoid.iwtcms.ktor.util.ErrorMessages
import ua.pp.lumivoid.iwtcms.util.CustomLogger
import java.io.File
import kotlin.system.exitProcess

object PREIWTCMS : PreLaunchEntrypoint {
    private val logger = LoggerFactory.getLogger("iwtcms pre launch")

    override fun onPreLaunch() {
        logger.info("Initialize pre launch iwtcms")

        if (Config.readConfig().devMode) {
            if (File("${Constants.CONFIG_FOLDER}/../../gradlew.bat").exists()) { // Check is mod launched in "developer environment"
                ErrorMessages.DEV_MODE.launch(logger)
            } else {
                ErrorMessages.NOT_A_DEVELOPER.launch(logger)
                exitProcess(1)
            }
        }

        CustomLogger.setup()

        val dbUrl: String = if (Config.readConfig().devMode && Config.readConfig().useExternalH2Db) {
            "tcp://${Config.readConfig().externalH2DbIp}:${Config.readConfig().externalH2DbPort}/iwtcms"
        } else {
            Constants.DB_FILE
        }

        logger.info("Connecting to DB ${dbUrl}")

        Database.connect(
            url = "jdbc:h2:$dbUrl",
            driver = "org.h2.Driver",
            user = Config.readConfig().databaseUser,
            password = Config.readConfig().databasePassword,
        )
        transaction {
            if (Config.readConfig().devMode) addLogger(StdOutSqlLogger)
            create(
                Users,
                UserPermissions,
            )

            if (Users.selectAll().empty()) {
                Users.insert {
                    it[username] = "admin"
                    it[passwordHash] = DigestUtils.sha256Hex("iwtcms" + "ySXBvMifqXULEm1uRKP91ctmL6tCwCMi").toString()
                    it[salt] = "ySXBvMifqXULEm1uRKP91ctmL6tCwCMi"
                    it[uniqueId] = DigestUtils.sha256Hex("admin+iwtcms").toString()
                    it[admin] = true
                }
            }
        }

        KtorServer.setup()
    }
}

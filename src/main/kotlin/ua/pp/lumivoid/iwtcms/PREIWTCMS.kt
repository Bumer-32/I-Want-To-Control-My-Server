package ua.pp.lumivoid.iwtcms

import kotlinx.coroutines.runBlocking
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint
import org.jetbrains.exposed.v1.core.StdOutSqlLogger
import org.jetbrains.exposed.v1.r2dbc.*
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.slf4j.LoggerFactory
import ua.pp.lumivoid.iwtcms.server.KtorServer
import ua.pp.lumivoid.iwtcms.server.api.requests.api.user.CreateUser
import ua.pp.lumivoid.iwtcms.server.tables.MetaTable
import ua.pp.lumivoid.iwtcms.server.tables.UserPermissionsTable
import ua.pp.lumivoid.iwtcms.server.tables.UsersTable
import ua.pp.lumivoid.iwtcms.server.util.Config
import ua.pp.lumivoid.iwtcms.server.util.ErrorMessages
import ua.pp.lumivoid.iwtcms.util.CustomLogger
import java.io.File
import java.util.*
import kotlin.system.exitProcess
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

object PREIWTCMS : PreLaunchEntrypoint {
    private val logger = LoggerFactory.getLogger("iwtcms pre launch")

    @OptIn(ExperimentalTime::class)
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

        runBlocking {
            suspendTransaction {
                if (config.devMode) addLogger(StdOutSqlLogger)

                SchemaUtils.create(
                    UsersTable,
                    UserPermissionsTable,
                    MetaTable
                )

                MetaTable.insertIgnore { it[key] = "iwtcms"; it[value] = "iwtcms" } // just why no?
                MetaTable.insertIgnore { it[key] = "schema_version"; it[value] = Constants.SCHEMA_VERSION }
                MetaTable.insertIgnore { it[key] = "last_migration_at"; it[value] = "unknown" }
                MetaTable.insertIgnore { it[key] = "last_migration_from"; it[value] = "unknown" }
                MetaTable.insertIgnore { it[key] = "last_migration_to"; it[value] = "unknown" }
                MetaTable.insertIgnore { it[key] = "last_migration_success"; it[value] = "unknown" }
                MetaTable.insertIgnore { it[key] = "total_migrations"; it[value] = "0" }
                MetaTable.upsert { it[key] = "last_used_at"; it[value] = Instant.fromEpochMilliseconds(System.currentTimeMillis()).toString() }
                MetaTable.upsert { it[key] = "last_used_by"; it[value] = "iwtcms" } // for easy debug (if used not by iwtcms must be different, for e.g. iwtcms forks)
                MetaTable.insertIgnore { it[key] = "last_shutdown_at"; it[value] = "unknown" }
                MetaTable.insertIgnore { it[key] = "initial_iwtcms_version"; it[value] = Constants.MOD_VERSION }
                MetaTable.upsert { it[key] = "last_iwtcms_version"; it[value] = Constants.MOD_VERSION }

                if (UsersTable.selectAll().empty()) CreateUser.create("admin", "iwtcms", true, emptyList())
            }
        }

        KtorServer.setup()
    }
}

package ua.pp.lumivoid.iwtcms.server

import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insertIgnore
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.upsert
import ua.pp.lumivoid.iwtcms.server.api.requests.api.user.CreateUser
import ua.pp.lumivoid.iwtcms.server.tables.*
import ua.pp.lumivoid.iwtcms.server.util.Config
import java.io.File
import kotlin.time.Instant

/**
 * This object used just to generate a database file in generateDbFile task
 * Used in actions to upload db copy to artifacts for future migrations testing
 */
object DbSchemaGenerator {
    @JvmStatic
    @Suppress("DuplicatedCode")
    fun main(args: Array<String>) {
        File("./build/db.mv.db").delete()
        File("./build/db.trace.db").delete()

        Database.connect(
            url = "jdbc:h2:file:./build/db;MODE=MYSQL",
            driver = Config.DbDriver.H2.driver
        )

        transaction {
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
            MetaTable.upsert { it[key] = "last_used_by"; it[value] = "iwtcms-test" } // for easy debug (if used not by iwtcms must be different, for e.g. iwtcms forks)
            MetaTable.insertIgnore { it[key] = "last_shutdown_at"; it[value] = "unknown" }
            MetaTable.insertIgnore { it[key] = "initial_iwtcms_version"; it[value] = Constants.MOD_VERSION }
            MetaTable.upsert { it[key] = "last_iwtcms_version"; it[value] = Constants.MOD_VERSION }

            runBlocking { CreateUser.create("admin", "iwtcms", true, emptyList()) }
            UserPermissionEntity.new {
                this.permissionName = "test"
                this.user = UserEntity.all().single()
            }
        }
    }
}
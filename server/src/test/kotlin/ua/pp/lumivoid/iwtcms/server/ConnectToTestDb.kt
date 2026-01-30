package ua.pp.lumivoid.iwtcms.server

import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.core.StdOutSqlLogger
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ua.pp.lumivoid.iwtcms.server.api.requests.api.user.CreateUser
import ua.pp.lumivoid.iwtcms.server.tables.UserPermissionsTable
import ua.pp.lumivoid.iwtcms.server.tables.UsersTable

fun connectToTestDb() {
    val url = "jdbc:h2:mem:///test;MODE=MYSQL;DB_CLOSE_DELAY=-1"

    Database.connect(url = url, driver = "h2")

    transaction {
        addLogger(StdOutSqlLogger)

        SchemaUtils.create(
            UsersTable,
            UserPermissionsTable
        )
    }

    runBlocking { CreateUser.create("admin", "iwtcms", true, emptyList()) }
}

package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ua.pp.lumivoid.iwtcms.ktor.api.doAuth
import ua.pp.lumivoid.iwtcms.ktor.tables.UserPermissionsTable
import ua.pp.lumivoid.iwtcms.ktor.tables.UsersTable

object DeleteUser : Request() {
    override val path = "/api/deleteUser"

    override val request: Routing.() -> Unit = {
        post(path) {
            val payload = call.receive<DeleteUserPayload>()

            doAuth(
                call = call,
                permission = "users.manage",
                success = {
                    transaction {
                        val userId: Int = try {
                            UsersTable
                                .selectAll()
                                .where { UsersTable.username eq payload.username }
                                .first()[UsersTable.id]
                        } catch (_: NoSuchElementException) {
                            runBlocking { call.respondText("User not found", status = HttpStatusCode.NotFound) }
                            return@transaction
                        }

                        UsersTable.deleteWhere { UsersTable.username eq payload.username }
                        UserPermissionsTable.deleteWhere { UserPermissionsTable.userId eq userId }
                        runBlocking { call.respondText("User deleted") }
                    }
                },
            )
        }
    }

    @Serializable
    data class DeleteUserPayload(
        val username: String,
    )
}
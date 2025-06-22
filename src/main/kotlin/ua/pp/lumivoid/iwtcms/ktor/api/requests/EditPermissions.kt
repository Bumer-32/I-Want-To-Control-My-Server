package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import ua.pp.lumivoid.iwtcms.ktor.api.doAuth
import ua.pp.lumivoid.iwtcms.ktor.tables.UserPermissions
import ua.pp.lumivoid.iwtcms.ktor.tables.Users

object EditPermissions : Request() {
    override val path = "/api/editPermissions"

    override val request: Routing.() -> Unit = {
        post(path) {
            val payload = call.receive<EditPermissionPayload>()

            doAuth(
                call = call,
                permission = "edit users",
                success = {
                    transaction {
                        val userId: Int = try {
                            Users
                                .selectAll()
                                .where { Users.username eq payload.username }
                                .first()[Users.id]
                        } catch (_: NoSuchElementException) {
                            runBlocking { call.respondText("User not found", status = HttpStatusCode.NotFound) }
                            return@transaction
                        }

                        try {
                            payload.permissions.forEach { (key, value) ->
                                UserPermissions.update({ (UserPermissions.userId eq userId) and (UserPermissions.permissionName eq key) }) {
                                    it[UserPermissions.permissionState] = value
                                }
                            }

                            runBlocking { call.respondText("User permissions updated", status = HttpStatusCode.OK) }
                        } catch (_: NoSuchElementException) {
                            runBlocking { call.respondText("Permission not found", status = HttpStatusCode.NotFound) }
                        }
                    }
                }
            )
        }
    }
}

@Serializable
data class EditPermissionPayload(
    val username: String,
    val permissions: Map<String, Boolean>,
)

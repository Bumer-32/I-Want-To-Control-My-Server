package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import org.apache.commons.codec.digest.DigestUtils
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import ua.pp.lumivoid.iwtcms.ktor.api.PermissionsList
import ua.pp.lumivoid.iwtcms.ktor.api.doAuth
import ua.pp.lumivoid.iwtcms.ktor.tables.UserPermissionsTable
import ua.pp.lumivoid.iwtcms.ktor.tables.UsersTable

object CreateUser : Request() {
    override val path = "/api/createUser"

    override val request: Routing.() -> Unit = {
        post(path) {
            val payload = call.receive<CreateUserPayload>()

            doAuth(
                call = call,
                permission = PermissionsList.Permission.USERS_MANAGE.value,
                success = {
                    val success = suspendTransaction  {
                        var salt = generateSequence { genSalt() }
                            .first { saltCandidate ->
                                UsersTable.selectAll().where(UsersTable.salt eq saltCandidate).empty()
                            }

                        runCatching {
                            UsersTable.insert {
                                it[UsersTable.username] = payload.username
                                it[UsersTable.passwordHash] = DigestUtils.sha256Hex(payload.password + salt)
                                it[UsersTable.salt] = salt
                                it[UsersTable.uniqueId] = DigestUtils.sha256Hex("${payload.username}+${payload.password}+${salt}")
                                it[UsersTable.admin] = payload.admin
                            }
                        }.onFailure {
                            return@suspendTransaction false
                        }

                        payload.permissions.forEach { (key, value) ->
                            if (key in PermissionsList.getPermissionsList()) {
                                runCatching {
                                    val userId = UsersTable.selectAll().where{ UsersTable.username eq payload.username }.first()[UsersTable.id]
                                    UserPermissionsTable.insert {
                                        it[UserPermissionsTable.permissionName] = key
                                        it[UserPermissionsTable.permissionState] = value
                                        it[UserPermissionsTable.userId] = userId
                                    }
                                }
                            }
                        }

                        true
                    }

                    if (success) call.respond("User created")
                    else call.respond(HttpStatusCode.Conflict, "User already exists")
                },
            )
        }
    }

    fun genSalt(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..32)
            .map { allowedChars.random() }
            .joinToString("")
    }

    @Serializable
    data class CreateUserPayload(
        val username: String,
        val password: String,
        val admin: Boolean,
        val permissions: Map<String, Boolean>,
    )
}
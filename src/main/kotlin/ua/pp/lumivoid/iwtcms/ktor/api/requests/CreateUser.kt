package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import org.apache.commons.codec.digest.DigestUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
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
                    val success = newSuspendedTransaction {
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
                            return@newSuspendedTransaction false
                        }

                        payload.permissions.forEach { (key, value) ->
                            if (key in PermissionsList.getPermissionsList()) {
                                runCatching {
                                    UserPermissionsTable.insert {
                                        it[UserPermissionsTable.permissionName] = key
                                        it[UserPermissionsTable.permissionState] = value
                                        it[UserPermissionsTable.userId] = UsersTable.selectAll().where { UsersTable.username eq payload.username }.first()[UsersTable.id]
                                    }
                                }
                            }
                        }

                        true
                    }

                    if (success) call.respondText("User created")
                    else call.respondText("User already exists", status = HttpStatusCode.Conflict)
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
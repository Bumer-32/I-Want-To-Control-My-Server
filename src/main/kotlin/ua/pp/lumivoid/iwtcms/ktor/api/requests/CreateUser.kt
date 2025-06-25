package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import org.apache.commons.codec.digest.DigestUtils
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ua.pp.lumivoid.iwtcms.ktor.api.PermissionsList
import ua.pp.lumivoid.iwtcms.ktor.api.doAuth
import ua.pp.lumivoid.iwtcms.ktor.tables.UserPermissions
import ua.pp.lumivoid.iwtcms.ktor.tables.Users

object CreateUser : Request() {
    override val path = "/api/createUser"

    override val request: Routing.() -> Unit = {
        post(path) {
            val payload = call.receive<CreateUserPayload>()

            doAuth(
                call = call,
                permission = "users.manage",
                success = {
                    transaction {
                        var salt = generateSequence { genSalt() }
                            .first { saltCandidate -> 
                                Users.selectAll().where(Users.salt eq saltCandidate).empty()
                            }

                        runCatching {
                            Users.insert {
                                it[Users.username] = payload.username
                                it[Users.passwordHash] = DigestUtils.sha256Hex(payload.password + salt)
                                it[Users.salt] = salt
                                it[Users.uniqueId] = DigestUtils.sha256Hex("${payload.username}+${payload.password}+${salt}")
                                it[Users.admin] = payload.isAdmin
                            }
                        }.onFailure {
                            runBlocking { call.respondText("User already exists", status = HttpStatusCode.Conflict) }
                            return@transaction
                        }

                        payload.permissions.forEach { (key, value) ->
                            if (key in PermissionsList.getPermissionsList()) {
                                try {
                                    UserPermissions.insert {
                                        it[UserPermissions.permissionName] = key
                                        it[UserPermissions.permissionState] = value
                                        it[UserPermissions.userId] = Users.selectAll().where { Users.username eq payload.username }.first()[Users.id]
                                    }
                                } catch (_: ExposedSQLException) {
                                }
                            }
                        }

                        runBlocking { call.respondText("User created") }
                    }
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
}

@Serializable
data class CreateUserPayload(
    val username: String,
    val password: String,
    val isAdmin: Boolean,
    val permissions: Map<String, Boolean>,
)
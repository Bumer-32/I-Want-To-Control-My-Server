package ua.pp.lumivoid.iwtcms.server.api.requests.api.user

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable
import org.apache.commons.codec.digest.DigestUtils
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import ua.pp.lumivoid.iwtcms.server.api.doAuth
import ua.pp.lumivoid.iwtcms.server.api.Request
import ua.pp.lumivoid.iwtcms.server.tables.UserPermissionsTable
import ua.pp.lumivoid.iwtcms.server.tables.UsersTable

internal object CreateUser : Request() {
    override val path = "/api/user/createUser"

    override val request: Routing.() -> Unit = {
        post(path) {
            val payload = call.receive<CreateUserPayload>()

            doAuth(
                call = call,
                permission = PermissionsList.Permission.USERS_MANAGE.value,
                success = {
                    if (create(
                            payload.username,
                            payload.password,
                            payload.admin,
                            payload.permissions
                        )
                    ) call.respond("User created")
                    else call.respond(HttpStatusCode.Conflict, "User already exists")
                },
            )
        }
    }

    suspend fun create(username: String, password: String, admin: Boolean, permissions: List<String>): Boolean {
        return suspendTransaction {
            if (UsersTable.selectAll().where { UsersTable.username eq username }.empty()) {
                val salt = generateSequence { genSalt() }.first { saltCandidate ->
                    UsersTable.selectAll().where(UsersTable.salt eq saltCandidate).empty()
                }

                val user = UsersTable.insert {
                    it[UsersTable.username] = username
                    it[UsersTable.passwordHash] = DigestUtils.sha256Hex(password + salt)
                    it[UsersTable.salt] = salt
                    it[UsersTable.uniqueId] = DigestUtils.sha256Hex("${username}+${password}+${salt}")
                    it[UsersTable.admin] = admin
                }

                if (!admin) {
                    permissions.forEach { permission ->
                        UserPermissionsTable.insert {
                            it[UserPermissionsTable.userId] = user[UsersTable.id]
                            it[UserPermissionsTable.permissionName] = permission
                        }
                    }
                }
                true
            } else {
                false
            }
        }
    }

    private fun genSalt(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..32)
            .map { allowedChars.random() }
            .joinToString("")
    }

    @Serializable
    private data class CreateUserPayload(
        val username: String,
        val password: String,
        val admin: Boolean,
        val permissions: List<String>
    )
}
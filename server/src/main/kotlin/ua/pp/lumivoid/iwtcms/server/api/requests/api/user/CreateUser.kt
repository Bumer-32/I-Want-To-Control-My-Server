package ua.pp.lumivoid.iwtcms.server.api.requests.api.user

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.apache.commons.codec.digest.DigestUtils
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ua.pp.lumivoid.iwtcms.server.api.Request
import ua.pp.lumivoid.iwtcms.server.api.doAuth
import ua.pp.lumivoid.iwtcms.server.tables.UserEntity
import ua.pp.lumivoid.iwtcms.server.tables.UserPermissionEntity
import ua.pp.lumivoid.iwtcms.server.tables.UsersTable

internal object CreateUser : Request() {
    override val path = "/api/user/createUser"

    override val request: Routing.() -> Unit = {
        post(path) {
            doAuth(
                call = call,
                permission = PermissionsList.Permission.USERS_MANAGE.value,
                success = {
                    val payload = call.receive<CreateUserPayload>()
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
        return withContext(Dispatchers.IO) {
            transaction {
                if (UsersTable.selectAll().where { UsersTable.username eq username }.empty()) {
                    val salt = generateSequence { genSalt() }.first { saltCandidate ->
                        UsersTable.selectAll().where(UsersTable.salt eq saltCandidate).empty()
                    }

                    val user = UserEntity.new {
                        this.username = username
                        this.passwordHash = DigestUtils.sha256Hex(password + salt)
                        this.salt = salt
                        this.uniqueId = DigestUtils.sha256Hex("${username}+${password}+${salt}")
                        this.admin = admin
                    }

                    if (!admin) {
                        permissions.forEach { permission ->
                            UserPermissionEntity.new {
                                this.user = user
                                this.permissionName = permission
                            }
                        }
                    }
                    true
                } else {
                    false
                }
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
    data class CreateUserPayload(
        val username: String,
        val password: String,
        val admin: Boolean,
        val permissions: List<String>
    )
}
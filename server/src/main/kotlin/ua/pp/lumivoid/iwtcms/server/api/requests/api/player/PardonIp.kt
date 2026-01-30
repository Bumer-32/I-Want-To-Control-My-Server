package ua.pp.lumivoid.iwtcms.server.api.requests.api.player

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ua.pp.lumivoid.iwtcms.server.IWTCMS
import ua.pp.lumivoid.iwtcms.server.api.Request
import ua.pp.lumivoid.iwtcms.server.api.doAuth
import ua.pp.lumivoid.iwtcms.server.api.requests.api.user.PermissionsList
import ua.pp.lumivoid.iwtcms.server.cookie.UserSession
import ua.pp.lumivoid.iwtcms.server.tables.UserEntity
import ua.pp.lumivoid.iwtcms.server.tables.UsersTable

internal object PardonIp : Request() {
    override val path = "/api/player/pardonIp"

    override val request: Routing.() -> Unit = {
        post(path) {
            doAuth(
                call = call,
                permission = PermissionsList.Permission.USERS_MANAGE.value,
                success = {
                    val session = call.sessions.get<UserSession>()
                    val payload = call.receive<PardonIpPayload>()
                    val iwtcms = IWTCMS.instance

                    if (!iwtcms.isIpBanned(payload.ip)) {
                        call.respond(HttpStatusCode.Conflict, "Such ip are not banned")
                        return@doAuth
                    }

                    val source = withContext(Dispatchers.IO) {
                        transaction {
                            UserEntity
                                .find { (UsersTable.username eq session!!.name) and (UsersTable.uniqueId eq session.id) }
                                .first().username
                        }
                    }

                    iwtcms.pardonIp(payload.ip)

                    logger.info("Iwtcms user $source successfully unbanned ip ${payload.ip}")
                    call.respond("Ip ${payload.ip} has been successfully unbanned")
                },
            )
        }
    }

    @Serializable
    data class PardonIpPayload(val ip: String)
}
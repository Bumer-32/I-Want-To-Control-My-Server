package ua.pp.lumivoid.iwtcms.server.api.requests.api.authentication

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import ua.pp.lumivoid.iwtcms.server.connectToTestDb
import ua.pp.lumivoid.iwtcms.server.cookie.UserSession
import ua.pp.lumivoid.iwtcms.server.module
import ua.pp.lumivoid.iwtcms.server.tables.UsersTable
import java.net.URLEncoder
import kotlin.test.assertEquals

class CheckLoginTest {
    @ParameterizedTest
    @MethodSource("users")
    internal fun `test checkLogin`(testSession: UserSession?, expectedStatus: HttpStatusCode, expectedMessage: String) = testApplication {
        application { module() }

        val cookie = URLEncoder.encode(Json.encodeToString(testSession), Charsets.UTF_8)

        val response = client.get(CheckLogin.path) {
            cookie("USER_SESSION", cookie)
        }

        assertEquals(expectedStatus, response.status)
        assertEquals(expectedMessage, response.bodyAsText())
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun beforeAll() {

        }

        @JvmStatic
        fun users(): List<Arguments> = runBlocking {
            connectToTestDb()

            val adminUniqueId = suspendTransaction {
                UsersTable
                    .selectAll()
                    .where { UsersTable.username eq "admin" }
                    .first()[UsersTable.uniqueId]
            }

            return@runBlocking listOf(
                Arguments.of(UserSession("admin", adminUniqueId), HttpStatusCode.OK, "admin"),
                Arguments.of(UserSession("test", adminUniqueId), HttpStatusCode.Unauthorized, "Not logged in"),
                Arguments.of(UserSession("admin", "test"), HttpStatusCode.Unauthorized, "Not logged in"),
                Arguments.of(UserSession("test", "test"), HttpStatusCode.Unauthorized, "Not logged in"),
                Arguments.of(null, HttpStatusCode.Unauthorized, "Not logged in"),
            )
        }
    }
}
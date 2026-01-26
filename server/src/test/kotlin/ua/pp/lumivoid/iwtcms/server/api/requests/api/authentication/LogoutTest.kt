package ua.pp.lumivoid.iwtcms.server.api.requests.api.authentication

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import ua.pp.lumivoid.iwtcms.server.SetupTestEnv
import ua.pp.lumivoid.iwtcms.server.cookie.UserSession
import ua.pp.lumivoid.iwtcms.server.createTestSession
import ua.pp.lumivoid.iwtcms.server.module
import kotlin.test.assertEquals

@ExtendWith(SetupTestEnv::class)
class LogoutTest {
    @ParameterizedTest
    @MethodSource("params")
    internal fun `test Logout`(testSession: UserSession?, expectedStatus: HttpStatusCode, expectedMessage: String) = testApplication {
        application { module() }

        val cookie = Json.encodeToString(testSession)

        val response = client.post(Logout.path) {
            if (testSession != null) cookie("USER_SESSION", cookie)
        }

        assertEquals(expectedStatus, response.status)
        assertEquals(expectedMessage, response.bodyAsText())
    }

    companion object {
        @JvmStatic
        fun params(): List<Arguments> {
            return listOf(
                Arguments.of(createTestSession(), HttpStatusCode.OK, "Logged out"),
                Arguments.of(null, HttpStatusCode.OK, "Not logged in"),
            )
        }
    }
}
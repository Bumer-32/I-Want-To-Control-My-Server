package ua.pp.lumivoid.iwtcms.server.api.requests.api.authentication

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import ua.pp.lumivoid.iwtcms.server.SetupTestEnv
import ua.pp.lumivoid.iwtcms.server.module
import kotlin.test.assertEquals

@ExtendWith(SetupTestEnv::class)
class LoginTest {
    @ParameterizedTest
    @MethodSource("params")
    internal fun `test Login`(testPayload: Login.LoginPayload, expectedStatus: HttpStatusCode, expectedMessage: String) = testApplication {
        application { module() }

        val response = client.post(Login.path) {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(testPayload))
        }

        assertEquals(expectedStatus, response.status)
        assertEquals(expectedMessage, response.bodyAsText())
    }

    companion object {
        @JvmStatic
        fun params(): List<Arguments> = runBlocking {
            return@runBlocking listOf(
                Arguments.of(Login.LoginPayload("admin", "iwtcms"), HttpStatusCode.OK, "Login successful"),
                Arguments.of(Login.LoginPayload("test", "test"), HttpStatusCode.Unauthorized, "Login failed"),
            )
        }
    }
}
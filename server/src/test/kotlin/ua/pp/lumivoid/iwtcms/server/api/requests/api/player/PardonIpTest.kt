package ua.pp.lumivoid.iwtcms.server.api.requests.api.player

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import ua.pp.lumivoid.iwtcms.server.*
import kotlin.test.assertEquals

@ExtendWith(SetupTestEnv::class)
class PardonIpIpTest: AuthBasedTest() {
    override var url: String = PardonIp.path

    @ParameterizedTest
    @MethodSource("params")
    internal fun `test PardonIp`(testPayload: PardonIp.PardonIpPayload, expectedStatus: HttpStatusCode) = testApplication {
        verifyTestUserRole(true)

        application { module() }

        val response = client.post(url) {
            cookie("USER_SESSION", Json.encodeToString(createTestSession()))
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(testPayload))
        }

        assertEquals(expectedStatus, response.status)
    }

    companion object {
        @JvmStatic
        fun params(): List<Arguments> = runBlocking {
            return@runBlocking listOf(
                Arguments.of(PardonIp.PardonIpPayload(""), HttpStatusCode.Conflict),
                Arguments.of(PardonIp.PardonIpPayload("1.1.1.1"), HttpStatusCode.OK),
            )
        }
    }
}
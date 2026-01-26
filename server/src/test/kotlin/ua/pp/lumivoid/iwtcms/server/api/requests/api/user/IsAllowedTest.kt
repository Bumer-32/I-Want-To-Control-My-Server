package ua.pp.lumivoid.iwtcms.server.api.requests.api.user

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
class IsAllowedTest: AuthBasedTest() {
    override var url: String = IsAllowed.path.replaceAfterLast("/", "")

    @ParameterizedTest
    @MethodSource("params")
    internal fun `test IsAllowed`(permission: String, admin: Boolean, expectedStatus: HttpStatusCode) = testApplication {
        verifyTestUserRole(admin)

        application { module() }

        val response = client.get("$url$permission") {
            cookie("USER_SESSION", Json.encodeToString(createTestSession()))
        }

        assertEquals(expectedStatus, response.status)
    }

    companion object {
        @JvmStatic
        fun params(): List<Arguments> = runBlocking {
            return@runBlocking listOf(
                Arguments.of("", false, HttpStatusCode.NotFound),
                Arguments.of(PermissionsList.Permission.LOGS_READ.value, false, HttpStatusCode.Forbidden),
                Arguments.of(PermissionsList.Permission.LOGS_READ.value, true, HttpStatusCode.OK),
            )
        }
    }
}
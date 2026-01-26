package ua.pp.lumivoid.iwtcms.server.api.requests.api.user

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.extension.ExtendWith
import ua.pp.lumivoid.iwtcms.server.*
import kotlin.test.Test
import kotlin.test.assertEquals

@ExtendWith(SetupTestEnv::class)
class UsersListTest: AuthBasedTest() {
    override var url: String = UsersList.path

    @Test
    internal fun `test UsersList`() = testApplication {
        verifyTestUserRole(true)

        application { module() }

        val response = client.get(url) {
            cookie("USER_SESSION", Json.encodeToString(createTestSession()))
        }

        assertEquals(HttpStatusCode.OK, response.status)

        val body = response.bodyAsText()
        val permissionsList = Json.decodeFromString<List<UsersList.UsersListPayload>>(body)
        assert(permissionsList.isNotEmpty())
    }
}
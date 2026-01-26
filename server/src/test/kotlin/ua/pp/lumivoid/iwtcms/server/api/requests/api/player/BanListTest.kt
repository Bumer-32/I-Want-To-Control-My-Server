package ua.pp.lumivoid.iwtcms.server.api.requests.api.player

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import ua.pp.lumivoid.iwtcms.server.*
import kotlin.test.assertEquals

@ExtendWith(SetupTestEnv::class)
class BanListTest: AuthBasedTest() {
    override var url: String = BanList.path

    @Test
    internal fun `test BanList`() = testApplication {
        verifyTestUserRole(true)

        application { module() }

        val response = client.get(url) {
            cookie("USER_SESSION", Json.encodeToString(createTestSession()))
        }

        assertEquals(HttpStatusCode.OK, response.status)

        val body = response.bodyAsText()
        val bannedList = Json.decodeFromString<List<BanList.Ban>>(body)

        assert(bannedList.isNotEmpty())
    }
}
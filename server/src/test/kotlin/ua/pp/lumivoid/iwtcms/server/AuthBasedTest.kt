package ua.pp.lumivoid.iwtcms.server

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

abstract class AuthBasedTest {
    abstract var url: String

    @Test
    fun `test for unauthorized`() = testApplication {
        application { module() }

        HttpMethod.DefaultMethods.forEach {
            val response = client.request(url) {
                method = it
                contentType(ContentType.Application.Json)
//                setBody("{}")
            }

            if (response.status != HttpStatusCode.MethodNotAllowed && response.status != HttpStatusCode.NotFound) {
                assertEquals(HttpStatusCode.Unauthorized, response.status)
            }
        }
    }

    @Test
    fun `test for forbidden`() = testApplication {
        verifyTestUserRole(false)
        application { module() }

        HttpMethod.DefaultMethods.forEach {
            val response = client.request(url) {
                method = it
                cookie("USER_SESSION", Json.encodeToString(createTestSession()))
                contentType(ContentType.Application.Json)
//                setBody("{}")
            }

            if (response.status != HttpStatusCode.MethodNotAllowed && response.status != HttpStatusCode.NotFound) {
                assertEquals(HttpStatusCode.Forbidden, response.status)
            }
        }
    }
}
package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.http.ContentType
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object ApiListG: Request() {
    override val PATH = "/apiList"

    private val json = Json { prettyPrint = true }

    private val apis = mutableMapOf<String, String>()

    override val request: Routing.() -> Unit = {
        get(PATH) {
            val response = json.encodeToString(apis)
            call.respondText(response, contentType = ContentType.Text.Plain)
        }
    }

    fun registerAPI(apiName: String, path: String) {
        apis.put(apiName, path)
    }
}
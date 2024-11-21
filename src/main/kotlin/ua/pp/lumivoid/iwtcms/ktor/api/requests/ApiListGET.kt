package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.http.ContentType
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ua.pp.lumivoid.iwtcms.Constants

object ApiListGET {
    private val logger = Constants.EMBEDDED_SERVER_LOGGER
    private val json = Json { prettyPrint = true }

    private val apis = mutableMapOf<String, String>()

    private const val PATH = "/apiList/"

    val request: Routing.() -> Unit = {
        logger.info("Initializing $PATH request")
        registerAPI("ApiListGET", PATH)

        get(PATH) {
            val response = json.encodeToString(apis)
            call.respondText(response, contentType = ContentType.Text.Plain)
        }
    }

    fun registerAPI(apiName: String, path: String) {
        apis.put(apiName, path)
    }
}
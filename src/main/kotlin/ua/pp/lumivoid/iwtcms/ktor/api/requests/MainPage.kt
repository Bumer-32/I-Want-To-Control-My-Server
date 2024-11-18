package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.http.ContentType
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import ua.pp.lumivoid.iwtcms.Constants

object MainPage {
    private val logger = Constants.EMBEDDED_SERVER_LOGGER

    val request: Routing.() -> Unit = {
        logger.info("Initializing / request")
        get("/") {
            val response = """
                <html>
                    <head>
                        <title>My Ktor App</title>
                    </head>
                    <body>
                        <h1>Welcome to my Ktor app!</h1>
                        <p>This is a simple example of a Ktor application.</p>
                    </body>
                </html>
            """.trimIndent()

            call.respondText(response, contentType = ContentType.Text.Html)
        }
    }
}
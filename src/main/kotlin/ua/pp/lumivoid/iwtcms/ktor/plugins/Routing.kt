package ua.pp.lumivoid.iwtcms.ktor.plugins

import io.ktor.http.ContentType
import io.ktor.server.application.*
import io.ktor.server.response.respondText
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
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
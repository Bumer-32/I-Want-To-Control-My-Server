package ua.pp.lumivoid.iwtcms.server

import io.ktor.http.*
import io.ktor.network.tls.certificates.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.httpsredirect.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import ua.pp.lumivoid.iwtcms.server.cookie.UserSession
import ua.pp.lumivoid.iwtcms.server.util.Config
import ua.pp.lumivoid.iwtcms.server.util.ErrorMessages
import java.io.File
import java.security.KeyStore
import kotlin.system.exitProcess
import kotlin.time.Duration.Companion.seconds

private val logger = Constants.LOGGER
private val config = Config.readConfig()

internal fun Application.module(test: Boolean = false) {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    install(Sessions) {
        cookie<UserSession>("USER_SESSION") {
            cookie.httpOnly = true
            cookie.secure = config.useSSL
            // cookie.sameSite = "None"
        }
    }

    install(ContentNegotiation) {
        json()
    }

    install(StatusPages) {
        status(HttpStatusCode.NotFound) { call, _ ->
            if(!call.request.path().startsWith("/api")) call.respondText(javaClass.getResource(Constants.NOT_FOUND_HTML)!!.readText(), ContentType.Text.Html, HttpStatusCode.NotFound)
        }

        exception<Throwable> { call, cause ->
            when (cause) {
                is BadRequestException -> call.respondText("Bad request: ${cause.localizedMessage}", status = HttpStatusCode.BadRequest)
                else -> {
                    cause.stackTrace.forEach { logger.error(it.toString()) }
                    call . respondText ("Server fucked up, $cause", status = HttpStatusCode.InternalServerError)
                }
            }
        }
    }

    if (config.useSSL && !config.devMode && !test) {
        install(HttpsRedirect) {
            sslPort = config.port
            permanentRedirect = true
        }
    }

    if (config.devMode) {
        install(CORS) {
            anyHost()
        }
    }

    configureRouting()
}

internal fun ApplicationEngine.Configuration.envConfig() {
    logger.info("Configuring environment")

    if (config.useSSL && !config.devMode) {
        val keyStoreFile = File(Constants.SSL_CERTIFICATE_FILE)
        val keyStore: KeyStore

        if (config.customCertificate) {
            logger.info("Check is SSL keys available")
            if (keyStoreFile.exists()) {
                logger.info("SSL keys found!")
                keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
                keyStoreFile.inputStream().use { inputStream ->
                    keyStore.load(inputStream, config.sslPass.toCharArray())
                }
            } else {
                logger.error("SSL keys are not found")
                ErrorMessages.BAD_CERTIFICATE.launch(logger)

                exitProcess(1)
            }
        } else {
            keyStore = buildKeyStore {
                certificate(config.sslAlias) {
                    password = config.sslPass
                }
            }
            keyStore.saveToFile(keyStoreFile, config.sslPass)
        }

        sslConnector(
            keyStore = keyStore,
            keyAlias = config.sslAlias,
            keyStorePassword = { config.sslPass.toCharArray() },
            privateKeyPassword = { config.sslPass.toCharArray() },
        ) {
            host = config.ip
            port = config.port
            keyStorePath = keyStoreFile
        }
    } else {
        connector {
            host = config.ip
            port = config.port
        }
    }
}

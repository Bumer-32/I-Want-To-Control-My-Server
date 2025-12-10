package ua.pp.lumivoid.iwtcms.server

import io.ktor.http.HttpStatusCode
import io.ktor.network.tls.certificates.buildKeyStore
import io.ktor.network.tls.certificates.saveToFile
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.connector
import io.ktor.server.engine.sslConnector
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.httpsredirect.HttpsRedirect
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.statuspages.statusFile
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import ua.pp.lumivoid.iwtcms.Constants
import ua.pp.lumivoid.iwtcms.server.cookie.UserSession
import ua.pp.lumivoid.iwtcms.server.util.Config
import ua.pp.lumivoid.iwtcms.server.util.ErrorMessages
import java.io.File
import java.security.KeyStore
import kotlin.system.exitProcess
import kotlin.time.Duration.Companion.seconds

private val logger = Constants.EMBEDDED_SERVER_LOGGER
private val config = Config.readConfig()

fun Application.module() {
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
        statusFile(HttpStatusCode.NotFound, filePattern = "/web/404.html")

        exception<Throwable> { _, cause ->
            cause.stackTrace.forEach { logger.error(it.toString()) }
        }
    }

    if (config.useSSL && !Config.readConfig().devMode) {
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

fun ApplicationEngine.Configuration.envConfig() {
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

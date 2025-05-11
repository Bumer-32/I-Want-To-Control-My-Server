package ua.pp.lumivoid.iwtcms.ktor

import io.ktor.network.tls.certificates.buildKeyStore
import io.ktor.network.tls.certificates.saveToFile
import io.ktor.server.application.Application
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.connector
import io.ktor.server.engine.sslConnector
import ua.pp.lumivoid.iwtcms.Constants
import ua.pp.lumivoid.iwtcms.ktor.plugins.configureRouting
import ua.pp.lumivoid.iwtcms.ktor.util.Config
import ua.pp.lumivoid.iwtcms.ktor.util.ErrorMessages
import java.io.File
import java.security.KeyStore
import kotlin.system.exitProcess

private val logger = Constants.EMBEDDED_SERVER_LOGGER
private val modConfig = Config.readConfig()

fun Application.module() {
    configureRouting()
}

fun ApplicationEngine.Configuration.envConfig() {
    logger.info("Configuring environment")

    if (Config.readConfig().useSSL && !Config.readConfig().devMode) {
        val keyStoreFile = File(Constants.SSL_CERTIFICATE_FILE)
        val keyStore: KeyStore

        if (Config.readConfig().customSertificate) {
            logger.info("Check is SSL keys available")
            if (keyStoreFile.exists()) {
                logger.info("SSL keys found!")
                keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
                keyStoreFile.inputStream().use { inputStream ->
                    keyStore.load(inputStream, modConfig.sslPass.toCharArray())
                }
            } else {
                logger.error("SSL keys are not found")
                ErrorMessages.BAD_CERTIFICATE.launch(logger)

                exitProcess(1)
            }
        } else {
            keyStore =
                buildKeyStore {
                    certificate(modConfig.sslAlias) {
                        password = modConfig.sslPass
                    }
                }
            keyStore.saveToFile(keyStoreFile, modConfig.sslPass)
        }

        sslConnector(
            keyStore = keyStore,
            keyAlias = modConfig.sslAlias,
            keyStorePassword = { modConfig.sslPass.toCharArray() },
            privateKeyPassword = { modConfig.sslPass.toCharArray() },
        ) {
            host = modConfig.ip
            port = modConfig.port
            keyStorePath = keyStoreFile
        }
    } else {
        connector {
            host = modConfig.ip
            port = modConfig.port
        }
    }
}

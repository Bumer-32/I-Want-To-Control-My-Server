package ua.pp.lumivoid.iwtcms.ktor

import io.ktor.network.tls.certificates.buildKeyStore
import io.ktor.network.tls.certificates.saveToFile
import io.ktor.server.application.Application
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.connector
import io.ktor.server.engine.sslConnector
import ua.pp.lumivoid.iwtcms.Constants
import ua.pp.lumivoid.iwtcms.ktor.plugins.configureRouting
import ua.pp.lumivoid.iwtcms.util.Config
import ua.pp.lumivoid.iwtcms.util.MinecraftServerHandler
import java.io.File
import java.security.KeyStore

private val logger = Constants.EMBEDDED_SERVER_LOGGER
private val modConfig = Config.readConfig()

fun Application.module() {
    configureRouting()
}

fun ApplicationEngine.Configuration.envConfig() {
    logger.info("Configuring environment")

    if (Config.readConfig().useSSL) {
        val keyStoreFile = File(Constants.SSL_SERTIFICATE_FILE)
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
                logger.error("###########################################################################################")

                logger.error("")
                logger.error("")
                logger.error("")
                logger.error("")
                logger.error("")

                logger.error("+-----------------------------------------------------------------------------------------+")
                logger.error("|                                                                                         |")
                logger.error("|                       IWTCMS STOPPED SERVER FOR SECURITY REASONS                        |")
                logger.error("|                              ERROR: CAN'T READ CONFIG FILE                              |")
                logger.error("|                        FIX CONFIG FILE AND THEN RESTART SERVER                          |")
                logger.error("|                                                                                         |")
                logger.error("|                     OLD CONFIG FILE RENAMED WITH *-BAD SUFFIX                           |")
                logger.error("|                              NEW CONFIG MUST BE GENERATED                               |")
                logger.error("|                                                                                         |")
                logger.error("|                IF YOU FIXED THE CONFIG FILE BUT STILL GET THIS ERROR,                   |")
                logger.error("|                          CONTACT THE DEVELOPERS ON GITHUB                               |")
                logger.error("|                                                                                         |")
                logger.error("|                          IWTCMS CARES ABOUT YOUR SECURITY!                              |")
                logger.error("|                                                                                         |")
                logger.error("+-----------------------------------------------------------------------------------------+")



                System.exit(1)

                return
            }
        } else {
            keyStore = buildKeyStore {
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
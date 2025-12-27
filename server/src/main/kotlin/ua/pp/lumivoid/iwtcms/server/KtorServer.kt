package ua.pp.lumivoid.iwtcms.server

import io.ktor.server.application.Application
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.applicationEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine

internal object KtorServer {
    private val logger = Constants.LOGGER

    private var server: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration>? = null

    fun setup() {
        logger.info("Starting up embedded server")

        server = embeddedServer(
            Netty,
            environment = applicationEnvironment {
                log = logger
            },
            { envConfig() },
            module = Application::module,
        ).start(wait = false)
    }

    fun shutdown() {
        logger.info("Shutting down embedded server")

        server?.stop()
    }
}

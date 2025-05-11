package ua.pp.lumivoid.iwtcms.ktor

import io.ktor.server.application.Application
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.applicationEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import ua.pp.lumivoid.iwtcms.Constants
import ua.pp.lumivoid.iwtcms.ktor.api.websockets.ConsoleWS
import ua.pp.lumivoid.iwtcms.ktor.api.websockets.ServerStatsWS

object KtorServer {
    private val logger = Constants.EMBEDDED_SERVER_LOGGER

    private var server: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration>? = null

    fun setup() {
        logger.info("Starting up embedded server")

        server =
            embeddedServer(
                Netty,
                environment =
                    applicationEnvironment {
                        log = logger
                    },
                { envConfig() },
                module = Application::module,
            ).start(wait = false)
    }

    fun shutdown() {
        logger.info("Shutting down embedded server")

        ConsoleWS.asWs()?.shutdown()
        ServerStatsWS.asWs()?.shutdown()

        server?.stop()
    }
}

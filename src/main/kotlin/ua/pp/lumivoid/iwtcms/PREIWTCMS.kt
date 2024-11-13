package ua.pp.lumivoid.iwtcms

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint
import org.slf4j.LoggerFactory
import ua.pp.lumivoid.iwtcms.ktor.KtorServer
import ua.pp.lumivoid.iwtcms.util.Config
import ua.pp.lumivoid.iwtcms.util.CustomLogger
import kotlin.concurrent.thread

object PREIWTCMS: PreLaunchEntrypoint {
    private val logger = LoggerFactory.getLogger("iwtcms pre launch")
    @Suppress("DEPRECATION")
    override fun onPreLaunch() {
        logger.info("Initialize pre launch iwtcms")
        CustomLogger.setup()

        KtorServer.setup()
    }
}
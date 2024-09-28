package ua.pp.lumivoid.iwtcms

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint
import org.slf4j.LoggerFactory
import ua.pp.lumivoid.iwtcms.server.Server
import ua.pp.lumivoid.iwtcms.util.CustomLogger

object PREIWTCMS: PreLaunchEntrypoint {
    private val logger = LoggerFactory.getLogger("iwtcms pre launch")
    @Suppress("DEPRECATION")
    override fun onPreLaunch() {
        logger.info("Initialize pre launch iwtcms")
        Server.setup()
        CustomLogger.setup()
    }
}
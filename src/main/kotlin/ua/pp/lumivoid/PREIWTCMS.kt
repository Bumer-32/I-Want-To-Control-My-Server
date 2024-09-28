package ua.pp.lumivoid

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint
import org.slf4j.LoggerFactory
import ua.pp.lumivoid.server.Server
import ua.pp.lumivoid.util.CustomLogger

object PREIWTCMS: PreLaunchEntrypoint {
    private val logger = LoggerFactory.getLogger("iwtcms pre launch")
    @Suppress("DEPRECATION")
    override fun onPreLaunch() {
        Server.setup()

        CustomLogger.setup()
    }
}
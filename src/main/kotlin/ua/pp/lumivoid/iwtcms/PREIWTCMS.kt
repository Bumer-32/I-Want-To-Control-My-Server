package ua.pp.lumivoid.iwtcms

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint
import org.slf4j.LoggerFactory
import ua.pp.lumivoid.iwtcms.server.DefaultServer
import ua.pp.lumivoid.iwtcms.server.SSLServer
import ua.pp.lumivoid.iwtcms.util.Config
import ua.pp.lumivoid.iwtcms.util.CustomLogger

object PREIWTCMS: PreLaunchEntrypoint {
    private val logger = LoggerFactory.getLogger("iwtcms pre launch")
    @Suppress("DEPRECATION")
    override fun onPreLaunch() {
        logger.info("Initialize pre launch iwtcms")
        CustomLogger.setup()
        if (Config.readConfig().useSSL) {
            SSLServer.setup()
        } else {
            DefaultServer.setup()
        }
    }
}
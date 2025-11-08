package ua.pp.lumivoid.iwtcms.util

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import ua.pp.lumivoid.iwtcms.Constants
import ua.pp.lumivoid.iwtcms.IWTCMS
import ua.pp.lumivoid.iwtcms.server.KtorServer

object StoppedServerTrigger {
    private val logger = Constants.LOGGER

    fun register() {
        ServerLifecycleEvents.SERVER_STOPPED.register {
            if (IWTCMS.h2Server != null) {
                logger.info("Shutting down h2 webserver")
                IWTCMS.h2Server!!.stop()
            }

            if (IWTCMS.viteProccess != null) {
                logger.info("Shutting down vite process")
                IWTCMS.viteProccess!!.destroy()
            }

            KtorServer.shutdown()
            CustomLogger.shutdown()
        }
    }
}

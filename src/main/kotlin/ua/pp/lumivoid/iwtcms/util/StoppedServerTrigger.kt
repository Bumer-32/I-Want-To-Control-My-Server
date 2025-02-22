package ua.pp.lumivoid.iwtcms.util

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import ua.pp.lumivoid.iwtcms.ktor.KtorServer

object StoppedServerTrigger {
    fun register() {
        ServerLifecycleEvents.SERVER_STOPPED.register {
            KtorServer.shutdown()
            CustomLogger.shutdown()
        }
    }
}
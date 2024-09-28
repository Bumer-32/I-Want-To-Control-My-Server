package ua.pp.lumivoid.server

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import ua.pp.lumivoid.util.CustomLogger

object StoppedServerTrigger {
    fun register() {
        ServerLifecycleEvents.SERVER_STOPPED.register {
            Server.shutdown()
            CustomLogger.shutdown()
        }
    }
}
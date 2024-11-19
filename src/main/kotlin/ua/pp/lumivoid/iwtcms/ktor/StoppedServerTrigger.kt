package ua.pp.lumivoid.iwtcms.ktor

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import ua.pp.lumivoid.iwtcms.ktor.api.websockets.WsConsoleImpl
import ua.pp.lumivoid.iwtcms.util.CustomLogger

object StoppedServerTrigger {
    fun register() {
        ServerLifecycleEvents.SERVER_STOPPED.register {
            WsConsoleImpl.asWsConsole()?.shutdown()
            KtorServer.shutdown()
            CustomLogger.shutdown()
        }
    }
}
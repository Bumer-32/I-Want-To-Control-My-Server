package ua.pp.lumivoid.iwtcms.util

import kotlinx.coroutines.runBlocking
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import ua.pp.lumivoid.iwtcms.ktor.KtorServer
import ua.pp.lumivoid.iwtcms.ktor.api.dev.DevReloadWS
import ua.pp.lumivoid.iwtcms.ktor.api.dev.KFSWFileWatcher

object StoppedServerTrigger {
    fun register() {
        ServerLifecycleEvents.SERVER_STOPPED.register {
            KtorServer.shutdown()
            CustomLogger.shutdown()

            if (Config.readConfig().devMode) {

                runBlocking { KFSWFileWatcher.stop() }
                DevReloadWS.ws
            }
        }
    }
}
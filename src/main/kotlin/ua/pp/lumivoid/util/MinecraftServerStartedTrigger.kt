package ua.pp.lumivoid.util

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.server.MinecraftServer

object MinecraftServerStartedTrigger {

    var server: MinecraftServer? = null

    fun register() {
        ServerLifecycleEvents.SERVER_STARTED.register { server ->
            this.server = server
        }
    }
}
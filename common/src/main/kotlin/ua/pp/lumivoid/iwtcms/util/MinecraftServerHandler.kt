package ua.pp.lumivoid.iwtcms.util

import dev.architectury.event.events.common.LifecycleEvent
import net.minecraft.server.MinecraftServer

/**
 * I don't know any other way to get Minecraft Server instance,
 * So I created this handler
 * When server starting variable server initializing with Minecraft Server instance
 *
 * Also, you can request stop server BEFORE it launch
 *
 * Ez way to get Minecraft Server instance
 */
object MinecraftServerHandler {

    var server: MinecraftServer? = null
    private  var requestStop = false // Using for stop server BEFORE it launch, when we didn't have a server instance

    fun register() {

        LifecycleEvent.SERVER_STARTING.register { server ->
            this.server = server
            if (requestStop) server.stop(false)
        }
    }

    fun stop() {
        requestStop = true
        server?.stop(false)
    }
}
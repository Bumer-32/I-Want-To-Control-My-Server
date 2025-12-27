package ua.pp.lumivoid.iwtcms

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.server.MinecraftServer
import ua.pp.lumivoid.iwtcms.server.util.McHandler

object McHandlerImpl: McHandler {
    override val implName: String = "iwtcms-fabric"
    private lateinit var server: MinecraftServer

    init {
        ServerLifecycleEvents.SERVER_STARTING.register { server ->
           this.server = server
        }
    }

    override fun getMinecraftServer(): MinecraftServer = server


    override fun isSparkLoaded(): Boolean = FabricLoader.getInstance().isModLoaded("spark")
}
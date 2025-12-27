package ua.pp.lumivoid.iwtcms.server.util

import net.minecraft.server.MinecraftServer

/**
 * Main interface to give iwtcms access to game independently of platform it implementing
 */
interface McHandler {
    /**
     * Name of mod, application or idk which implementing this
     * For e.g. official fabricmc mod implements it's like iwtcms-fabric
     * DON'T USE OFFICIAL-LIKE NAMES HERE, if you're making your own mod based on iwtcms-server or fork of iwtcms change this name to avoid conflicts and make debug easier
     */
    val implName: String
    fun getMinecraftServer(): MinecraftServer
    fun isSparkLoaded(): Boolean
}
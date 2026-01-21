package ua.pp.lumivoid.iwtcms.server.util

import java.util.*

/**
 * Main interface to give iwtcms access to game independently of platform it implementing
 */
abstract class McHandler {
    /**
     * Name of mod, application or idk which implementing this
     * For e.g. official fabricmc mod implements it's like iwtcms-fabric
     * DON'T USE OFFICIAL-LIKE NAMES HERE, if you're making your own mod based on iwtcms-server or fork of iwtcms change this name to avoid conflicts and make debug easier
     */
    abstract val implName: String
    abstract fun isSparkLoaded(): Boolean

    abstract fun players(): List<Player>

    abstract fun isBanned(player: Player): Boolean
    abstract fun ban(player: Player, creationDate: Date?, source: String?, expireDate: Date?, reason: String?)
    abstract fun pardon(player: Player)
    abstract fun bans(): List<Ban>

    abstract fun isIpBanned(ip: String): Boolean
    abstract fun banIp(ip: String, creationDate: Date?, source: String?, expireDate: Date?, reason: String?)
    abstract fun pardonIp(ip: String)
    abstract fun ipBans(): List<IpBan>

    abstract fun op(player: Player)
    abstract fun deop(player: Player)
    abstract fun isOp(player: Player): Boolean

    abstract fun disconnect(player: Player, reason: String, translatable: Boolean)
    abstract fun kill(player: Player)

    abstract fun executeMcCommand(command: String)
    abstract fun minecraftVersion(): String

    abstract fun playersCount(): Int
    abstract fun maxPlayersCount(): Int
    abstract fun mcIp(): String
    abstract fun mcPort(): Int


    data class Player(
        val name: String,
        val uuid: String,
        val ipAddress: String,
        val inventory: List<ItemStack>,
        val gameMode: Int,
        val permissionLevel: Int,
        val x: Double,
        val y: Double,
        val z: Double
    )
    data class ItemStack(val id: String, val count: Int)
    data class Ban(val player: Player, val creationDate: Date?, val source: String?, val expireDate: Date?, val reason: String?)
    data class IpBan(val ip: String, val creationDate: Date?, val source: String?, val expireDate: Date?, val reason: String?)

}
package ua.pp.lumivoid.iwtcms

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.chat.Component
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.players.IpBanListEntry
import net.minecraft.server.players.UserBanListEntry
import ua.pp.lumivoid.iwtcms.server.util.McHandler
import java.util.*

object McHandlerImpl: McHandler() {
    override val implName: String = "iwtcms-fabric"
    private lateinit var server: MinecraftServer

    init {
        ServerLifecycleEvents.SERVER_STARTING.register { server ->
           this.server = server
        }
    }

    override fun isSparkLoaded(): Boolean = FabricLoader.getInstance().isModLoaded("spark")

    override fun players(): List<Player> = server.playerList.players.map { createPlayer(it) }

    override fun isBanned(player: Player): Boolean = server.playerList.bans.isBanned(getPlayer(player).gameProfile)
    override fun ban(player: Player, creationDate: Date?, source: String?, expireDate: Date?, reason: String?) =
        server.playerList.bans.add(UserBanListEntry(getPlayer(player).gameProfile, creationDate, source, expireDate, reason))
    override fun pardon(player: Player) = server.playerList.bans.remove(getPlayer(player).gameProfile)
    override fun bans(): List<Ban> =
        server.playerList.bans.entries.map { Ban(createPlayer(getPlayer(it.displayName.string)), it.created, it.source, it.expires, it.reason) }

    override fun isIpBanned(ip: String): Boolean = server.playerList.ipBans.isBanned(ip)
    override fun banIp(ip: String, creationDate: Date?, source: String?, expireDate: Date?, reason: String?) =
        server.playerList.ipBans.add(IpBanListEntry(ip, creationDate, source, expireDate, reason))
    override fun pardonIp(ip: String) = server.playerList.ipBans.remove(ip)
    override fun ipBans(): List<IpBan> =
        server.playerList.ipBans.entries.map { IpBan(it.displayName.string, it.created, it.source, it.expires, it.reason) }

    override fun op(player: Player) = server.playerList.op(getPlayer(player).gameProfile)
    override fun deop(player: Player) = server.playerList.deop(getPlayer(player).gameProfile)
    override fun isOp(player: Player): Boolean = server.playerList.isOp(getPlayer(player).gameProfile)

    override fun disconnect(player: Player, reason: String, translatable: Boolean) =
        getPlayer(player).connection.disconnect(if (translatable) Component.translatable(reason) else Component.literal(reason))
    override fun kill(player: Player) = getPlayer(player).let { it.kill(it.level()) }

    override fun executeMcCommand(command: String) = server.commands.performPrefixedCommand(server.createCommandSourceStack(), command)
    override fun minecraftVersion(): String = server.serverVersion
    override fun playersCount(): Int = server.playerList.playerCount
    override fun maxPlayersCount(): Int = server.playerList.maxPlayers

    override fun mcIp(): String = server.localIp

    override fun mcPort(): Int = server.port

    private fun getPlayer(player: Player): ServerPlayer = server.playerList.players.find { it.name.string == player.name }!!
    private fun getPlayer(name: String): ServerPlayer = server.playerList.players.find { it.name.string == name }!!
    private fun createPlayer(serverPlayer: ServerPlayer): Player {
        val inventory = mutableListOf<ItemStack>()

        serverPlayer.inventory.forEach {
            inventory.add(
                ItemStack(
                    it.itemName.string,
                    it.count
                )
            )
        }

        return Player(
            name = serverPlayer.name.string,
            uuid = serverPlayer.stringUUID,
            ipAddress = serverPlayer.ipAddress,
            gameMode = serverPlayer.gameMode.gameModeForPlayer.id,
            permissionLevel = serverPlayer.permissionLevel,
            inventory = inventory,
            x = serverPlayer.position().x,
            y = serverPlayer.position().y,
            z = serverPlayer.position().z,
        )
    }
}
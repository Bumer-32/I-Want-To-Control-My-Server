package ua.pp.lumivoid.iwtcms.server

import ua.pp.lumivoid.iwtcms.server.util.McHandler
import java.util.*

object TestMcHandlerImpl: McHandler() {
    private val playersList = listOf(
        Player("test", "test", "test", listOf(ItemStack("test", 64)), 0, 0, 0.0, 0.0, 0.0),
        Player("banned", "banned", "1.1.1.1", listOf(ItemStack("banned", 64)), 0, 0, 0.0, 0.0, 0.0),
        Player("op", "op", "op", listOf(ItemStack("op", 64)), 0, 0, 0.0, 0.0, 0.0)
    )

    override val implName: String = "iwtcms-test"

    override fun isSparkLoaded(): Boolean = false

    override fun players(): List<Player> = playersList

    override fun isBanned(player: Player): Boolean = player.name == "banned"
    override fun bans(): List<Ban> = playersList.map { Ban(Player("banned", "banned", "banned", emptyList(), 0, 0, 0.0, 0.0, 0.0), Date(), "test", Date(), "test") }

    override fun isIpBanned(ip: String): Boolean = ip == "1.1.1.1"
    override fun ipBans(): List<IpBan> = listOf(IpBan("banned", Date(), "banned", Date(), "banned"))

    override fun isOp(player: Player): Boolean = player.name == "op"

    override fun minecraftVersion(): String = "0"
    override fun playersCount(): Int = players().size
    override fun maxPlayersCount(): Int = players().size

    override fun mcIp(): String = "test"
    override fun mcPort(): Int = 0

    override fun ban(player: Player, creationDate: Date?, source: String?, expireDate: Date?, reason: String?) {}
    override fun pardon(player: Player) {}
    override fun banIp(ip: String, creationDate: Date?, source: String?, expireDate: Date?, reason: String?) {}
    override fun pardonIp(ip: String) {}
    override fun op(player: Player) {}
    override fun deop(player: Player) {}
    override fun disconnect(player: Player, reason: String, translatable: Boolean) {}
    override fun kill(player: Player) {}
    override fun executeMcCommand(command: String) {}
}

package ua.pp.lumivoid.iwtcms.server.api.requests.api.ws

import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ua.pp.lumivoid.iwtcms.server.IWTCMS
import ua.pp.lumivoid.iwtcms.server.api.WebSocket
import ua.pp.lumivoid.iwtcms.server.api.doAuth
import ua.pp.lumivoid.iwtcms.server.api.requests.api.user.PermissionsList
import ua.pp.lumivoid.iwtcms.server.util.Config
import ua.pp.lumivoid.iwtcms.server.util.McHandler

internal object PlayersWS : WebSocket() {
    override val path = "/ws/players"

    private val sendPeriod = Config.readConfig().playerInfoPeriod.toLong()

    override val ws: Routing.() -> Unit = {
        webSocket(path) {
            val status = doAuth(
                call = call as RoutingCall,
                permission = PermissionsList.Permission.PLAYERS_MANAGE.value,
                success = {},
                unauthorized = {
                    logger.debug("Unauthorized user tried to connect to $path websocket")
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Unauthorized"))
                },
                forbidden = {
                    logger.debug("Forbidden user tried to connect to $path websocket")
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Forbidden"))
                },
            )

            if (status != HttpStatusCode.OK) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Auth Error"))
                return@webSocket
            }

            // and ws

            send(Frame.Text(Json.encodeToString(PlayersData.get()))) // First message always list, then every message contains info only about 1 player

            // Send
            val job = launch {
                var oldPlayersData = PlayersData.get()
                loop@ while (true) {
                    val newPlayersData = PlayersData.get()
                    val changedPlayers = mutableListOf<PlayersData.PlayerData>()
                    if (oldPlayersData != newPlayersData) {
                        if (oldPlayersData.players.size != newPlayersData.players.size) {
                            send(Frame.Text(Json.encodeToString(newPlayersData)))
                            oldPlayersData = newPlayersData
                            delay(sendPeriod)
                            continue
                        }

                        oldPlayersData.players.forEachIndexed { i, oldPlayer ->
                            if (oldPlayer.username != newPlayersData.players[i].username) { // Some players changed on server
                                send(Frame.Text(Json.encodeToString(newPlayersData)))
                                oldPlayersData = newPlayersData
                                changedPlayers.clear()
                                delay(sendPeriod)
                                continue@loop
                            }


                            if (oldPlayer != newPlayersData.players[i] && oldPlayer.username == newPlayersData.players[i].username) {
                                val changedPlayer = newPlayersData.players[i].copy()
                                if (oldPlayer.inventory == changedPlayer.inventory) {
                                    changedPlayer.inventory = null
                                }
                                changedPlayers.add(changedPlayer)
                            }

                        }
                    }

                    if (changedPlayers.isNotEmpty()) {
                        send(
                            Frame.Text(
                                Json.encodeToString(
                                    PlayersData(
                                        isAllData = false,
                                        players = changedPlayers
                                    )
                                )
                            )
                        )
                        oldPlayersData = newPlayersData
                        changedPlayers.clear()
                    }
                    delay(sendPeriod)
                }
            }


            // Receive
            runCatching {
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        // send on every message
                        send(Frame.Text(Json.encodeToString(PlayersData.get())))
                    }
                }
            }.onFailure { exception ->
                logger.error("WebSocket exception: $exception")
            }.also {
                job.cancel()
            }
        }
    }

    private fun parsePlayer(player: McHandler.Player): PlayersData.PlayerData {
        val playerInventory = mutableListOf<PlayersData.PlayerData.Slot>()

        player.inventory.forEach { itemStack ->
            playerInventory.add(
                PlayersData.PlayerData.Slot(
                    itemId = if (itemStack.count == 0) null else itemStack.id,
                    count = if (itemStack.count == 0) null else itemStack.count,
                )
            )
        }

        return PlayersData.PlayerData(
            pos = PlayersData.PlayerData.Pos(
                player.x,
                player.y,
                player.z
            ),
            username = player.name,
            uuid = player.uuid,
            gameMode = player.gameMode,
            permissionLevel = player.permissionLevel,
            inventory = playerInventory,
        )
    }

    @Serializable
    private data class PlayersData(
        val isAllData: Boolean,
        val players: List<PlayerData>
    ) {
        companion object {
            fun get(): PlayersData {
                val players = mutableListOf<PlayerData>()

                IWTCMS.instance.players().forEach { player ->
                    players.add(parsePlayer(player))
                }

                return PlayersData(
                    isAllData = true,
                    players = players,
                )
            }
        }

        @Serializable
        data class PlayerData(
            val pos: Pos,
            val username: String,
            val uuid: String,
            val gameMode: Int,
            val permissionLevel: Int,
            var inventory: List<Slot>?
        ) {
            @Serializable
            data class Pos(
                val x: Double,
                val y: Double,
                val z: Double
            )

            @Serializable
            data class Slot(
                val itemId: String?,
                val count: Int?,
            )
        }
    }
}

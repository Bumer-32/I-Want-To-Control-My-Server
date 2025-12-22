package ua.pp.lumivoid.iwtcms.util

import kotlinx.coroutines.runBlocking
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.server.MinecraftServer
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.upsert
import ua.pp.lumivoid.iwtcms.server.tables.MetaTable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * I don't know any other way to get Minecraft Server instance,
 * So I created this handler
 * When server starting variable server initializing with Minecraft Server instance
 *
 * Ez way to get Minecraft Server instance
 */
object MinecraftServerHandler {
    var server: MinecraftServer? = null
    private var requestStop = false // Using for stop server BEFORE it launch, when we didn't have a server instance

    @OptIn(ExperimentalTime::class)
    fun register() {
        ServerLifecycleEvents.SERVER_STARTING.register { server ->
            this.server = server
            if (requestStop) server.stop(false)
            runBlocking {
                suspendTransaction {
                    MetaTable.upsert { it[key] = "last_shutdown_at"; it[value] = Instant.fromEpochMilliseconds(System.currentTimeMillis()).toString() }
                }
            }
        }
    }
}

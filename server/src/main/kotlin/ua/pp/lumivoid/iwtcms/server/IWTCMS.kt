package ua.pp.lumivoid.iwtcms.server

import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.upsert
import ua.pp.lumivoid.iwtcms.server.tables.MetaTable
import ua.pp.lumivoid.iwtcms.server.util.CustomLogger
import ua.pp.lumivoid.iwtcms.server.util.McHandler
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

object IWTCMS {
    private val logger = Constants.LOGGER

    internal lateinit var instance: McHandler private set

    /**
     * Call this in your mod as early as you can, only way to launch iwtcms
     */
    fun init(handler: McHandler) {
        instance = handler

        Entry.launch()
    }

    /**
     * Optional but recommended call, launches some useful features like auto opening iwtcms page
     * Call on default mod initialization process
     */
    fun lateInit() {
        LateEntry.launch()
    }

    /**
     * Stops iwtcms internal server, not minecraft server
     *
     * Technically, you may not even call this function, but whatever you don't be able to normal stop of server
     * Even after typing stop in console iwtcms will be still running and process won't die until crash
     * Also some data can be broken if you didn't use this at stop
     *
     * Usually calls after minecraft server stop.
     */
    @OptIn(ExperimentalTime::class)
    fun stop() {
        if (LateEntry.h2Server != null) {
            logger.info("Shutting down h2 webserver")
            LateEntry.h2Server!!.stop()
        }

        if (LateEntry.viteProcess != null) {
            logger.info("Shutting down vite process")
            LateEntry.viteProcess!!.destroy()
        }

        KtorServer.shutdown()
        CustomLogger.shutdown()

        runBlocking {
            suspendTransaction {
                MetaTable.upsert { it[key] = "last_shutdown_at"; it[value] = Instant.fromEpochMilliseconds(System.currentTimeMillis()).toString() }
            }
        }
    }
}
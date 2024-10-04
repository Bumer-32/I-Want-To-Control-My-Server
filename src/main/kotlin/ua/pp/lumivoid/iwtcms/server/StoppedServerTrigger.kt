package ua.pp.lumivoid.iwtcms.server

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import ua.pp.lumivoid.iwtcms.Constants
import ua.pp.lumivoid.iwtcms.util.CustomLogger

/**
 * Trigger of Minecraft Server stopping
 *
 * Also, you can request any log in the last possible log time,
 * for example, for error in config, then user can see your log at the end of it.
 */
object StoppedServerTrigger {
    private val requestedLogs: MutableList<() -> Unit> = mutableListOf()

    fun register() {
        ServerLifecycleEvents.SERVER_STOPPED.register {
            Constants.SERVER_INSTANCE?.shutdown()
            requestedLogs.forEach { it.invoke() }
            CustomLogger.shutdown()
        }
    }

    /**
     * Request log in last possible log time
     *
     * After all logs, logger will be stopped
     */
    fun requestLog(log: () -> Unit) {
        requestedLogs.add(log)
    }
}
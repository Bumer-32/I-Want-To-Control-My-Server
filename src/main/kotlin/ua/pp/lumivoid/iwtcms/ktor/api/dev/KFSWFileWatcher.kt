package ua.pp.lumivoid.iwtcms.ktor.api.dev

import io.github.irgaly.kfswatch.KfsDirectoryWatcher
import io.github.irgaly.kfswatch.KfsDirectoryWatcherEvent
import io.github.irgaly.kfswatch.KfsEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.pp.lumivoid.iwtcms.Constants
import java.io.File

object KFSWFileWatcher {
    private val logger = Constants.EMBEDDED_SERVER_LOGGER
    private val scope = CoroutineScope(Dispatchers.Default)
    private val watcher = KfsDirectoryWatcher(scope)

    suspend fun run() {
        logger.info("Starting KFSW file watcher")

        // recursive
        watcher.add(Constants.KFSW_SRC_WEB_FOLDER)
        logger.info("Watching: ${Constants.KFSW_SRC_WEB_FOLDER}")
        File(Constants.KFSW_SRC_WEB_FOLDER).walkTopDown().forEach {
            if (it.isDirectory) {
                watcher.add(it.absolutePath)
                logger.info("Watching: ${it.absolutePath}")
            }
        }

        var oldEvent: KfsDirectoryWatcherEvent? = null // sometimes event duplicates, so we can check is it a duplicate
        var oldEventTime: Long = 0

        scope.launch {
            watcher.onEventFlow.collect { event: KfsDirectoryWatcherEvent ->
                if (oldEvent == event && System.currentTimeMillis() - oldEventTime < 1000) {
                    return@collect
                }

                oldEvent = event
                oldEventTime = System.currentTimeMillis()

                // recursive watch
                if (File("${event.targetDirectory}/${event.path}").isDirectory && event.event == KfsEvent.Create) {
                    watcher.add("${event.targetDirectory}/${event.path}")
                    logger.info("Watching: ${event.targetDirectory}/${event.path}")
                }

                if (event.event == KfsEvent.Delete) {
                    watcher.remove("${event.targetDirectory}/${event.path}")
                    //logger.info("Stop watching: ${event.targetDirectory}/${event.path}")
                }

                logger.info("Event: ${event.event} ${event.path}")
                WebCompile.compile(event)
            }
        }
    }

    suspend fun stop() {
        watcher.removeAll()
        watcher.close()
    }
}
package ua.pp.lumivoid.iwtcms.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.commons.io.output.ByteArrayOutputStream
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.Logger
import org.apache.logging.log4j.core.appender.FileAppender
import org.apache.logging.log4j.core.appender.WriterAppender
import org.apache.logging.log4j.core.config.Configurator
import org.apache.logging.log4j.core.layout.PatternLayout
import ua.pp.lumivoid.iwtcms.Constants
import ua.pp.lumivoid.iwtcms.ktor.api.requests.LogsHistoryGET
import ua.pp.lumivoid.iwtcms.ktor.api.websockets.ConsoleWS
import java.io.IOException
import java.io.OutputStreamWriter

object CustomLogger {
    private val logger = Constants.LOGGER

    private val output = ByteArrayOutputStream()
    private var running = false

    fun setup() {
        val rootLogger = LogManager.getRootLogger() as Logger

        val layout = PatternLayout.newBuilder()
            .withPattern("[%d{HH:mm:ss}] [%t/%level] (%logger{1}) %msg%n")
            .build()

        val appender = FileAppender.newBuilder()
            .withFileName("logs/iwtcms.log")
            .setName("CustomFileAppender")
            .setLayout(layout)
            .withAppend(false)
            .build()

        // custom appender

        val customAppender = WriterAppender.newBuilder()
            .setLayout(layout)
            .setName("CustomWriterAppender")
            .setIgnoreExceptions(false)
            .setTarget(OutputStreamWriter(output))
            .build()

        customAppender.start()
        rootLogger.addAppender(customAppender)

        CoroutineScope(Dispatchers.Default).launch {
            try {
                running = true
                while (running) {
                    @Suppress("Deprecation")
                    val log = output.toString()
                    if (log.isNotEmpty()) {
                        LogsHistoryGET.addLog(log)
                        ConsoleWS.asWs()?.sendMessage(log)
                        output.reset()
                    }
                }
            } catch (_: IOException) {
            }
        }

        appender.start()
        rootLogger.addAppender(appender)

        Configurator.setLevel(rootLogger.name, Level.getLevel(Config.readConfig().logLevel))

        logger.info("Initialized new logger") // After logger added, to log this
        logger.info("Logging level - ${rootLogger.level}")
        logger.info("Started logging")
    }

    fun shutdown() {
        logger.info("Shutting down logger")
        running = false
    }
}
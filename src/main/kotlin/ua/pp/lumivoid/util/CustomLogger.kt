package ua.pp.lumivoid.util

import org.apache.commons.io.output.ByteArrayOutputStream
import org.apache.logging.log4j.Level.INFO
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.Logger
import org.apache.logging.log4j.core.appender.FileAppender
import org.apache.logging.log4j.core.appender.WriterAppender
import org.apache.logging.log4j.core.config.Configurator
import org.apache.logging.log4j.core.layout.PatternLayout
import ua.pp.lumivoid.Constants
import ua.pp.lumivoid.server.Server
import java.io.IOException
import java.io.OutputStreamWriter
import kotlin.concurrent.thread

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

        thread {
            try {
                running = true
                while (running) {
                    val logs = output.toString()
                    if (logs.isNotEmpty()) {
                        Server.broadcast(logs)
                        output.reset()
                    }
                }
            } catch (e: IOException) {
                //e.stackTrace.forEach { logger.error(it.toString()) }
                //bufferedReader.close()
            } finally {
                //bufferedReader.close()
            }
        }

        appender.start()
        rootLogger.addAppender(appender)

        Configurator.setLevel(rootLogger.name, INFO)

        logger.info("Initialized new logger") // After logger added, to log this
        logger.info("Logging level - ${rootLogger.level}")
        logger.info("Started logging")
    }

    fun shutdown() {
        logger.info("Shutting down logger")
        running = false
    }
}
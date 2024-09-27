package ua.pp.lumivoid

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint
import org.apache.logging.log4j.Level.INFO
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.Logger
import org.apache.logging.log4j.core.appender.FileAppender
import org.apache.logging.log4j.core.layout.PatternLayout
import org.apache.logging.log4j.core.config.Configurator
import org.slf4j.LoggerFactory

object PREIWTCMS: PreLaunchEntrypoint {
    private val logger = LoggerFactory.getLogger("iwtcms pre launch")
    @Suppress("DEPRECATION")
    override fun onPreLaunch() {
        val rootLogger = LogManager.getRootLogger() as Logger

        val layout = PatternLayout.newBuilder()
            .withPattern("[%d{HH:mm:ss}] [%t/%level] (%logger{1}) %msg%n")
            .build()

        val appender = FileAppender.newBuilder()
            .withFileName("logs/iwtcms.log")
            .withName("CustomFileAppender")
            .withLayout(layout)
            .withAppend(false)
            .build()

        appender.start()
        rootLogger.addAppender(appender)

        Configurator.setLevel(rootLogger.name, INFO)

        logger.info("Initialized new logger") // After logger added, to log this
        logger.info("Logging level - ${rootLogger.level}")
        logger.info("Started logging")
    }
}
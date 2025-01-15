package ua.pp.lumivoid.iwtcms

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint
import org.slf4j.LoggerFactory
import ua.pp.lumivoid.iwtcms.ktor.KtorServer
import ua.pp.lumivoid.iwtcms.ktor.util.Config
import ua.pp.lumivoid.iwtcms.ktor.util.ErrorMessages
import ua.pp.lumivoid.iwtcms.util.CustomLogger
import java.io.File
import kotlin.system.exitProcess

object PREIWTCMS: PreLaunchEntrypoint {
    private val logger = LoggerFactory.getLogger("iwtcms pre launch")
    override fun onPreLaunch() {
        logger.info("Initialize pre launch iwtcms")
        checkForDevMode()
        CustomLogger.setup()
        KtorServer.setup()
    }

    private fun checkForDevMode() {
        if (Config.readConfig().devMode) {
            if (File("${Constants.CONFIG_FOLDER}/../../gradlew.bat").exists()) { // Check is mod launched in "developer environment"
                ErrorMessages.DEV_MODE.launch(logger)
            } else {
                ErrorMessages.NOT_A_DEVELOPER.launch(logger)
                exitProcess(1)
            }
        }
    }
}
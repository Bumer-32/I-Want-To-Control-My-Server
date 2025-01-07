package ua.pp.lumivoid.iwtcms

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint
import org.slf4j.LoggerFactory
import ua.pp.lumivoid.iwtcms.ktor.KtorServer
import ua.pp.lumivoid.iwtcms.util.Config
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
                logger.warn("")
                logger.warn("")
                logger.warn("")
                logger.warn("")
                logger.warn("")
                logger.warn("+-----------------------------------------------------------------------------------------+")
                logger.warn("|                                                                                         |")
                logger.warn("|                       SEEMS LIKE YOU LAUNCHED IWTCMS IN DEV MODE                        |")
                logger.warn("|                     BE CAREFUL, IT MAY CONTAIN BUGS AND BE UNSTABLE                     |")
                logger.warn("|                                                                                         |")
                logger.warn("|                IF YOU SEE THIS MESSAGE BUT DID NOT ENABLE DEV MODE, PLEASE              |")
                logger.warn("|                          CONTACT THE DEVELOPERS ON GITHUB                               |")
                logger.warn("|                                                                                         |")
                logger.warn("|                          IWTCMS CARES ABOUT YOUR SECURITY!                              |")
                logger.warn("|                                                                                         |")
                logger.warn("+-----------------------------------------------------------------------------------------+")
                logger.warn("")
                logger.warn("")
                logger.warn("")
                logger.warn("")
                logger.warn("")
            } else {
                logger.error("")
                logger.error("")
                logger.error("")
                logger.error("")
                logger.error("")
                logger.error("+-----------------------------------------------------------------------------------------+")
                logger.error("|                                                                                         |")
                logger.error("|                     IT SEEMS YOU LAUNCHED IWTCMS IN DEV MODE                            |")
                logger.error("|                            BUT YOU ARE NOT A DEVELOPER!                                 |")
                logger.error("|            YOU WERE INSTRUCTED NOT TO ENABLE THIS OPTION IN THE CONFIG!                 |")
                logger.error("|                                                                                         |")
                logger.error("|                THE SERVER WILL NOT BE STARTED FOR SECURITY REASONS.                     |")
                logger.error("|                  DEV MODE MAY INTRODUCE BUGS AND VULNERABILITIES.                       |")
                logger.error("|                                                                                         |")
                logger.error("|                      PLEASE DISABLE DEV MODE IN THE CONFIG!                             |")
                logger.error("|                                                                                         |")
                logger.error("|              IF YOU SEE THIS MESSAGE BUT DID NOT ENABLE DEV MODE, PLEASE                |")
                logger.error("|                          CONTACT THE DEVELOPERS ON GITHUB                               |")
                logger.error("|                                                                                         |")
                logger.error("|                       IWTCMS CARES ABOUT YOUR SECURITY!                                 |")
                logger.error("|                                                                                         |")
                logger.error("+-----------------------------------------------------------------------------------------+")

                exitProcess(1)
            }
        }
    }
}
package ua.pp.lumivoid.iwtcms.server.util

import io.ktor.util.logging.Logger

@Suppress("DuplicatedCode")
internal enum class ErrorMessages {
    DEV_MODE {
        override fun launch(logger: Logger) {
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
        }
    },
    NOT_A_DEVELOPER {
        override fun launch(logger: Logger) {
            logger.error("###########################################################################################")
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
        }
    },
    BAD_CERTIFICATE {
        override fun launch(logger: Logger) {
            logger.error("###########################################################################################")
            logger.error("")
            logger.error("")
            logger.error("")
            logger.error("")
            logger.error("")
            logger.error("+-----------------------------------------------------------------------------------------+")
            logger.error("|                                                                                         |")
            logger.error("|                       IWTCMS STOPPED SERVER FOR SECURITY REASONS                        |")
            logger.error("|                                                                                         |")
            logger.error("|                              ERROR: CAN'T FIND SSL KEYS,                                |")
            logger.error("|                           MAYBE, PROBLEM WITH CONFIG FILE?                              |")
            logger.error("|                        FIX CONFIG FILE AND THEN RESTART SERVER                          |")
            logger.error("|                                                                                         |")
            logger.error("|                   IF YOU FIXED THE SSL KEYS BUT STILL GET THIS ERROR,                   |")
            logger.error("|                          CONTACT THE DEVELOPERS ON GITHUB                               |")
            logger.error("|                                                                                         |")
            logger.error("|                          IWTCMS CARES ABOUT YOUR SECURITY!                              |")
            logger.error("|                                                                                         |")
            logger.error("+-----------------------------------------------------------------------------------------+")
        }
    },
    BAD_CONFIG {
        override fun launch(logger: Logger) {
            logger.error("###########################################################################################")
            logger.error("")
            logger.error("")
            logger.error("")
            logger.error("")
            logger.error("")
            logger.error("+-----------------------------------------------------------------------------------------+")
            logger.error("|                                                                                         |")
            logger.error("|                       IWTCMS STOPPED SERVER FOR SECURITY REASONS                        |")
            logger.error("|                              ERROR: CAN'T READ CONFIG FILE                              |")
            logger.error("|                        FIX CONFIG FILE AND THEN RESTART SERVER                          |")
            logger.error("|                                                                                         |")
            logger.error("|                     OLD CONFIG FILE RENAMED WITH *-BAD SUFFIX                           |")
            logger.error("|                              NEW CONFIG MUST BE GENERATED                               |")
            logger.error("|                                                                                         |")
            logger.error("|                IF YOU FIXED THE CONFIG FILE BUT STILL GET THIS ERROR,                   |")
            logger.error("|                          CONTACT THE DEVELOPERS ON GITHUB                               |")
            logger.error("|                                                                                         |")
            logger.error("|                          IWTCMS CARES ABOUT YOUR SECURITY!                              |")
            logger.error("|                                                                                         |")
            logger.error("+-----------------------------------------------------------------------------------------+")
        }
    }, ;

    abstract fun launch(logger: Logger)

    companion object {
        fun printStackTrace(
            logger: Logger,
            e: Exception,
        ) {
            logger.error("###########################################################################################")
            logger.error("PRINTING STACK TRACE:")
            e.stackTrace.forEach { logger.error(it.toString()) }
            logger.error("")
        }
    }
}

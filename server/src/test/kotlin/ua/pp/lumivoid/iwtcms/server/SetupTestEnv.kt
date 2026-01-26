package ua.pp.lumivoid.iwtcms.server

import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import ua.pp.lumivoid.iwtcms.server.util.Config
import ua.pp.lumivoid.iwtcms.server.util.Config.ConfigData
import ua.pp.lumivoid.iwtcms.server.util.Config.DbDriver

object SetupTestEnv: BeforeAllCallback {
    override fun beforeAll(context: ExtensionContext) {
        Config.setConfig(
            ConfigData(
                ip = "test",
                port = 25566,
                logLevel = "DEBUG",
                databaseUser = "test",
                databasePassword = "test",
                useSSL = false,
                customCertificate = false,
                sslAlias = "test",
                sslPass = "test",
                statisticsPeriod = 1000,
                playerInfoPeriod = 1000,
                enableIWTCMSControlPanel = true,
                autoOpenIWTCMSPageOnStartup = false,

                // dev
                devMode = false,
                autoOpenVite = false,
                enableH2WebServer =  false,
                useExternalDb = false,
                externalDbDriver = DbDriver.MariaDB,
                externalDbIWTCMSName = "iwtcms_test",
                externalDbIp = "test",
                externalDbPort = 9092
            )
        )

        connectToTestDb()

        IWTCMS.testInit(TestMcHandlerImpl)
    }
}
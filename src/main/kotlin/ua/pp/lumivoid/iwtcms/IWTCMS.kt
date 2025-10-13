package ua.pp.lumivoid.iwtcms

import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import org.h2.tools.Server
import ua.pp.lumivoid.iwtcms.ktor.util.Config
import ua.pp.lumivoid.iwtcms.util.MinecraftServerHandler
import ua.pp.lumivoid.iwtcms.util.StoppedServerTrigger
import java.awt.Desktop
import java.lang.ProcessBuilder.Redirect
import java.net.URI

object IWTCMS : ModInitializer {
    private val logger = Constants.LOGGER

    var viteProccess: Process? = null
    var h2Server: Server? = null

    override fun onInitialize() {
        logger.info("Hello from Bumer_32!")

        val config = Config.readConfig()

        MinecraftServerHandler.register()
        StoppedServerTrigger.register()

        if (FabricLoader.getInstance().isModLoaded(Constants.SPARK_FABRIC_ID)) {
            logger.info("Spark found!")
        }

        // ? run "npm run dev" if dev mode enabled
        if (config.devMode) {
            // run h2 db webserver
            if (config.enableH2WebServer && !config.useExternalDb) {
                h2Server = Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start()
                logger.info("H2 Console accessible by address: http://localhost:8082")
            }

            if (config.autoOpenVite) {
                val runnerFile =
                    if (System.getProperty("os.name").startsWith("Win")) {
                        "devRunner.bat"
                    } else {
                        "devRunner.sh"
                    }

                viteProccess =
                    ProcessBuilder("${Constants.CONFIG_FOLDER}/../../$runnerFile", "runDev")
                        .redirectOutput(Redirect.INHERIT)
                        .redirectError(Redirect.INHERIT)
                        .start()
            }
        }

        // If dev mode enabled, it will be opened by vite
        if (config.autoOpenIWTCMSPageOnStartup && !config.devMode) {
            logger.info("Open IWTCMS page")
            @Suppress("HttpUrlsUsage")
            val prefix = if (config.useSSL) "https://" else "http://"
            Desktop.getDesktop().browse(URI("$prefix${config.ip}:${config.port}"))
        }
    }
}

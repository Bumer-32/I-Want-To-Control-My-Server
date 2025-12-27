package ua.pp.lumivoid.iwtcms.server

import org.h2.tools.Server
import ua.pp.lumivoid.iwtcms.server.util.Config
import java.awt.Desktop
import java.io.File
import java.net.URI

internal object LateEntry {
    private val logger = Constants.LOGGER

    var viteProcess: Process? = null
    var h2Server: Server? = null

    fun launch() {

        val config = Config.readConfig()

        // ? run "npm run dev" if dev mode enabled
        if (config.devMode) {
            // run h2 db webserver
            if (config.enableH2WebServer && !config.useExternalDb) {
                h2Server = Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start()
                logger.info("H2 Console accessible by address: http://localhost:8082")
            }

            if (config.autoOpenVite) {
                val npm = if (System.getProperty("os.name").startsWith("Win")) {
                    "npm.cmd"
                } else {
                    "npm"
                }

                viteProcess = ProcessBuilder(npm, "run", "dev")
                                .directory(File("${Constants.CONFIG_FOLDER}/../../../front"))
                                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                                .redirectError(ProcessBuilder.Redirect.INHERIT)
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
package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.server.http.content.staticFiles
import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.Routing
import kotlinx.coroutines.runBlocking
import ua.pp.lumivoid.iwtcms.Constants
import ua.pp.lumivoid.iwtcms.ktor.api.dev.KFSWFileWatcher
import ua.pp.lumivoid.iwtcms.ktor.api.requests.ApiListGET.registerAPI
import ua.pp.lumivoid.iwtcms.util.Config
import java.io.File

object MainGET: Request() {
    override val logger = Constants.EMBEDDED_SERVER_LOGGER
    override val PATH =  "/"

    override val request: Routing.() -> Unit = {
        logger.info("Initializing $PATH request")
        registerAPI("MainGET", PATH)
        if (!Config.readConfig().devMode) {
            if (Config.readConfig().enableIWTCMSControlPanel) {
                staticResources(PATH, "web", index = "index.html")
            } else {
                staticResources(PATH, "disabledWeb", index = "index.html")
            }
        } else {
            runBlocking { KFSWFileWatcher.run() }
            staticFiles(PATH, File(Constants.DEV_WEB_FOLDER), index = "index.html")
        }
    }
}
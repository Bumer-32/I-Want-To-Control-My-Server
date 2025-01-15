package ua.pp.lumivoid.iwtcms.ktor.api.requests

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receiveText
import io.ktor.server.response.respondFile
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.put
import kotlinx.coroutines.runBlocking
import ua.pp.lumivoid.iwtcms.Constants
import ua.pp.lumivoid.iwtcms.ktor.api.UserAuthentication
import java.io.File

@Suppress("DuplicatedCode")
object IWTCMSSettingsGP: Request() {
    override val PATH = "/api/iwtcmsSettings"
    private val file = File(Constants.CONFIG_FILE)
    private val backupFile = File("${Constants.CONFIG_FOLDER}/__BACKUP__${file.name}")

    override val request: Routing.() -> Unit = {
        get(PATH) {
            UserAuthentication.doAuth(
                call = call,
                permit = "read iwtcms config",
                success = {
                    runBlocking { call.respondFile(file) }
                }
            )
        }

        put(PATH) {
            UserAuthentication.doAuth(
                call = call,
                permit = "edit iwtcms config",
                success = {
                    val text = runBlocking { call.receiveText() }

                    if (file.readText() == text) {
                        runBlocking { call.respondText("Not modified", status = HttpStatusCode.NoContent) }
                        return@doAuth
                    }

                    logger.info("Request with new ${file.name} file received")
                    logger.info("Creating backup in ${backupFile.absolutePath}")

                    if (!backupFile.exists()) backupFile.createNewFile()
                    backupFile.writeText(file.readText())
                    logger.info("Backup created")

                    file.writeText(text)
                    logger.info("New ${file.name} file saved")

                    runBlocking { call.respondText("Created", status = HttpStatusCode.Created) }
                }
            )
        }
    }
}
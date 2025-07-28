package ua.pp.lumivoid.iwtcms.ktor.api.requests

import com.charleskorn.kaml.Yaml
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receiveText
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.put
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import ua.pp.lumivoid.iwtcms.Constants
import ua.pp.lumivoid.iwtcms.ktor.api.PermissionsList
import ua.pp.lumivoid.iwtcms.ktor.api.doAuth
import java.io.File

object Configs : Request() {
    override val path = "/api/config"
    private val availableConfigsSettingsFile = this.javaClass.getResource(Constants.AVAILABLE_CONFIGS_SETTINGS_FILE)!!

    override val request: Routing.() -> Unit = {
        val availableConfigsSettings: Map<String, AvailableConfigSetting> =
            Yaml.default.decodeFromString(
                availableConfigsSettingsFile.readText(),
            )

        get(path) {
            call.respondText(availableConfigsSettingsFile.readText(), contentType = ContentType.Text.Plain)
        }

        availableConfigsSettings.forEach {
            val configPath = "$path/${it.value.selector_name}"
            logger.info("       - config ${it.key} url: $configPath")

            //also create permissions
            PermissionsList.createPermission("config.read.${it.value.selector_name}")
            PermissionsList.createPermission("config.write.${it.value.selector_name}")

            get(configPath) {
                doAuth(
                    call = call,
                    permission = "config.read.${it.value.selector_name}",
                    success = {
                        val filePath = "${System.getProperty("user.dir")}${it.value.config_path}"
                        val file = File(filePath)
                        val fileContent = file.readText()
                        runBlocking { call.respondText(fileContent, contentType = ContentType.Text.Plain) }
                    },
                )
            }

            put(configPath) {
                doAuth(
                    call = call,
                    permission = "config.write.${it.value.selector_name}",
                    success = {
                        val filePath = "${System.getProperty("user.dir")}${it.value.config_path}"
                        val file = File(filePath)
                        val backupFilePath = "${Constants.CONFIG_FOLDER}/__BACKUP__${file.name}"
                        val backupFile = File(backupFilePath)

                        if (it.value.make_backup) {
                            if (!backupFile.exists()) backupFile.createNewFile()
                            backupFile.writeText(file.readText())
                            logger.info("Backup created")
                        }

                        val fileContent = runBlocking { call.receiveText() }
                        file.writeText(fileContent)

                        runBlocking { call.respondText("Created", status = HttpStatusCode.Created) }
                    },
                )
            }

            get("$path/${it.value.selector_name}/strategy") {

                doAuth(
                    call = call,
                    permission = "config.read.${it.value.selector_name}",
                    success = {
                        val filePath = "/${it.value.config_path.split("/").last()}.strategy.yaml"
                        val file = this.javaClass.getResource(filePath)!!
                        val fileContent = file.readText()
                        runBlocking { call.respondText(fileContent, contentType = ContentType.Text.Plain) }
                    },
                )
            }
        }
    }

    @Suppress("PropertyName")
    @Serializable
    data class AvailableConfigSetting(
        val selector_name: String,
        val config_name: String,
        val config_type: String,
        val config_path: String,
        val make_backup: Boolean
    )
}
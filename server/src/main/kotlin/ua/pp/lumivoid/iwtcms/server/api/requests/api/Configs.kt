package ua.pp.lumivoid.iwtcms.server.api.requests.api

import com.charleskorn.kaml.Yaml
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import ua.pp.lumivoid.iwtcms.server.Constants
import ua.pp.lumivoid.iwtcms.server.api.Request
import ua.pp.lumivoid.iwtcms.server.api.doAuth
import ua.pp.lumivoid.iwtcms.server.api.requests.api.user.PermissionsList
import java.io.File

internal object Configs : Request() {
    override val path = "/api/config"
    private val availableConfigsSettingsFile = javaClass.getResource(Constants.AVAILABLE_CONFIGS_SETTINGS_FILE)!!

    override val request: Routing.() -> Unit = {
        val availableConfigsSettings: Map<String, AvailableConfigSetting> = Yaml.default.decodeFromString(availableConfigsSettingsFile.readText())

        get(path) {
            call.respondText(availableConfigsSettingsFile.readText(), ContentType.Application.Yaml)
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
                        call.respond(fileContent)
                    },
                )
            }

            put(configPath) {
                doAuth(
                    call = call,
                    permission = "config.write.${it.value.selector_name}",
                    success = {
                        val file = File("${System.getProperty("user.dir")}${it.value.config_path}")
                        val backupFile = File("${Constants.CONFIG_FOLDER}/__BACKUP__${file.name}")

                        if (it.value.make_backup) {
                            if (!backupFile.exists()) backupFile.createNewFile()
                            backupFile.writeText(file.readText())
                            logger.info("Backup created")
                        }

                        val fileContent = call.receiveText()
                        file.writeText(fileContent)

                        call.respond(HttpStatusCode.Created, "Created")
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
                        call.respond(fileContent)
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
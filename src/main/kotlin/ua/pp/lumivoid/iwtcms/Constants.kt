package ua.pp.lumivoid.iwtcms

import net.fabricmc.loader.api.FabricLoader
import org.slf4j.LoggerFactory

object Constants {
    const val MOD_ID = "iwtcms"
    val LOGGER = LoggerFactory.getLogger(MOD_ID)
    val EMBEDDED_SERVER_LOGGER = LoggerFactory.getLogger("iwtcms embedded server")
    val CONFIG_FOLDER = "${System.getProperty("user.dir",)}/$MOD_ID" // FabricLoader.getInstance().configDir.toString() // Temporary, idk why but FabricLoader works wrongly at jar https://github.com/Bumer-32/I-Want-To-Control-My-Server/issues/7
    val CONFIG_FILE = "$CONFIG_FOLDER/iwtcms.conf"
    val DB_FILE = "$CONFIG_FOLDER/db"
    val SSL_CERTIFICATE_FILE = "$CONFIG_FOLDER/keystore.jks"
    const val SPARK_FABRIC_ID = "spark"
    val MOD_VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).get().metadata.version.toString()
    const val SCHEMA_VERSION = "v1"
    const val AVAILABLE_CONFIGS_SETTINGS_FILE = "/availableConfigs.yaml" // AVAILABLE_CONFIGS_SETTINGS_FILE
    const val NOT_FOUND_HTML = "/disabledWeb/404.html"
}

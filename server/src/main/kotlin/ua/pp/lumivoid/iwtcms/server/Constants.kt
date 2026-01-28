package ua.pp.lumivoid.iwtcms.server

import org.slf4j.LoggerFactory

internal object Constants {
    val LOGGER = LoggerFactory.getLogger("iwtcms embedded server")

    val CONFIG_FOLDER = "${System.getProperty("user.dir",)}/iwtcms"
    val CONFIG_FILE = "$CONFIG_FOLDER/iwtcms.conf"
    val DB_FILE = "$CONFIG_FOLDER/db"
    val SSL_CERTIFICATE_FILE = "$CONFIG_FOLDER/keystore.jks"
    const val AVAILABLE_CONFIGS_SETTINGS_FILE = "/availableConfigs.yaml" // AVAILABLE_CONFIGS_SETTINGS_FILE
    const val NOT_FOUND_HTML = "/static/404.html"

    val MOD_VERSION = javaClass.getResource("/iwtcms.version")?.readText() ?: ""
    const val SCHEMA_VERSION = "v1"
}
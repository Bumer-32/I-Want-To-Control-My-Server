package ua.pp.lumivoid.iwtcms

import org.slf4j.LoggerFactory

object Constants {
    const val MOD_ID = "iwtcms"
    val LOGGER = LoggerFactory.getLogger(MOD_ID)
    val EMBEDDED_SERVER_LOGGER = LoggerFactory.getLogger("iwtcms embedded server")
    val CONFIG_FOLDER = "${System.getProperty("user.dir")}/config/$MOD_ID" // FabricLoader.getInstance().configDir.toString() // Temporary, idk why but FabricLoader works wrong at jar https://github.com/Bumer-32/I-Want-To-Control-My-Server/issues/7
    val CONFIG_FILE = CONFIG_FOLDER + "/iwtcms.conf"
    val SSL_SERTIFICATE_FILE = CONFIG_FOLDER + "/keystore.jks"
    const val SPARK_FABRIC_ID = "spark"
}
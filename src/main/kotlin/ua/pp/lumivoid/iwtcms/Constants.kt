package ua.pp.lumivoid.iwtcms

import net.fabricmc.loader.api.FabricLoader
import org.slf4j.LoggerFactory
import ua.pp.lumivoid.iwtcms.server.Server

object Constants {
    const val MOD_ID = "iwtcms"
    val LOGGER = LoggerFactory.getLogger(MOD_ID)
    val CONFIG_FILE = FabricLoader.getInstance().configDir.toString() + "\\iwtcms\\iwtcms.json"
    val SSL_SERTIFICATE_FILE = FabricLoader.getInstance().configDir.toString() + "\\iwtcms\\keystore.jks"

    var SERVER_INSTANCE: Server? = null
}
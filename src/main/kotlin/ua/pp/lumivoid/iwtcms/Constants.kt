package ua.pp.lumivoid.iwtcms

import net.fabricmc.loader.api.FabricLoader
import org.slf4j.LoggerFactory
import ua.pp.lumivoid.iwtcms.server.Server
import ua.pp.lumivoid.iwtcms.util.Config
import java.security.MessageDigest

object Constants {
    const val MOD_ID = "iwtcms"
    val LOGGER = LoggerFactory.getLogger(MOD_ID)
    val CONFIG_FILE = FabricLoader.getInstance().configDir.toString() + "\\iwtcms\\iwtcms.json"
    val SSL_SERTIFICATE_FILE = FabricLoader.getInstance().configDir.toString() + "\\iwtcms\\keystore.jks"

    var SERVER_INSTANCE: Server? = null

    val passwordHash: String = MessageDigest.getInstance("SHA-256").digest(Config.readConfig().authenticationPassword.toByteArray()).fold("", { str, it -> str + "%02x".format(it) })
}
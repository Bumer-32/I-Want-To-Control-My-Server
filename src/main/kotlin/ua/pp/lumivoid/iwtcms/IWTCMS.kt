package ua.pp.lumivoid.iwtcms

import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import ua.pp.lumivoid.iwtcms.ktor.api.dev.WebCompile
import ua.pp.lumivoid.iwtcms.ktor.util.Config
import ua.pp.lumivoid.iwtcms.util.StoppedServerTrigger
import ua.pp.lumivoid.iwtcms.util.MinecraftServerHandler
import java.awt.Desktop
import java.net.URI

object IWTCMS : ModInitializer {
	private val logger = Constants.LOGGER

	override fun onInitialize() {
		logger.info("Hello from Bumer_32!")

		MinecraftServerHandler.register()
		StoppedServerTrigger.register()

		if (FabricLoader.getInstance().isModLoaded(Constants.SPARK_FABRIC_ID)) {
			logger.info("Spark found!")
		}

		if (Config.readConfig().devMode) {
			WebCompile.compileAll()
		}

		if (Config.readConfig().autoOpenIWTCMSPageOnStartup) {
			logger.info("Open IWTCMS page")
			@Suppress("HttpUrlsUsage")
			val prefix = if (Config.readConfig().useSSL) "https://" else "http://"
			Desktop.getDesktop().browse(URI("$prefix${Config.readConfig().ip}:${Config.readConfig().port}"))
		}
	}
}
package ua.pp.lumivoid.iwtcms

import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import org.h2.tools.Server
import ua.pp.lumivoid.iwtcms.ktor.util.Config
import ua.pp.lumivoid.iwtcms.util.StoppedServerTrigger
import ua.pp.lumivoid.iwtcms.util.MinecraftServerHandler
import java.awt.Desktop
import java.lang.ProcessBuilder.Redirect
import java.net.URI

object IWTCMS : ModInitializer {
	private val logger = Constants.LOGGER

	var viteProccess: Process? = null
	var h2Server: Server? = null

	override fun onInitialize() {
		logger.info("Hello from Bumer_32!")

		MinecraftServerHandler.register()
		StoppedServerTrigger.register()

		if (FabricLoader.getInstance().isModLoaded(Constants.SPARK_FABRIC_ID)) {
			logger.info("Spark found!")
		}

		// ? run "npm run dev" if dev mode enabled
		if (Config.readConfig().devMode) {
			// run h2 db webserver
			h2Server = Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start()
			logger.info("H2 Console accessible by address: http://localhost:8082")



			if (Config.readConfig().autoOpenVite) {
				val runnerFile = if (System.getProperty("os.name").startsWith("Win")) {
					"devRunner.bat"
				} else {
					"devRunner.sh"
				}

				viteProccess = ProcessBuilder("${Constants.CONFIG_FOLDER}/../../$runnerFile", "runDev")
					.redirectOutput(Redirect.INHERIT)
					.redirectError(Redirect.INHERIT)
					.start()
			}
		}

		if (Config.readConfig().autoOpenIWTCMSPageOnStartup && !Config.readConfig().devMode) { // If dev mode enabled, it will be opened by vite
			logger.info("Open IWTCMS page")
			@Suppress("HttpUrlsUsage")
			val prefix = if (Config.readConfig().useSSL) "https://" else "http://"
			Desktop.getDesktop().browse(URI("$prefix${Config.readConfig().ip}:${Config.readConfig().port}"))
		}
	}
}
package ua.pp.lumivoid.iwtcms

import net.fabricmc.api.ModInitializer
import ua.pp.lumivoid.iwtcms.server.StoppedServerTrigger
import ua.pp.lumivoid.iwtcms.util.MinecraftServerStartedTrigger

object IWTCMS : ModInitializer {
	private val logger = Constants.LOGGER

	override fun onInitialize() {
		logger.info("Hello from Bumer_32!")

		MinecraftServerStartedTrigger.register()
		StoppedServerTrigger.register()
	}
}
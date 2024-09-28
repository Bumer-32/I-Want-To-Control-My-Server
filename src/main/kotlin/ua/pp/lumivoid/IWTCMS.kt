package ua.pp.lumivoid

import net.fabricmc.api.ModInitializer
import ua.pp.lumivoid.server.StoppedServerTrigger
import ua.pp.lumivoid.util.MinecraftServerStartedTrigger

object IWTCMS : ModInitializer {
	private val logger = Constants.LOGGER

	override fun onInitialize() {
		logger.info("Hello from Bumer_32!")

		MinecraftServerStartedTrigger.register()
		StoppedServerTrigger.register()
	}
}
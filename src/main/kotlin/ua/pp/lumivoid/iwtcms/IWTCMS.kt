package ua.pp.lumivoid.iwtcms

import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import ua.pp.lumivoid.iwtcms.util.StoppedServerTrigger
import ua.pp.lumivoid.iwtcms.util.MinecraftServerHandler

object IWTCMS : ModInitializer {
	private val logger = Constants.LOGGER

	override fun onInitialize() {
		logger.info("Hello from Bumer_32!")

		MinecraftServerHandler.register()
		StoppedServerTrigger.register()

		if (FabricLoader.getInstance().isModLoaded(Constants.SPARK_FABRIC_ID)) {
			logger.info("Spark found!")
		}
	}
}
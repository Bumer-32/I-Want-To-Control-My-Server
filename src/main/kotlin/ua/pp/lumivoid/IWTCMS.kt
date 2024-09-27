package ua.pp.lumivoid

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object IWTCMS : ModInitializer {
    private val logger = LoggerFactory.getLogger("iwtcms")

	override fun onInitialize() {
		logger.info("Hello from Bumer_32!")
	}
}
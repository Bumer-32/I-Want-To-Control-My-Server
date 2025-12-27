package ua.pp.lumivoid.iwtcms

import net.fabricmc.api.ModInitializer
import ua.pp.lumivoid.iwtcms.server.IWTCMS

object IWTCMSFabric : ModInitializer {
    override fun onInitialize() {
        IWTCMS.lateInit()
    }
}

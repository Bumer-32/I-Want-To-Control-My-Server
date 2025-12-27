package ua.pp.lumivoid.iwtcms

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint
import ua.pp.lumivoid.iwtcms.server.IWTCMS

object PREIWTCMSFabric : PreLaunchEntrypoint {
    override fun onPreLaunch() {
        IWTCMS.init(McHandlerImpl)
    }
}

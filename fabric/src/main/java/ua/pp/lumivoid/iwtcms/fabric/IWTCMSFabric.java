package ua.pp.lumivoid.iwtcms.fabric;

import net.fabricmc.api.ModInitializer;
import ua.pp.lumivoid.iwtcms.IWTCMSCommon;

public final class IWTCMSFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        IWTCMSCommon.INSTANCE.init();
    }
}

package ua.pp.lumivoid.iwtcms.neoforge;

import net.neoforged.fml.common.Mod;

import ua.pp.lumivoid.iwtcms.Constants;
import ua.pp.lumivoid.iwtcms.IWTCMSCommon;

@Mod(Constants.MOD_ID)
public final class IWTCMSNeoForge {
    public IWTCMSNeoForge() {
        // Run our common setup.
        IWTCMSCommon.INSTANCE.init();
    }
}

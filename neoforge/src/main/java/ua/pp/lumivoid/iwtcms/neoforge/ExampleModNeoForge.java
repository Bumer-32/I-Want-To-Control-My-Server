package ua.pp.lumivoid.iwtcms.neoforge;

import net.neoforged.fml.common.Mod;

import ua.pp.lumivoid.iwtcms.ExampleMod;

@Mod(ExampleMod.MOD_ID)
public final class ExampleModNeoForge {
    public ExampleModNeoForge() {
        // Run our common setup.
        ExampleMod.init();
    }
}

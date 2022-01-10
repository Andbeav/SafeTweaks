package net.fabricmc.safetweaks;

import net.fabricmc.api.ModInitializer;
// import net.fabricmc.safetweaks.config.FeatureFlagManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SafeTweaksClient implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LogManager.getLogger("modid");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

        // Set feature flags on launch | Apparently mixin runs first...
        // initializeFeatureFlags();

		LOGGER.info("SafeTweaks init!");
	}

    // private void initializeFeatureFlags() {
    //     // Will populate from the given file in FeatureFlagManager constructor
    //     FeatureFlagManager.getInstance((instance) -> {
    //         // Skip setting defaults unless the flags are empty
    //         if(instance.isEmpty()) { return; }

    //         // Set default flags here
    //         instance.set("renderDistanceFogToggle", false);
    //     });
    // }
}

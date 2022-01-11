package net.fabricmc.safetweaks;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.safetweaks.config.FeatureFlagManager;
// import net.fabricmc.safetweaks.config.FeatureFlagManager;
import net.fabricmc.safetweaks.config.KeyBindManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

import java.util.Map;
import java.util.Set;

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

		LOGGER.info("SafeTweaks init!");

        registerKeyBinds();
        registerDoubleSneakToggle();
	}

    private void registerKeyBinds() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            FeatureFlagManager flagManager = FeatureFlagManager.getInstance();
            for (Map.Entry<String, KeyBinding> entry : getKeyBindEntrySet()) {
                while(entry.getValue().wasPressed()) {
                    flagManager.set(entry.getKey(), !flagManager.get(entry.getKey()));
                    FeatureFlagManager.saveFlagsPersistent();
                    client.player.sendMessage(new LiteralText("SafeTweaks: Toggled " + new TranslatableText(entry.getKey()).getString()), false);
                }
            }

            // while(KeyBinding.wasPressed()) {
            //     client.player.sendMessage(new LiteralText("Unknown key was pressed"), false);
            // }
        });
    }

    private void registerDoubleSneakToggle() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if(mc.player == null) { return; }
            if(FeatureFlagManager.getInstance().get("config.safetweaks.double-sneak", false)) {
                mc.options.sneakToggled = true;
            } else {
                mc.options.sneakToggled = false;
            }
        });
    }

    private Set<Map.Entry<String,KeyBinding>> getKeyBindEntrySet() {
        return KeyBindManager.getInstance().getKeyBindings().entrySet();
    }
}

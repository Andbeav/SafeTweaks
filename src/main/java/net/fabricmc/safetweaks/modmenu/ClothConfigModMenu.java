package net.fabricmc.safetweaks.modmenu;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.safetweaks.config.FeatureFlagManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TranslatableText;

public class ClothConfigModMenu {

    private static FeatureFlagManager featureFlags = FeatureFlagManager.getInstance((instance) -> {
        // Skip setting defaults unless the flags are empty
        if(!instance.isEmpty()) { return; }

        // Set default flags here
        instance.set("renderDistanceFogToggle", false);
    });

    public static ConfigBuilder getConfigBuilderWithOptions() {

        // Am lazy so no persistent storage, flags will be disabled by default (in initializer mod)

        // Builder setup
        ConfigBuilder builder = ConfigBuilder.create().setTitle(new TranslatableText("config.safetweaks.title"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // MC Client (mostly for getting options)
        MinecraftClient client = MinecraftClient.getInstance();

        // Tweaks category
        ConfigCategory tweaks = builder.getOrCreateCategory(new TranslatableText("config.safetweaks.category"));

        // Gamma tweak
        Double gamma = client.options.gamma;
        tweaks.addEntry(entryBuilder.startDoubleField(new TranslatableText("config.safetweaks.gamma"), gamma).setDefaultValue(0.0).setSaveConsumer(newVal -> client.options.gamma = newVal).build());

        // Render distance fog override tweak
        tweaks.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("config.safetweaks.render-distance-fog"), featureFlags.get("renderDistanceFogToggle")).setDefaultValue(false).setSaveConsumer((newVal) -> { featureFlags.set("renderDistanceFogToggle", newVal); }).build());

        // On save
        builder.setSavingRunnable(() -> {
            FeatureFlagManager.saveFlagsPersistent();
        });

        // Returning the builder
        builder.transparentBackground();
        return builder;
    }
}

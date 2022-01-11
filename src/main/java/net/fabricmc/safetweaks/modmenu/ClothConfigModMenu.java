package net.fabricmc.safetweaks.modmenu;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.safetweaks.config.FeatureFlagManager;
import net.fabricmc.safetweaks.config.KeyBindManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.TranslatableText;

public class ClothConfigModMenu {

    private static FeatureFlagManager featureFlags = FeatureFlagManager.getInstance((instance) -> {
        // Skip setting defaults unless the flags are empty
        if(!instance.isEmpty()) { return; }

        // Set default flags here
        instance.set("config.safetweaks.render-distance-fog", false);
        instance.set("config.safetweaks.permanent-sneak", false);
        FeatureFlagManager.saveFlagsPersistent();
    });

    private static KeyBindManager keyBindManager = KeyBindManager.getInstance((instance) -> {
        if(!instance.isEmpty()) { return; }
        final String translatableCat = "key-binds.safetweaks.category";

        // Render distance keybind default
        final String fogOverrideID = "config.safetweaks.render-distance-fog";
        instance.set(fogOverrideID, new KeyBinding(fogOverrideID, InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(), translatableCat));

        // Double sneak toggle keybind default
        final String doubleSneakID = "config.safetweaks.double-sneak";
        instance.set(doubleSneakID, new KeyBinding(doubleSneakID, InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(), translatableCat));

        KeyBindManager.saveBindsPersistent();
    });

    public static ConfigBuilder getConfigBuilderWithOptions() {
        // Builder setup
        ConfigBuilder builder = ConfigBuilder.create().setTitle(new TranslatableText("config.safetweaks.title"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // Categories
        createTweaksCategory(builder, entryBuilder);
        createKeyBindsCategory(builder, entryBuilder);

        // On save
        builder.setSavingRunnable(() -> {
            FeatureFlagManager.saveFlagsPersistent();
            KeyBindManager.saveBindsPersistent();
        });

        // Returning the builder
        builder.transparentBackground();
        return builder;
    }

    // Tweaks category
    private static void createTweaksCategory(ConfigBuilder builder, ConfigEntryBuilder entryBuilder) {
        ConfigCategory tweaksCat = builder.getOrCreateCategory(new TranslatableText("config.safetweaks.tweaks-category"));

        // Gamma tweak
        MinecraftClient client = MinecraftClient.getInstance();
        tweaksCat.addEntry(entryBuilder.startDoubleField(new TranslatableText("config.safetweaks.gamma"), client.options.gamma).setDefaultValue(0.0).setSaveConsumer(newVal -> client.options.gamma = newVal).build());

        // Render distance fog override tweak
        tweaksCat.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("config.safetweaks.render-distance-fog"), featureFlags.get("config.safetweaks.render-distance-fog", false)).setDefaultValue(false).setSaveConsumer(newVal -> featureFlags.set("config.safetweaks.render-distance-fog", newVal)).build());

        // Double toggle sneak tweak
        tweaksCat.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("config.safetweaks.double-sneak"), featureFlags.get("config.safetweaks.double-sneak", false)).setDefaultValue(false).setSaveConsumer(newVal -> featureFlags.set("config.safetweaks.double-sneak", newVal)).build());
    }

    // KeyBinds category
    private static void createKeyBindsCategory(ConfigBuilder builder, ConfigEntryBuilder entryBuilder) {
        final String fogOverrideID = "config.safetweaks.render-distance-fog";
        final String doubleSneakID = "config.safetweaks.double-sneak";
        final String translatableCat = "key-binds.safetweaks.category";

        ConfigCategory keyBindsCat = builder.getOrCreateCategory(new TranslatableText("config.safetweaks.keybinds-category"));

        // Render distance fog toggle keyBind
        keyBindsCat.addEntry(entryBuilder.startKeyCodeField(new TranslatableText(fogOverrideID), keyBindManager.get(fogOverrideID).getDefaultKey()).setDefaultValue(InputUtil.UNKNOWN_KEY).setSaveConsumer(newVal -> {
            keyBindManager.set(fogOverrideID, new KeyBinding(fogOverrideID, InputUtil.Type.KEYSYM, newVal.getCode(), translatableCat));
        }).build());

        // Double toggle sneak tweak
        keyBindsCat.addEntry(entryBuilder.startKeyCodeField(new TranslatableText(doubleSneakID), keyBindManager.get(doubleSneakID).getDefaultKey()).setDefaultValue(InputUtil.UNKNOWN_KEY).setSaveConsumer(newVal -> {
            keyBindManager.set(doubleSneakID, new KeyBinding(doubleSneakID, InputUtil.Type.KEYSYM, newVal.getCode(), translatableCat));
        }).build());
    }
}

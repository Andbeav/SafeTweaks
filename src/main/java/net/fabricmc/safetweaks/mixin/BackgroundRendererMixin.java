package net.fabricmc.safetweaks.mixin;

// Taken from the temporary 1.18 implementation of tweakeroo: https://github.com/maruohon/tweakeroo/blob/fabric_1.18_temp_features/src/main/java/fi/dy/masa/tweakeroo/mixin/MixinBackgroundRenderer.java

import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.fabricmc.safetweaks.config.FeatureFlagManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {
    private static boolean wasLava;
    private static FeatureFlagManager featureFlagManager = FeatureFlagManager.getInstance();

    @ModifyConstant(
            method = "applyFog(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/BackgroundRenderer$FogType;FZ)V",
            slice = @Slice(
                            from = @At(value = "FIELD", target = "Lnet/minecraft/entity/effect/StatusEffects;FIRE_RESISTANCE:Lnet/minecraft/entity/effect/StatusEffect;"),
                            to   = @At(value = "FIELD", target = "Lnet/minecraft/entity/effect/StatusEffects;BLINDNESS:Lnet/minecraft/entity/effect/StatusEffect;")),
            constant = @Constant(floatValue = 0.25f),
            require = 0)
    private static float reduceLavaFogStart(float original)
    {
        wasLava = true;

        // if (/*FeatureToggle.TWEAK_LAVA_VISIBILITY.getBooleanValue()*/true)
        if(featureFlagManager.get("renderDistanceFogToggle"))
        {
            return 0.0f;
        }

        return original;
    }

    /*@ModifyConstant(
            method = "applyFog(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/BackgroundRenderer$FogType;FZ)V",
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/entity/effect/StatusEffects;FIRE_RESISTANCE:Lnet/minecraft/entity/effect/StatusEffect;"),
                    to   = @At(value = "FIELD", target = "Lnet/minecraft/entity/effect/StatusEffects;BLINDNESS:Lnet/minecraft/entity/effect/StatusEffect;")),
            constant = { @Constant(floatValue = 1.0f), @Constant(floatValue = 3.0f)},
            require = 0)
    private static float reduceLavaFogEnd(float original)
    {
        wasLava = true;

        if (FeatureToggle.TWEAK_LAVA_VISIBILITY.getBooleanValue())
        {
            return RenderUtils.getLavaFogDistance(MinecraftClient.getInstance().getCameraEntity(), original);
        }

        return original;
    }*/

    /*
    @ModifyVariable(
            method = "applyFog(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/BackgroundRenderer$FogType;FZ)V",
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/client/render/BackgroundRenderer$FogType;FOG_SKY:Lnet/minecraft/client/render/BackgroundRenderer$FogType;")),
            at = @At(value = "STORE", opcode = Opcodes.FSTORE, ordinal = 2), ordinal = 1)
    private static float overrideFogStart(float original)
    {
        if (Configs.Disable.DISABLE_RENDER_DISTANCE_FOG.getBooleanValue())
        {
            return Math.max(512, MinecraftClient.getInstance().gameRenderer.getViewDistance()) * 1.6f;
        }
        return original;
    }
    @ModifyVariable(
            method = "applyFog(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/BackgroundRenderer$FogType;FZ)V",
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/client/render/BackgroundRenderer$FogType;FOG_SKY:Lnet/minecraft/client/render/BackgroundRenderer$FogType;")),
            at = @At(value = "STORE", opcode = Opcodes.FSTORE, ordinal = 3), ordinal = 2)
    private static float overrideFogEnd(float original)
    {
        if (Configs.Disable.DISABLE_RENDER_DISTANCE_FOG.getBooleanValue())
        {
            return Math.max(512, MinecraftClient.getInstance().gameRenderer.getViewDistance()) * 2.0f;
        }
        return original;
    }
    */

    @Inject(method = "applyFog(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/BackgroundRenderer$FogType;FZ)V",
            require = 0,
            at = @At(value = "INVOKE", remap = false,
                     target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogEnd(F)V",
                     shift = At.Shift.AFTER))
    private static void disableRenderDistanceFog(
            Camera camera,
            BackgroundRenderer.FogType fogType,
            float viewDistance, boolean thickFog, CallbackInfo ci)
    {
        // if (/*Configs.Disable.DISABLE_RENDER_DISTANCE_FOG.getBooleanValue()*/true)
        if(featureFlagManager.get("renderDistanceFogToggle"))
        {
            if (thickFog == false && wasLava == false)
            {
                float distance = Math.max(512, MinecraftClient.getInstance().gameRenderer.getViewDistance());
                RenderSystem.setShaderFogStart(distance * 1.6F);
                RenderSystem.setShaderFogEnd(distance * 2.0F);
            }

            wasLava = false;
        }
    }
}

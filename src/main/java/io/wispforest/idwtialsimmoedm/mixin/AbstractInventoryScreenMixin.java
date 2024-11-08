package io.wispforest.idwtialsimmoedm.mixin;

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.sugar.Local;
import io.wispforest.idwtialsimmoedm.api.GatherDescriptionCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.StatusEffectsDisplay;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(StatusEffectsDisplay.class)
public abstract class AbstractInventoryScreenMixin {

    @Shadow
    protected abstract Text getStatusEffectDescription(StatusEffectInstance statusEffect);

    @Shadow
    @Final
    private HandledScreen<?> parent;
    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "drawStatusEffects(Lnet/minecraft/client/gui/DrawContext;II)V", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/List;of(Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;"))
    private List<Text> makeListBased(List<Text> liste) {
        return new ArrayList<>(liste);
    }

    @Inject(method = "drawStatusEffects(Lnet/minecraft/client/gui/DrawContext;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;Ljava/util/Optional;II)V"))
    private void addDescription(DrawContext context, int mouseX, int mouseY, CallbackInfo ci, @Local StatusEffectInstance statusEffectInstance, @Local List<Text> tooltip) {
        var description = GatherDescriptionCallback.STATUS_EFFECT.invoker().gatherDescription(statusEffectInstance.getEffectType().value());
        if (description == null) return;

        tooltip.clear();
        tooltip.add(((MutableText) this.getStatusEffectDescription(statusEffectInstance))
            .append(Text.literal(" ").append(StatusEffectUtil.getDurationText(statusEffectInstance, 1.0F, MinecraftClient.getInstance().world.getTickManager().getTickRate()))
                .formatted(Formatting.GRAY)));
        tooltip.addAll(Lists.reverse(description));
    }

    @Inject(method = "drawStatusEffects(Lnet/minecraft/client/gui/DrawContext;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/StatusEffectsDisplay;drawStatusEffectDescriptions(Lnet/minecraft/client/gui/DrawContext;IILjava/lang/Iterable;)V"))
    private void mald(DrawContext context, int mouseX, int mouseY, CallbackInfo ci, @Local(ordinal = 2) int i, @Local(ordinal = 4) int effectHeight, @Local Iterable<StatusEffectInstance> displayedEffects) {
        if (mouseX >= i && mouseX <= i + 121) {
            int yLowerBound = ((HandledScreenAccessor) this.parent).idwtialsimmoedm$getY();
            StatusEffectInstance hoveredEffect = null;

            for (var effect : displayedEffects) {
                if (mouseY >= yLowerBound && mouseY <= yLowerBound + effectHeight) {
                    hoveredEffect = effect;
                }

                yLowerBound += effectHeight;
            }

            if (hoveredEffect == null) return;

            var description = GatherDescriptionCallback.STATUS_EFFECT.invoker().gatherDescription(hoveredEffect.getEffectType().value());
            if (description == null) return;

            var tooltip = new ArrayList<Text>();
            tooltip.add(((MutableText) this.getStatusEffectDescription(hoveredEffect))
                .append(Text.literal(" ").append(StatusEffectUtil.getDurationText(hoveredEffect, 1.0F, MinecraftClient.getInstance().world.getTickManager().getTickRate()))
                    .formatted(Formatting.GRAY)));
            tooltip.addAll(Lists.reverse(description));

            context.drawTooltip(MinecraftClient.getInstance().textRenderer, tooltip, Optional.empty(), mouseX, mouseY);
        }
    }
}

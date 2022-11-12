package io.wispforest.idwtialsimmoedm.mixin;

import io.wispforest.idwtialsimmoedm.IdwtialsimmoedmClient;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Mixin(AbstractInventoryScreen.class)
public abstract class AbstractInventoryScreenMixin extends HandledScreen<ScreenHandler> {
    public AbstractInventoryScreenMixin(ScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Shadow protected abstract Text getStatusEffectDescription(StatusEffectInstance statusEffect);

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "drawStatusEffects", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/List;of(Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;"))
    private List<Text> makeListBased(List<Text> liste) {
        return new ArrayList<>(liste);
    }

    @Inject(method = "drawStatusEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/AbstractInventoryScreen;renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;Ljava/util/Optional;II)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void addDescription(MatrixStack matrices, int mouseX, int mouseY, CallbackInfo ci, int i, int j, Collection<?> collection, boolean bl, int k, Iterable<StatusEffectInstance> iterable, int l, StatusEffectInstance statusEffectInstance, List<Text> list) {
        list.clear();
        list.add(((MutableText) this.getStatusEffectDescription(statusEffectInstance))
                .append(Text.literal(" " + StatusEffectUtil.durationToString(statusEffectInstance, 1.0F))
                    .formatted(Formatting.GRAY)));

        list.addAll(IdwtialsimmoedmClient.getEffectDescription(statusEffectInstance.getEffectType()));
    }

    @Inject(method = "drawStatusEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/AbstractInventoryScreen;drawStatusEffectDescriptions(Lnet/minecraft/client/util/math/MatrixStack;IILjava/lang/Iterable;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void Mald(MatrixStack matrices, int mouseX, int mouseY, CallbackInfo ci, int i, int j, Collection<StatusEffectInstance> collection, boolean bl, int k, Iterable<StatusEffectInstance> iterable) {
        if (mouseX >= i && mouseX <= i + 121) {
            int l = this.y;
            StatusEffectInstance statusEffectInstance = null;

            for(StatusEffectInstance statusEffectInstance2 : iterable) {
                if (mouseY >= l && mouseY <= l + k) {
                    statusEffectInstance = statusEffectInstance2;
                }

                l += k;
            }

            if (statusEffectInstance != null) {
                List<Text> list = new ArrayList<>();

                list.add(((MutableText) this.getStatusEffectDescription(statusEffectInstance))
                    .append(Text.literal(" " + StatusEffectUtil.durationToString(statusEffectInstance, 1.0F))
                        .formatted(Formatting.GRAY)));
                list.addAll(IdwtialsimmoedmClient.getEffectDescription(statusEffectInstance.getEffectType()));

                this.renderTooltip(matrices, list, Optional.empty(), mouseX, mouseY);
            }
        }
    }
}

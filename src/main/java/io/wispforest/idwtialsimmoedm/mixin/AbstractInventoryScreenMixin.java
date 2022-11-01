package io.wispforest.idwtialsimmoedm.mixin;

import io.wispforest.idwtialsimmoedm.IdwtialsimmoedmClient;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mixin(AbstractInventoryScreen.class)
public class AbstractInventoryScreenMixin {
    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(method = "drawStatusEffects", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/List;of(Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;"))
    private List<Text> makeListBased(List<Text> liste) {
        return new ArrayList<>(liste);
    }

    @Inject(method = "drawStatusEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/AbstractInventoryScreen;renderTooltip(Lnet/minecraft/client/util/math/MatrixStack;Ljava/util/List;Ljava/util/Optional;II)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void addDescription(MatrixStack matrices, int mouseX, int mouseY, CallbackInfo ci, int i, int j, Collection<?> collection, boolean bl, int k, Iterable<StatusEffectInstance> iterable, int l, StatusEffectInstance statusEffectInstance, List<Text> list) {
        list.addAll(IdwtialsimmoedmClient.getEffectDescription(statusEffectInstance.getEffectType()));
    }
}

package io.wispforest.idwtialsimmoedm.api;

import io.wispforest.idwtialsimmoedm.IdwtialsimmoedmConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DefaultDescriptions {

    private static final Map<Enchantment, List<Text>> ENCHANTMENT_CACHE = new HashMap<>();
    private static final Map<StatusEffect, List<Text>> EFFECT_CACHE = new HashMap<>();

    private DefaultDescriptions() {}

    /**
     * Get the raw, unformatted, translated form of {@code enchantment}'s description (as provided through
     * {@code <enchantment translation key>.desc}) for {@code enchantment}, or {@code null} if no description
     * is provided by any language file and {@link IdwtialsimmoedmConfig#hideMissingDescriptions} is {@code true}
     */
    public static @Nullable Text forEnchantmentRaw(Enchantment enchantment) {
        var translationKey = enchantment.getTranslationKey() + ".desc";
        if (IdwtialsimmoedmConfig.get().hideMissingDescriptions && !Language.getInstance().hasTranslation(translationKey)) {
            return null;
        }

        return Text.translatable(translationKey);
    }

    /**
     * Get the default description (as provided by {@link #forEnchantmentRaw(Enchantment)}) for
     * {@code enchantment}, or {@code null} if no description is provided by any language file and
     * {@link IdwtialsimmoedmConfig#hideMissingDescriptions} is {@code true}
     */
    public static @Nullable List<Text> forEnchantmentFormatted(Enchantment enchantment) {
        return ENCHANTMENT_CACHE.computeIfAbsent(enchantment, $ -> {
            var raw = forEnchantmentRaw(enchantment);
            if (raw == null) return null;

            return GatherDescriptionCallback.wrapDescription(raw);
        });
    }

    /**
     * Get the raw, unformatted, translated form of {@code effect}'s description (as provided through
     * {@code <effect translation key>.desc}) for {@code enchantment}, or {@code null} if no description
     * is provided by any language file and {@link IdwtialsimmoedmConfig#hideMissingDescriptions} is {@code true}
     */
    public static @Nullable Text forStatusEffectRaw(StatusEffect effect) {
        var translationKey = effect.getTranslationKey() + ".desc";
        if (IdwtialsimmoedmConfig.get().hideMissingDescriptions && !Language.getInstance().hasTranslation(translationKey)) {
            return null;
        }

        return Text.translatable(translationKey);
    }

    /**
     * Get the default description (as provided by {@link #forStatusEffectRaw(StatusEffect)}) for
     * {@code effect}, or {@code null} if no description is provided by any language file and
     * {@link IdwtialsimmoedmConfig#hideMissingDescriptions} is {@code true}
     */
    public static @Nullable List<Text> forStatusEffectFormatted(StatusEffect effect) {
        return EFFECT_CACHE.computeIfAbsent(effect, $ -> {
            var raw = forStatusEffectRaw(effect);
            if (raw == null) return null;

            return GatherDescriptionCallback.wrapDescription(raw);
        });
    }

    @ApiStatus.Internal
    public static void clearCache() {
        ENCHANTMENT_CACHE.clear();
        EFFECT_CACHE.clear();
    }
}

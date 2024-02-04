package io.wispforest.idwtialsimmoedm;

import io.wispforest.idwtialsimmoedm.api.DefaultDescriptions;
import io.wispforest.idwtialsimmoedm.api.GatherDescriptionCallback;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class IdwtialsimmoedmClient implements ClientModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("idwtialsimmoedm");
    private static final Identifier LATE_PHASE = new Identifier("idwtialsimmoedm", "descriptions");

    @Override
    public void onInitializeClient() {
        IdwtialsimmoedmConfig.load();

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier("idwtialsimmoedm", "cache-yeeter");
            }

            @Override
            public void reload(ResourceManager manager) {
                DefaultDescriptions.clearCache();
            }
        });

        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            if (IdwtialsimmoedmConfig.get().displayOnlyWhenShiftIsHeld) {
                if (!Screen.hasShiftDown()) return;
            }

            if (IdwtialsimmoedmConfig.get().displayOnBooksOnly) {
                if (!(stack.getItem() instanceof EnchantedBookItem)) return;
            }

            final var enchantments = EnchantmentHelper.get(stack).keySet();
            if (enchantments.isEmpty()) return;

            for (var enchantment : enchantments) {
                for (int i = 0; i < lines.size(); i++) {
                    if (!(lines.get(i).getContent() instanceof TranslatableTextContent text)) continue;
                    if (!text.getKey().equals(enchantment.getTranslationKey())) continue;

                    var description = GatherDescriptionCallback.ENCHANTMENT.invoker().gatherDescription(enchantment);
                    if (description == null) return;

                    for (var descLine : description) {
                        lines.add(i + 1, descLine);
                    }
                }
            }
        });

        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            if (IdwtialsimmoedmConfig.get().displayOnlyWhenShiftIsHeld) {
                if (!Screen.hasShiftDown()) return;
            }

            for (int i = 0; i < lines.size(); i++) {
                Text line = lines.get(i);

                if (line.getContent() instanceof TranslatableTextContent translatable
                        && translatable.getKey().equals("potion.withDuration")) {
                    line = (Text) translatable.getArgs()[0];
                }

                if (line.getContent() instanceof TranslatableTextContent translatable
                        && translatable.getKey().equals("potion.withAmplifier")) {
                    line = (Text) translatable.getArgs()[0];
                }

                if (!(line.getContent() instanceof TranslatableTextContent translatable)) continue;

                String key = translatable.getKey();

                if (!key.startsWith("effect.")) continue;

                int firstDot = 6;
                int secondDot = key.indexOf('.', firstDot + 1);

                if (secondDot == -1) continue;

                String namespace = key.substring(firstDot + 1, secondDot);
                String path = key.substring(secondDot + 1);
                StatusEffect effect = Registries.STATUS_EFFECT.get(new Identifier(namespace, path));

                if (effect == null) continue;

                var description = GatherDescriptionCallback.STATUS_EFFECT.invoker().gatherDescription(effect);
                if (description == null) return;

                for (var descLine : description) {
                    lines.add(i + 1, descLine);
                }
            }
        });

        GatherDescriptionCallback.ENCHANTMENT.addPhaseOrdering(Event.DEFAULT_PHASE, LATE_PHASE);
        GatherDescriptionCallback.ENCHANTMENT.register(LATE_PHASE, DefaultDescriptions::forEnchantmentFormatted);

        GatherDescriptionCallback.STATUS_EFFECT.addPhaseOrdering(Event.DEFAULT_PHASE, LATE_PHASE);
        GatherDescriptionCallback.STATUS_EFFECT.register(LATE_PHASE, DefaultDescriptions::forStatusEffectFormatted);
    }

    public record VisitableTextContent(StringVisitable content) implements TextContent {
        @Override
        public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> visitor, Style style) {
            return content.visit(visitor, style);
        }

        @Override
        public <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
            return content.visit(visitor);
        }
    }
}

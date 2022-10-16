package io.wispforest.idwtialsimmoedm;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

@Environment(EnvType.CLIENT)
public class IdwtialsimmoedmClient implements ClientModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("idwtialsimmoedm");

    private static final Map<Enchantment, List<MutableText>> CACHE = new HashMap<>();

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
                IdwtialsimmoedmClient.clearCache();
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

                    for (var descLine : getDescription(enchantment)) {
                        lines.add(i + 1, descLine);
                    }
                }
            }

        });
    }

    public static List<MutableText> getDescription(Enchantment enchantment) {
        return CACHE.computeIfAbsent(enchantment, s -> {
            var lines = MinecraftClient.getInstance().textRenderer.getTextHandler()
                    .wrapLines(Text.translatable(enchantment.getTranslationKey() + ".desc"), 150, Style.EMPTY.withColor(Formatting.DARK_GRAY))
                    .stream()
                    .map(VisitableTextContent::new)
                    .map(MutableText::of).toList();

            var output = new ArrayList<MutableText>();
            for (int i = 0; i < lines.size(); i++) {
                if (i == 0) {
                    output.add(0, Text.literal(IdwtialsimmoedmConfig.get().descriptionPrefix).formatted(Formatting.GRAY).append(lines.get(i)));
                } else {
                    output.add(0, Text.literal(IdwtialsimmoedmConfig.get().descriptionIndent).formatted(Formatting.GRAY).append(lines.get(i)));
                }
            }

            return output;
        });
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

    public static void clearCache() {
        CACHE.clear();
    }
}

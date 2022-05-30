package io.wispforest.idwtialsimmoedm;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Environment(EnvType.CLIENT)
public class IdwtialsimmoedmClient implements ClientModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("idwtialsimmoedm");

    private static final Map<Enchantment, List<MutableText>> CACHE = new HashMap<>();

    @Override
    public void onInitializeClient() {
        IdwtialsimmoedmConfig.load();

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier("idwtialsimmoedm", "cache-yeeter");
            }

            @Override
            public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
                IdwtialsimmoedmClient.clearCache();
                return CompletableFuture.completedFuture(null).thenAccept(synchronizer::whenPrepared);
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
            final var wrappedLines = WordUtils.wrap(Language.getInstance().get(enchantment.getTranslationKey() + ".desc"), 35).split("\n");
            final var output = new ArrayList<MutableText>();

            output.add(Text.literal(IdwtialsimmoedmConfig.get().descriptionPrefix).formatted(Formatting.GRAY)
                    .append(Text.literal(wrappedLines[0]).formatted(Formatting.DARK_GRAY)));

            if (wrappedLines.length > 1) {
                for (int i = 1; i < wrappedLines.length; i++) {
                    output.add(0, Text.literal(IdwtialsimmoedmConfig.get().descriptionIndent).formatted(Formatting.GRAY)
                            .append(Text.literal(wrappedLines[i]).formatted(Formatting.DARK_GRAY)));
                }
            }

            return output;
        });
    }

    public static void clearCache() {
        CACHE.clear();
    }
}

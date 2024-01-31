package io.wispforest.idwtialsimmoedm.api;

import io.wispforest.idwtialsimmoedm.IdwtialsimmoedmClient;
import io.wispforest.idwtialsimmoedm.IdwtialsimmoedmConfig;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface GatherDescriptionCallback<T> {

    Event<GatherDescriptionCallback<Enchantment>> ENCHANTMENT = event();
    Event<GatherDescriptionCallback<StatusEffect>> STATUS_EFFECT = event();

    /**
     * Gather the description for the given game object instance, or return {@code null} if this
     * callback does not provide a description for said instance.
     * <p>
     * {@link DefaultDescriptions} may be used for obtaining the standard descriptions provided
     * to Idwtialsimmoedm through language files
     *
     * @apiNote Implementors should use {@link #wrapDescription(Text)} for wrapping their
     * text, to comply with the default format used by Idwtialsimmoedm. Further, as this method
     * is likely invoked very frequently, it is desirable to cache the wrapped description for a given
     * instance, if possible (check {@link DefaultDescriptions} for a reference implementation).
     */
    @Nullable List<Text> gatherDescription(T instance);

    static List<Text> wrapDescription(Text description) {
        var lines = MinecraftClient.getInstance().textRenderer.getTextHandler()
                .wrapLines(description, 150, Style.EMPTY.withColor(Formatting.DARK_GRAY))
                .stream()
                .map(IdwtialsimmoedmClient.VisitableTextContent::new)
                .map(MutableText::of).toList();

        var output = new ArrayList<Text>();
        for (int i = 0; i < lines.size(); i++) {
            if (i == 0) {
                output.add(0, Text.literal(IdwtialsimmoedmConfig.get().descriptionPrefix).formatted(Formatting.GRAY).append(lines.get(i)));
            } else {
                output.add(0, Text.literal(IdwtialsimmoedmConfig.get().descriptionIndent).formatted(Formatting.GRAY).append(lines.get(i)));
            }
        }

        return output;
    }

    private static <T> Event<GatherDescriptionCallback<T>> event() {
        return EventFactory.createArrayBacked(GatherDescriptionCallback.class, callbacks -> instance -> {
            for (var callback : callbacks) {
                var description = callback.gatherDescription(instance);
                if (description == null) continue;

                return description;
            }

            return null;
        });
    }
}

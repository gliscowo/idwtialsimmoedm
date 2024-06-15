package io.wispforest.idwtialsimmoedm.rei;

import io.wispforest.idwtialsimmoedm.api.DefaultDescriptions;
import io.wispforest.idwtialsimmoedm.api.GatherDescriptionCallback;
import me.shedaniel.rei.api.client.registry.display.DynamicDisplayGenerator;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.plugin.common.displays.DefaultInformationDisplay;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IdwtialsimmoedmInfoGenerator implements DynamicDisplayGenerator<DefaultInformationDisplay> {

    @Override
    public Optional<List<DefaultInformationDisplay>> getRecipeFor(EntryStack<?> entry) {
        return this.generate(entry);
    }

    @Override
    public Optional<List<DefaultInformationDisplay>> getUsageFor(EntryStack<?> entry) {
        return this.generate(entry);
    }

    private Optional<List<DefaultInformationDisplay>> generate(EntryStack<?> entry) {
        if (entry.getType() != VanillaEntryTypes.ITEM) return Optional.empty();

        final var stack = entry.<ItemStack>castValue();
        final var enchantments = stack.get(DataComponentTypes.STORED_ENCHANTMENTS);
        if (!stack.isOf(Items.ENCHANTED_BOOK) || enchantments == null) return Optional.empty();

        final var displayList = new ArrayList<DefaultInformationDisplay>();
        for (var enchantmentEntry : enchantments.getEnchantments()) {
            var enchantment = enchantmentEntry.value();

            final var display = DefaultInformationDisplay.createFromEntry(entry, enchantment.description());
            display.lines(GatherDescriptionCallback.ENCHANTMENT.invoker().gatherDescription(enchantment));

            displayList.add(display);
        }

        return Optional.of(displayList);
    }
}

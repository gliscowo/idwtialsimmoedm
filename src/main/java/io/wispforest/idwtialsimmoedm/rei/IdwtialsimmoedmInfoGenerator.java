package io.wispforest.idwtialsimmoedm.rei;

import me.shedaniel.rei.api.client.registry.display.DynamicDisplayGenerator;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.plugin.common.displays.DefaultInformationDisplay;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.EnchantedBookItem;
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
        if (!stack.isOf(Items.ENCHANTED_BOOK) || !stack.hasNbt()) return Optional.empty();

        final var enchantments = EnchantmentHelper.fromNbt(EnchantedBookItem.getEnchantmentNbt(stack)).keySet();
        final var displayList = new ArrayList<DefaultInformationDisplay>();

        for (var enchantment : enchantments) {
            final var display = DefaultInformationDisplay.createFromEntry(entry, Text.translatable(enchantment.getTranslationKey()));
            display.line(Text.translatable(enchantment.getTranslationKey() + ".desc"));

            displayList.add(display);
        }

        return Optional.of(displayList);
    }
}

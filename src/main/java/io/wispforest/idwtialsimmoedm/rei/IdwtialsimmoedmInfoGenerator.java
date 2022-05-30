package io.wispforest.idwtialsimmoedm.rei;

import io.wispforest.idwtialsimmoedm.IdwtialsimmoedmClient;
import me.shedaniel.rei.api.client.registry.display.DynamicDisplayGenerator;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.plugin.common.displays.DefaultInformationDisplay;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IdwtialsimmoedmInfoGenerator implements DynamicDisplayGenerator<DefaultInformationDisplay> {

    private Optional<List<DefaultInformationDisplay>> generate(EntryStack<?> entry) {
        if (entry.getType() != VanillaEntryTypes.ITEM) return Optional.empty();

        final var stack = entry.<ItemStack>castValue();
        if (!stack.isOf(Items.ENCHANTED_BOOK) || !stack.hasNbt()) return Optional.empty();

        final var enchantments = EnchantmentHelper.fromNbt(EnchantedBookItem.getEnchantmentNbt(stack)).keySet();
        final var displayList = new ArrayList<DefaultInformationDisplay>();

        for (var enchantment : enchantments) {
            final var display = DefaultInformationDisplay.createFromEntry(entry, Text.of("epic, it describes it"));
            display.line(new TranslatableText(enchantment.getTranslationKey() + ".desc"));

            displayList.add(display);
        }

        return Optional.of(displayList);
    }

    @Override
    public Optional<List<DefaultInformationDisplay>> getRecipeFor(EntryStack<?> entry) {
        return generate(entry);
    }

    @Override
    public Optional<List<DefaultInformationDisplay>> getUsageFor(EntryStack<?> entry) {
        return generate(entry);
    }
}

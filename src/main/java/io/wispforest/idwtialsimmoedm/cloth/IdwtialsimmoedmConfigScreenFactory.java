package io.wispforest.idwtialsimmoedm.cloth;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import io.wispforest.idwtialsimmoedm.IdwtialsimmoedmConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.lang.reflect.Field;
import java.util.function.Consumer;

public class IdwtialsimmoedmConfigScreenFactory implements ConfigScreenFactory<Screen> {

    private static final IdwtialsimmoedmConfig DEFAULT_VALUES = new IdwtialsimmoedmConfig();

    @Override
    public Screen create(Screen parent) {
        final var builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("text.idwtialsimmoedm.config.title"));

        final var entryBuilder = builder.entryBuilder();
        final var configInstance = IdwtialsimmoedmConfig.get();
        final var category = builder.getOrCreateCategory(Text.of("category moment"));

        for (var field : IdwtialsimmoedmConfig.class.getFields()) {
            if (field.getType() == boolean.class) {
                category.addEntry(entryBuilder.startBooleanToggle(fieldName(field), fieldGet(configInstance, field))
                        .setSaveConsumer(fieldSetter(configInstance, field))
                        .setDefaultValue((boolean) fieldGet(DEFAULT_VALUES, field)).build());
            } else if (field.getType() == String.class) {
                category.addEntry(entryBuilder.startStrField(fieldName(field), fieldGet(configInstance, field))
                        .setSaveConsumer(fieldSetter(configInstance, field))
                        .setDefaultValue((String) fieldGet(DEFAULT_VALUES, field)).build());
            }
        }

        builder.setSavingRunnable(IdwtialsimmoedmConfig::save);
        return builder.build();
    }

    private static Text fieldName(Field field) {
        return Text.translatable("text.idwtialsimmoedm.config.field." + field.getName());
    }

    @SuppressWarnings("unchecked")
    private static <T> T fieldGet(Object instance, Field field) {
        try {
            return (T) field.get(instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> Consumer<T> fieldSetter(Object instance, Field field) {
        return t -> {
            try {
                field.set(instance, t);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };
    }
}

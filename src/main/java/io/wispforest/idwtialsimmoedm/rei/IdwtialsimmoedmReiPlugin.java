package io.wispforest.idwtialsimmoedm.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;

public class IdwtialsimmoedmReiPlugin implements REIClientPlugin {

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerGlobalDisplayGenerator(new IdwtialsimmoedmInfoGenerator());
    }
}

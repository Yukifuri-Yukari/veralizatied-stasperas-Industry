package yukifuri.mc.vsindustry.client.registries;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import yukifuri.mc.vsindustry.client.gui.CompressorScreen;
import yukifuri.mc.vsindustry.client.gui.energy.generator.ThermoelectricScreen;
import yukifuri.mc.vsindustry.api.gui.UI;
import yukifuri.mc.vsindustry.api.gui.UIScreen;
import yukifuri.mc.vsindustry.api.gui.UIType;
import yukifuri.mc.vsindustry.ui.CompressorUi;
import yukifuri.mc.vsindustry.ui.energy.generator.ThermoelectricUi;

public class VGuis {
    public static void register() {
        register(CompressorUi.TYPE, CompressorScreen::new);
        register(ThermoelectricUi.TYPE, ThermoelectricScreen::new);

    }

    private static <T extends UI<T>> void register(UIType<T> type, ScreenFactory<T> screenFactory) {
        MenuScreens.register(type.get(), screenFactory::create);
    }

    @FunctionalInterface
    private interface ScreenFactory<T extends UI<T>> {
        UIScreen<T> create(T ui, Inventory inventory, Component component);
    }
}

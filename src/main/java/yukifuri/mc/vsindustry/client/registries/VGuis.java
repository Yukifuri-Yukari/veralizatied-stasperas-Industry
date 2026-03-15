package yukifuri.mc.vsindustry.client.registries;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import yukifuri.mc.vsindustry.client.gui.CompressorScreen;
import yukifuri.mc.vsindustry.gui.api.UI;
import yukifuri.mc.vsindustry.gui.api.UIScreen;
import yukifuri.mc.vsindustry.gui.api.UIType;
import yukifuri.mc.vsindustry.gui.ui.CompressorUi;

public class VGuis {
    public static void register() {
        register(CompressorUi.TYPE, "item_with_gui_ui", CompressorScreen::new);
    }

    private static <T extends UI<T>> void register(UIType<T> type, String name, ScreenFactory<T> screenFactory) {
        MenuScreens.register(type.get(), screenFactory::create);
    }

    @FunctionalInterface
    private interface ScreenFactory<T extends UI<T>> {
        UIScreen<T> create(UI<T> ui, Inventory inventory, Component component);
    }
}

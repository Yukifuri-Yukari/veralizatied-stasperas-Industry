package yukifuri.mc.vsindustry.registries;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import yukifuri.mc.vsindustry.gui.api.UI;
import yukifuri.mc.vsindustry.gui.api.UIType;
import yukifuri.mc.vsindustry.gui.ui.CompressorUi;

import static yukifuri.mc.vsindustry.VSIndustry.MOD_ID;

public class VGuis {
    public static void register() {
        register(CompressorUi.TYPE, "item_with_gui_ui");
    }

    static <T extends UI<T>> void register(UIType<T> type, String name) {
        Registry.register(
                BuiltInRegistries.MENU,
                MOD_ID + ":" + name,
                type.get()
        );
    }
}

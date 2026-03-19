package yukifuri.mc.vsindustry.registries;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import yukifuri.mc.vsindustry.api.gui.UI;
import yukifuri.mc.vsindustry.api.gui.UIType;
import yukifuri.mc.vsindustry.ui.CompressorUi;
import yukifuri.mc.vsindustry.ui.energy.generator.ThermoelectricUi;

import static yukifuri.mc.vsindustry.VSIndustry.MOD_ID;

public class VGuis {
    public static void register() {
        register(CompressorUi.TYPE, "compressor_ui");
        register(ThermoelectricUi.TYPE, "thermoelectric_generator_ui");
    }

    static <T extends UI<T>> void register(UIType<T> type, String name) {
        Registry.register(
                BuiltInRegistries.MENU,
                MOD_ID + ":" + name,
                type.get()
        );
    }
}

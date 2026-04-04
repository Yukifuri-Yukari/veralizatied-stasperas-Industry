package yukifuri.mc.vsindustry.registries;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import yukifuri.mc.vsindustry.item.material.Materials;
import yukifuri.mc.vsindustry.item.tool.Tools;

import static yukifuri.mc.vsindustry.VSIndustry.LOGGER;
import static yukifuri.mc.vsindustry.VSIndustry.MOD_ID;

public class VItems {
    public static void register() {
        // Materials
        register(Materials.IRON_PLATE, "iron_plate");
        // Tools
        register(Tools.WRENCH, "wrench");
    }

    static void register(Item item, String id) {
        Registry.register(
                BuiltInRegistries.ITEM,
                MOD_ID + ":" + id,
                item
        );
    }
}

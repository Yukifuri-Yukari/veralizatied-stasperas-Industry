package yukifuri.mc.vsindustry.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import yukifuri.mc.vsindustry.VSIndustry;

public class ThermoelectricFuelTags {
    public static final TagKey<Item> TAG = create("thermoelectric_fuels");

    private static TagKey<Item> create(String name) {
        return TagKey.create(Registries.ITEM, new ResourceLocation(VSIndustry.MOD_ID, name));
    }
}

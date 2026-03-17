package yukifuri.mc.vsindustry.registries;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import yukifuri.mc.vsindustry.VSIndustry;

public class VTabs {
    public static final CreativeModeTab VSI_TAB = FabricItemGroup.builder()
            .icon(Items.ACACIA_BUTTON::getDefaultInstance)
            .title(Component.literal("tab." + VSIndustry.MOD_ID + ".tab"))
            .displayItems((param, output) -> {
                output.accept(VBlocks.COMPRESSOR.asItem());
                output.accept(VBlocks.CABLE.asItem());
                output.accept(VBlocks.THERMOELECTRIC_GENERATOR.asItem());
            })
            .build();

    public static void register() {
        Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                VSIndustry.MOD_ID + ":tab",
                VSI_TAB
        );
    }
}

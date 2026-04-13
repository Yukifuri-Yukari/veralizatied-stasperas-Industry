package yukifuri.mc.vsindustry.registries;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import yukifuri.mc.vsindustry.VSIndustry;
import yukifuri.mc.vsindustry.block.*;
import yukifuri.mc.vsindustry.block.energy.generator.ThermoelectricGenerator;
import yukifuri.mc.vsindustry.block.energy.storge.SmallPowerStorge;

public class VBlocks {
    public static final Block
            COMPRESSOR = new Compressor(),
            CABLE = new Cable(),
            ITEM_PIPE = new ItemPipe(),
            THERMOELECTRIC_GENERATOR = new ThermoelectricGenerator(),
            SMALL_POWER_STORGE = new SmallPowerStorge(),
            MINE_EXCAVATOR = new MineExcavator();

    public static void register() {
        register(COMPRESSOR, Compressor.Entity.TYPE, Items.COMPRESSOR, "compressor");
        register(CABLE, Cable.Entity.TYPE, Items.CABLE, "cable");
        register(ITEM_PIPE, ItemPipe.Entity.TYPE, Items.ITEM_PIPE, "item_pipe");
        register(
                THERMOELECTRIC_GENERATOR, ThermoelectricGenerator.Entity.TYPE,
                Items.THERMOELECTRIC_GENERATOR, "thermoelectric_generator"
        );
        register(SMALL_POWER_STORGE, SmallPowerStorge.Entity.TYPE, Items.SMALL_POWER_STORGE, "small_power_storge");
        register(MINE_EXCAVATOR, MineExcavator.Entity.TYPE, Items.MINE_EXCAVATOR, "mine_excavator");
    }

    static <T extends Block> void register(T block, String name) {
        Registry.register(
                BuiltInRegistries.BLOCK,
                VSIndustry.MOD_ID + ":" + name,
                block
        );
    }

    static <T extends BlockEntity> void register(BlockEntityType<T> type, String name) {
        Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                VSIndustry.MOD_ID + ":" + name,
                type
        );
    }

    static <B extends Block, E extends BlockEntity> void register(
            B block, BlockEntityType<E> type, BlockItem item, String name
    ) {
        register(block, name);
        VItems.register(item, name);
        register(type, name);
    }

    static class Items {
        public static final BlockItem
                COMPRESSOR = simple(VBlocks.COMPRESSOR),
                CABLE = simple(VBlocks.CABLE),
                ITEM_PIPE = simple(VBlocks.ITEM_PIPE),
                THERMOELECTRIC_GENERATOR = simple(VBlocks.THERMOELECTRIC_GENERATOR),
                SMALL_POWER_STORGE = simple(VBlocks.SMALL_POWER_STORGE),
                MINE_EXCAVATOR = simple(VBlocks.MINE_EXCAVATOR);

        static BlockItem simple(Block block) {
            return new BlockItem(block, new Item.Properties());
        }
    }
}

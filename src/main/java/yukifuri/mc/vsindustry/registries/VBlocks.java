package yukifuri.mc.vsindustry.registries;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import yukifuri.mc.vsindustry.VSIndustry;
import yukifuri.mc.vsindustry.block.Cable;
import yukifuri.mc.vsindustry.block.Compressor;
import yukifuri.mc.vsindustry.block.ItemPipe;
import yukifuri.mc.vsindustry.block.energy.generator.ThermoelectricGenerator;

public class VBlocks {
    public static final Compressor COMPRESSOR = new Compressor();
    public static final Cable CABLE = new Cable();
    public static final ItemPipe ITEM_PIPE = new ItemPipe();
    public static final ThermoelectricGenerator THERMOELECTRIC_GENERATOR = new ThermoelectricGenerator();

    public static void register() {
        register(COMPRESSOR, Compressor.Entity.TYPE, Items.COMPRESSOR, "compressor");
        register(CABLE, Cable.Entity.TYPE, Items.CABLE, "cable");
        register(ITEM_PIPE, ItemPipe.Entity.TYPE, Items.ITEM_PIPE, "item_pipe");
        register(
                THERMOELECTRIC_GENERATOR, ThermoelectricGenerator.Entity.TYPE,
                Items.THERMOELECTRIC_GENERATOR, "thermoelectric_generator"
        );
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
        public static final BlockItem COMPRESSOR = simple(VBlocks.COMPRESSOR);
        public static final BlockItem CABLE = simple(VBlocks.CABLE);
        public static final BlockItem ITEM_PIPE = simple(VBlocks.ITEM_PIPE);
        public static final BlockItem THERMOELECTRIC_GENERATOR = simple(VBlocks.THERMOELECTRIC_GENERATOR);

        static BlockItem simple(Block block) {
            return new BlockItem(block, new Item.Properties());
        }
    }
}

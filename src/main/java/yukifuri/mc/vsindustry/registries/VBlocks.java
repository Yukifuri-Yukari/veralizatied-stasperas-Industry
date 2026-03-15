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

public class VBlocks {
    public static final Compressor COMPRESSOR = new Compressor();
    public static final Cable CABLE = new Cable();

    public static void register() {
        register(COMPRESSOR, "compressor");
        VItems.register(Items.COMPRESSOR, "compressor");
        register(Compressor.Entity.TYPE, "compressor");

        register(CABLE, "cable");
        VItems.register(Items.CABLE, "cable");
        register(Cable.Entity.TYPE, "cable");
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

    static class Items {
        public static final BlockItem COMPRESSOR = simple(VBlocks.COMPRESSOR);

        public static final BlockItem CABLE = simple(VBlocks.CABLE);

        static BlockItem simple(Block block) {
            return new BlockItem(block, new Item.Properties());
        }
    }
}

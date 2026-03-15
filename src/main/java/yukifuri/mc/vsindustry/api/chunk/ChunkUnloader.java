package yukifuri.mc.vsindustry.api.chunk;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import yukifuri.mc.vsindustry.api.level.blockentity.BaseBlockEntity;
import yukifuri.mc.vsindustry.hook.TickHandler;

import java.util.ArrayList;
import java.util.List;

public class ChunkUnloader {
    public static void onChunkUnload(ServerLevel level, LevelChunk chunk) {
        List<BaseBlockEntity> entitiesToRemove = new ArrayList<>();

        for (BlockEntity be : chunk.getBlockEntities().values()) {
            if (be instanceof BaseBlockEntity bbe) {
                entitiesToRemove.add(bbe);
            }
        }

        if (!entitiesToRemove.isEmpty()) {
            TickHandler.getInstance().registerTicker(level, l -> callback(entitiesToRemove, l));
        }
    }

    private static void callback(List<BaseBlockEntity> entitiesToRemove, Level level) {
        for (var entity : entitiesToRemove) {
            entity.onChunkUnload();
        }
    }
}

package yukifuri.mc.vsindustry.api.chunk;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import yukifuri.mc.vsindustry.api.level.blockentity.BaseBlockEntity;
import yukifuri.mc.vsindustry.logic.hook.TickHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Deferred blockentity unload callback for BaseBlockEntity.
 * <p>
 * If we do something about status, must wait until next tick start,
 * to ensure simulating result of the current tick is saved in disk,
 * or else we'll lose some info.
 * <p>
 * 针对 BaseBlockEntity 的延迟区块卸载回调.
 * <p>
 * 若要对状态进行处理, 必须等到下一 tick 开始时再执行,
 * 以确保当前 tick 的模拟结果已写入磁盘, 否则会丢失部分信息.
 */
public class ChunkUnloader {
    /**
     * Called when a chunk is unloaded on the server side.
     * Collects all {@link BaseBlockEntity} instances in the chunk and schedules
     * their unload callbacks to run at the start of the next tick.
     * <p>
     * 服务端区块卸载时调用.
     * 收集区块内所有 {@link BaseBlockEntity}, 并将其卸载回调推迟到下一 tick 开始时执行.
     */
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

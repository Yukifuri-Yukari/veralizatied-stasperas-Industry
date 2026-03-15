package yukifuri.mc.vsindustry.hook;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import yukifuri.mc.vsindustry.VSIndustry;
import yukifuri.mc.vsindustry.api.chunk.ChunkUnloader;
import yukifuri.mc.vsindustry.hook.functions.LevelTicker;

import java.util.*;

public class TickHandler {
    private static final TickHandler INSTANCE = new TickHandler();

    public static TickHandler getInstance() {
        return INSTANCE;
    }

    private TickHandler() { }

    private final Map<ResourceKey<Level>, Queue<LevelTicker>> onceTickers = new HashMap<>();
    private final Map<ResourceKey<Level>, Queue<Runnable>> firstTickQueue = new HashMap<>();
    private final Map<ResourceKey<Level>, List<LevelTicker>> persistentTickers = new HashMap<>();

    public void init() {
        ServerChunkEvents.CHUNK_UNLOAD.register(this::onChunkUnload);
        ServerTickEvents.START_WORLD_TICK.register(this::startWorldTick);

        // Release data to let gc collect it.
        ServerLifecycleEvents.SERVER_STOPPED.register(s -> clearAll());
    }

    public void trigListeners() {
        ChunkUnloader.register();
    }

    private void startWorldTick(ServerLevel level) {
        var key = level.dimension();

        // One-time Tickers
        var once = onceTickers.remove(key);
        if (once != null) {
            once.forEach(t -> runSafely(t, level));
        }

        // First-tick Tickers
        var ready = firstTickQueue.remove(key);
        if (ready != null) {
            ready.forEach(r -> {
                try {
                    r.run();
                } catch (Exception e) {
                    VSIndustry.LOGGER.error("firstTick 初始化时发生异常，世界: {}", key.location(), e);
                }
            });
        }

        // Persistent Tickers
        var persistent = persistentTickers.get(key);
        if (persistent != null) {
            persistent.forEach(t -> runSafely(t, level));
        }
    }

    private void runSafely(LevelTicker ticker, ServerLevel level) {
        try {
            ticker.run(level);
        } catch (Exception e) {
            VSIndustry.LOGGER.error("ticker 执行时发生异常，世界: {}", level.dimension().location(), e);
        }
    }

    private void onChunkUnload(ServerLevel level, LevelChunk chunk) {
        // Chunk Unloader has processed this.
        /// {@link ChunkUnloader}
    }

    /**
     * Registers a one-time ticker, which will be executed on the next tick and removed afterward.
     * Caller: {@link yukifuri.mc.vsindustry.api.chunk.ChunkUnloader}
     */
    public void registerTicker(ServerLevel level, LevelTicker ticker) {
        onceTickers.computeIfAbsent(level.dimension(), k -> new ArrayDeque<>())
                .add(ticker);
    }

    /**
     * Registers a first-tick callback, which will be executed on the first tick.
     * Caller: {@link yukifuri.mc.vsindustry.api.level.blockentity.BaseBlockEntity#scheduleInit}
     */
    public void scheduleOnFirstTick(ServerLevel level, Runnable action) {
        firstTickQueue.computeIfAbsent(level.dimension(), k -> new ArrayDeque<>())
                .add(action);
    }

    /**
     * Registers a persistent ticker, which will be executed every tick.
     */
    public void registerPersistentTicker(ServerLevel level, LevelTicker ticker) {
        persistentTickers.computeIfAbsent(level.dimension(), k -> new ArrayList<>())
                .add(ticker);
    }
    /**
     * Clear all tickers.
     */
    private void clearAll() {
        onceTickers.clear();
        firstTickQueue.clear();
        persistentTickers.clear();
        VSIndustry.LOGGER.info("[TickHandler] Cleared remaining tickers");
    }
}
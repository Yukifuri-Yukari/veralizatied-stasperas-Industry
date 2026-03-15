package yukifuri.mc.vsindustry.level.grid;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.*;

/**
 * Responsibilities:
 *   • Rebuild PowerGrids when cable/machine topology changes (BFS)
 *   • Register a per-world tick to call PowerGrid.tick() every server tick
 *   • Track live GridNodes so chunk-load/unload can splice them in/out cleanly
 */
public class GridManager {
    private static final Map<ResourceKey<Level>, GridManager> MANAGERS = new HashMap<>();

    public static GridManager get(ServerLevel level) {
        return MANAGERS.computeIfAbsent(
                level.dimension(),
                k -> new GridManager(level)
        );
    }

    public final ServerLevel level;
    private final Set<PowerGrid> grids = new HashSet<>();

    public GridManager(ServerLevel level) {
        this.level = level;
    }
    // TODO: 2026/3/14 Implement this grid manager
}

package yukifuri.mc.vsindustry.level.grid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import yukifuri.mc.vsindustry.VSIndustry;
import yukifuri.mc.vsindustry.hook.TickHandler;
import yukifuri.mc.vsindustry.level.node.GridNode;

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

    public static PowerGrid get(ServerLevel level, GridNode node) {
        return get(level).getGrid(node);
    }

    public final ServerLevel level;
    private final Set<PowerGrid> grids = new HashSet<>();
    private final Map<BlockPos, PowerGrid> gridsByPos = new HashMap<>();

    public GridManager(ServerLevel level) {
        this.level = level;
        TickHandler.getInstance().registerPersistentTicker(level, this::tick);
    }

    /// Must be called after no pairs in gridsByPos, use {@link GridManager#remove(GridNode)}
    public void remove(PowerGrid grid) {
        grids.remove(grid);
    }

    public void remove(GridNode node) {
        gridsByPos.remove(node.getPos());
    }

    /**
     * Search for 6 sides of given blockpos to find an existing PowerGrid.
     *
     * If a grid is found, return it.
     * If no grids present, return null.
     *
     * The given PowerGrid will not contain the given blockpos.
     */
    @Nullable
    public PowerGrid getGridFromNeighbors(GridNode node) {
        var pos = node.getPos();
        for (Direction direction : Direction.values()) {
            BlockPos offset = pos.relative(direction);
            if (gridsByPos.containsKey(offset)) {
                return gridsByPos.get(offset);
            }
        }

        return null;
    }

    /**
     * Returns the PowerGrid for the given blockpos.
     *
     * If no PowerGrid exists for the given blockpos, returns null.
     *
     * The given PowerGrid will not contain the given blockpos.
     */
    @Nullable
    public PowerGrid getGridFor(GridNode node) {
        var pos = node.getPos();
        if (gridsByPos.containsKey(pos))
            return gridsByPos.get(pos);

        for (var grid : grids) {
            if (grid.nodes.contains(node)) {
                gridsByPos.put(pos, grid);
                return grid;
            }
        }

        return null;
    }

    /**
     * Returns the PowerGrid for the given blockpos.
     *
     * If no power-grid exists around the given blockpos and the given blockpos, a new PowerGrid is created.
     *
     * The given PowerGrid will not contain the given blockpos (pivot is ignored).
     */
    public PowerGrid getGrid(GridNode node) {
        var grid = getGridFromNeighbors(node);
        if (grid != null) return grid;
        var grid2 = getGridFor(node);
        return grid2 != null ? grid2 : newGrid(node);
    }

    /**
     * Returns a new PowerGrid
     */
    private PowerGrid newGrid(GridNode pivot) {
        PowerGrid grid = new PowerGrid(pivot);
        grids.add(grid);
        return grid;
    }

    private void tick(Level _unused) {
        VSIndustry.LOGGER.info("[PowerGrid] Tick {} {}", level, _unused);
        for (var grid : List.copyOf(grids)) {
            grid.tick(level);
        }
    }
}

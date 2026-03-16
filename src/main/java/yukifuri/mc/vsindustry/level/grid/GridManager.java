package yukifuri.mc.vsindustry.level.grid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import yukifuri.mc.vsindustry.hook.TickHandler;
import yukifuri.mc.vsindustry.level.node.GridNode;

import java.util.*;

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
    private final Map<BlockPos, GridNode> nodesByPos = new HashMap<>();

    public GridManager(ServerLevel level) {
        this.level = level;
        TickHandler.getInstance().registerPersistentTicker(level, this::tick);
    }

    // region Node management
    public void addNode(GridNode node) {
        nodesByPos.put(node.getPos(), node);
    }

    public void removeNode(GridNode node) {
        nodesByPos.remove(node.getPos());
    }

    @Nullable
    public GridNode getNodeAt(BlockPos pos) {
        return nodesByPos.get(pos);
    }
    //endregion

    //region Grid management
    public void addGrid(PowerGrid grid) {
        grids.add(grid);
    }

    public void removeGrid(PowerGrid grid) {
        grids.remove(grid);
    }
    //endregion

    //region Topology queries
    /**
     * Returns connected neighbor nodes of the given node.
     */
    public List<GridNode> getNeighbors(GridNode node) {
        List<GridNode> result = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            var neighbor = nodesByPos.get(node.getPos().relative(dir));
            if (neighbor != null) result.add(neighbor);
        }
        return result;
    }

    /**
     * Finds an existing PowerGrid from the neighbors of the given node.
     * Returns null if no neighbors belong to any grid.
     */
    @Nullable
    public PowerGrid getGridFromNeighbors(GridNode node) {
        for (Direction dir : Direction.values()) {
            var neighbor = nodesByPos.get(node.getPos().relative(dir));
            if (neighbor != null && neighbor.getGrid() != null)
                return neighbor.getGrid();
        }
        return null;
    }
    //endregion

    //region Node join / leave
    /**
     * Called when a node is placed into the world.
     * Joins an existing neighboring grid, or creates a new one.
     * If multiple neighboring grids exist, merges them all into one.
     */
    public void nodeJoined(GridNode node) {
        addNode(node);

        Set<PowerGrid> neighborGrids = new HashSet<>();
        for (Direction dir : Direction.values()) {
            var neighbor = nodesByPos.get(node.getPos().relative(dir));
            if (neighbor != null && neighbor.getGrid() != null)
                neighborGrids.add(neighbor.getGrid());
        }

        if (neighborGrids.isEmpty()) {
            // 独立节点，创建新网格
            var grid = new PowerGrid(this);
            grid.addNode(node);
            addGrid(grid);
        } else if (neighborGrids.size() == 1) {
            // 加入唯一邻居网格
            neighborGrids.iterator().next().addNode(node);
        } else {
            // 多个网格需要合并
            merge(neighborGrids, node);
        }
    }

    /**
     * Called when a node is removed from the world.
     * Removes it from its grid, then checks if the grid needs to be split.
     */
    public void nodeRemoved(GridNode node) {
        removeNode(node);

        var grid = node.getGrid();
        if (grid == null) return;

        grid.removeNode(node);

        if (grid.isEmpty()) {
            removeGrid(grid);
            return;
        }

        // 检查移除后网格是否需要分裂
        split(grid);
    }
    //endregion

    //region Merge / Split
    /**
     * Merges multiple grids and the joining node into a single grid.
     * Keeps the largest grid and absorbs the rest.
     */
    private void merge(Set<PowerGrid> toMerge, GridNode joiningNode) {
        // 保留最大的网格，其余合并进去
        PowerGrid largest = toMerge.stream()
                .max(Comparator.comparingInt(g -> g.nodes().size()))
                .orElseThrow();

        for (PowerGrid other : toMerge) {
            if (other == largest) continue;
            for (GridNode n : other.nodes()) {
                largest.addNode(n);
            }
            removeGrid(other);
        }

        largest.addNode(joiningNode);
    }

    /**
     * After a node is removed, checks if the remaining grid has become
     * disconnected. If so, splits it into multiple grids via BFS.
     */
    private void split(PowerGrid grid) {
        Set<GridNode> remaining = new HashSet<>(grid.nodes());
        Set<GridNode> visited = new HashSet<>();
        List<Set<GridNode>> components = new ArrayList<>();

        for (GridNode start : remaining) {
            if (visited.contains(start)) continue;

            // BFS 找连通分量
            Set<GridNode> component = new HashSet<>();
            Queue<GridNode> queue = new ArrayDeque<>();
            queue.add(start);
            visited.add(start);

            while (!queue.isEmpty()) {
                var current = queue.poll();
                component.add(current);
                for (GridNode neighbor : getNeighbors(current)) {
                    if (remaining.contains(neighbor) && !visited.contains(neighbor)) {
                        visited.add(neighbor);
                        queue.add(neighbor);
                    }
                }
            }

            components.add(component);
        }

        if (components.size() <= 1) return; // 没有分裂，不需要处理

        // 第一个分量复用原网格
        grid.setNodes(components.get(0));

        // 其余分量创建新网格
        for (int i = 1; i < components.size(); i++) {
            var newGrid = new PowerGrid(this);
            newGrid.setNodes(components.get(i));
            addGrid(newGrid);
        }
    }
    //endregion

    private void tick(Level _unused) {
        for (var grid : List.copyOf(grids)) {
            grid.tick(level);
        }
    }
}
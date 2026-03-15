package yukifuri.mc.vsindustry.level.grid;

import net.minecraft.server.level.ServerLevel;
import yukifuri.mc.vsindustry.VSIndustry;
import yukifuri.mc.vsindustry.level.node.GridNode;
import yukifuri.mc.vsindustry.util.WorkInProgress;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@WorkInProgress
public class PowerGrid {
    public final Set<GridNode> nodes = new HashSet<>();
    public GridNode pivot;

    public PowerGrid(GridNode pivot) {
        nodeJoined(pivot);
        pivot.setGrid(this);
        this.pivot = pivot;
    }

    public void nodeJoined(GridNode node) {
        nodes.add(node);
        node.setGrid(this);
        VSIndustry.LOGGER.info("[PowerGrid] Node joined: {}", node);
    }

    public void nodeRemoved(GridNode node) {
        nodes.remove(node);
        node.setGrid(null);
        VSIndustry.LOGGER.info("[PowerGrid] Node removed: {}", node);
        getManager().remove(node);
        if (node == pivot) {
            VSIndustry.LOGGER.info("[PowerGrid] Pivot removed: {}", node);
            Optional<GridNode> node1 = nodes.stream().findFirst();
            if (node1.isEmpty()) {
                VSIndustry.LOGGER.info("[PowerGrid] No more nodes in grid");
                /// Ensure we cleaned up all the references and waiting gc to release this.
                /// Things to be cleaned up: Node, GridManager, GridManager.grids, GridManager.gridsByPos
                pivot = null;
                getManager().remove(this);
            }
        }
    }

    // TODO: 2026/3/14


    @Override
    public String toString() {
        return "PowerGrid{" +
                "nodes=" + nodes +
                ", pivot=" + pivot +
                '}';
    }

    private GridManager manager;

    public GridManager getManager() {
        if (manager == null)
            manager = GridManager.get((ServerLevel) pivot.getOwner().getLevel());
        return manager;
    }

    public void empty() {
        manager = null;
    }
}

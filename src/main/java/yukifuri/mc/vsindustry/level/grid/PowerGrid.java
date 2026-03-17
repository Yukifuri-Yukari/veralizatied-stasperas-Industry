package yukifuri.mc.vsindustry.level.grid;

import net.minecraft.server.level.ServerLevel;
import yukifuri.mc.vsindustry.VSIndustry;
import yukifuri.mc.vsindustry.level.node.GridNode;

import java.util.HashSet;
import java.util.Set;

public class PowerGrid {
    private static int currentId = 0;
    public final int id = currentId++;

    private final GridManager manager;
    private final Set<GridNode> nodeSet = new HashSet<>();

    public PowerGrid(GridManager manager) {
        this.manager = manager;
    }

    public void addNode(GridNode node) {
        nodeSet.add(node);
        node.setGrid(this);
    }

    public void removeNode(GridNode node) {
        nodeSet.remove(node);
        node.setGrid(null);
    }

    public void setNodes(Set<GridNode> nodes) {
        for (GridNode n : nodeSet) n.setGrid(null);
        nodeSet.clear();
        for (GridNode n : nodes) {
            nodeSet.add(n);
            n.setGrid(this);
        }
    }

    public Set<GridNode> nodes() {
        return nodeSet;
    }

    public boolean isEmpty() {
        return nodeSet.isEmpty();
    }

    public void tick(ServerLevel level) {
        long totalSupply = 0;
        long totalDemand = 0;

        for (GridNode node : nodeSet) {
            var entity = node.getOwner();
            totalSupply += entity.powerSupplied();
            totalDemand += entity.expectedPower();
        }

        VSIndustry.LOGGER.info("[PowerGrid] {} Ticked complete. Supply {} Demand {}", this, totalSupply, totalDemand);
        if (totalDemand == 0) return;

        long ratio = Math.min(1000L, totalSupply * 1000L / totalDemand);

        for (GridNode node : nodeSet) {
            if (!node.isOnline()) continue;
            var entity = node.getOwner();
            long demand = entity.expectedPower();
            if (demand <= 0) continue;
            entity.powerAccepted(demand * ratio / 1000L);
        }
    }

    @Override
    public String toString() {
        return "PowerGrid{id=" + id + ", size=" + nodeSet.size() + "}";
    }
}
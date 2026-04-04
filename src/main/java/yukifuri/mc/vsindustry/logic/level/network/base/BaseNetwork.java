package yukifuri.mc.vsindustry.logic.level.network.base;

import net.minecraft.server.level.ServerLevel;

import java.util.HashSet;
import java.util.Set;

public abstract class BaseNetwork<Node extends BaseNetworkNode<?>> {
    private static int serialId = 0;

    protected final int serial = serialId++;
    protected final Set<Node> nodeSet = new HashSet<>();

    public final int getSerialId() {
        return serial;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void addNode(Node node) {
        nodeSet.add(node);
        ((BaseNetworkNode) node).setNetwork(this);
    }

    public void removeNode(Node node) {
        nodeSet.remove(node);
        node.setNetwork(null);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void setNodes(Set<Node> nodes) {
        for (Node n : nodeSet) n.setNetwork(null);
        nodeSet.clear();
        for (Node n : nodes) {
            nodeSet.add(n);
            ((BaseNetworkNode) n).setNetwork(this);
        }
    }

    public Set<Node> nodes() {
        return nodeSet;
    }

    public boolean isEmpty() {
        return nodeSet.isEmpty();
    }

    public void tick(ServerLevel level) {}

    protected abstract String getNetworkName();

    @Override
    public String toString() {
        return getNetworkName() + "Network{" +
                "serial=" + serial +
                ", nodes=" + nodeSet.size() +
                '}';
    }
}

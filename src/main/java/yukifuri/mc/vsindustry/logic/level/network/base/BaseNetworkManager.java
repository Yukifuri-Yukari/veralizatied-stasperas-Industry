package yukifuri.mc.vsindustry.logic.level.network.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import yukifuri.mc.vsindustry.VSIndustry;
import yukifuri.mc.vsindustry.logic.hook.TickHandler;

import java.util.*;

public abstract class BaseNetworkManager<
        Network extends BaseNetwork<Node>,
        Node extends BaseNetworkNode<Network>
        > {
    public final ServerLevel level;
    private final Set<Network> networks = new HashSet<>();
    private final Map<BlockPos, Node> nodesByPos = new HashMap<>();

    public BaseNetworkManager(ServerLevel level) {
        this.level = level;
        TickHandler.getInstance().registerPersistentTicker(level, this::tick);
    }

    protected abstract Network createNetwork();

    // region Node management
    public void addNode(Node node) {
        nodesByPos.put(node.getPos(), node);
    }

    public void removeNode(Node node) {
        nodesByPos.remove(node.getPos());
    }

    @Nullable
    public Node getNodeAt(BlockPos pos) {
        return nodesByPos.get(pos);
    }
    //endregion

    //region Network management
    public void addNetwork(Network network) {
        networks.add(network);
    }

    public void removeNetwork(Network network) {
        networks.remove(network);
    }

    public Set<Network> getNetworks() {
        return networks;
    }
    //endregion

    //region Topology queries
    /**
     * Returns connected neighbor nodes of the given node.
     */
    public List<Node> getNeighbors(Node node) {
        List<Node> result = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            var neighbor = nodesByPos.get(node.getPos().relative(dir));
            if (neighbor != null) result.add(neighbor);
        }
        return result;
    }

    /**
     * Finds an existing Network from the neighbors of the given node.
     * Returns null if no neighbors belong to any network.
     */
    @Nullable
    public Network getNetworkFromNeighbors(Node node) {
        for (Direction dir : Direction.values()) {
            var neighbor = nodesByPos.get(node.getPos().relative(dir));
            if (neighbor != null && neighbor.getNetwork() != null)
                return neighbor.getNetwork();
        }
        return null;
    }
    //endregion

    //region Node join / leave
    /**
     * Called when a node is placed into the world.
     * Joins an existing neighboring network, or creates a new one.
     * If multiple neighboring networks exist, merges them all into one.
     */
    public void nodeJoined(Node node) {
        // Chunk reload: reuse existing node at same pos
        // 区块重载: 同一位置已有节点则复用
        Node existing = nodesByPos.get(node.getPos());
        if (existing != null) {
            existing.updateOwner(node.getOwner());
            // 只有区块确实已加载时才 online（防止 firstTick 在 onceTicker offline 之后才执行的竞态）
            if (node.getOwner().isLoaded()) existing.online();
            return;
        }

        addNode(node);

        Set<Network> neighborNetworks = new HashSet<>();
        for (Direction dir : Direction.values()) {
            var neighbor = nodesByPos.get(node.getPos().relative(dir));
            if (neighbor != null && neighbor.getNetwork() != null)
                neighborNetworks.add(neighbor.getNetwork());
        }

        if (neighborNetworks.isEmpty()) {
            // 独立节点，创建新网络
            var network = createNetwork();
            network.addNode(node);
            addNetwork(network);
        } else if (neighborNetworks.size() == 1) {
            // 加入唯一邻居网络
            neighborNetworks.iterator().next().addNode(node);
        } else {
            // 多个网络需要合并
            merge(neighborNetworks, node);
        }
        VSIndustry.LOGGER.info("[Network {}] Node Joined: {}", node.getNetwork(), node);
    }

    /**
     * Called when a node is removed from the world.
     * Removes it from its network, then checks if the network needs to be split.
     */
    public void nodeRemoved(Node node) {
        // Resolve stale node reference from chunk reload
        // 处理区块重载后的陈旧节点引用
        Node registered = nodesByPos.get(node.getPos());
        if (registered != null && registered != node) node = registered;

        VSIndustry.LOGGER.info("[Network {}] Node Removed: {}", node.getNetwork(), node);
        removeNode(node);

        var network = node.getNetwork();
        if (network == null) return;

        network.removeNode(node);

        if (network.isEmpty()) {
            removeNetwork(network);
            return;
        }

        // 检查移除后网络是否需要分裂
        split(network);
    }
    //endregion

    //region Merge / Split
    /**
     * Merges multiple networks and the joining node into a single network.
     * Keeps the largest network and absorbs the rest.
     */
    private void merge(Set<Network> toMerge, Node joiningNode) {
        // 保留最大的网络，其余合并进去
        Network largest = toMerge.stream()
                .max(Comparator.comparingInt(g -> g.nodes().size()))
                .orElseThrow();

        for (Network other : toMerge) {
            if (other == largest) continue;
            for (Node n : List.copyOf(other.nodes()))
                largest.addNode(n);
            removeNetwork(other);
        }

        largest.addNode(joiningNode);
    }

    /**
     * After a node is removed, checks if the remaining network has become
     * disconnected. If so, splits it into multiple networks via BFS.
     */
    private void split(Network network) {
        Set<Node> remaining = new HashSet<>(network.nodes());
        Set<Node> visited = new HashSet<>();
        List<Set<Node>> components = new ArrayList<>();

        for (Node start : remaining) {
            if (visited.contains(start)) continue;

            // BFS 找连通分量
            Set<Node> component = new HashSet<>();
            Queue<Node> queue = new ArrayDeque<>();
            queue.add(start);
            visited.add(start);

            while (!queue.isEmpty()) {
                var current = queue.poll();
                component.add(current);
                for (Node neighbor : getNeighbors(current)) {
                    if (remaining.contains(neighbor) && !visited.contains(neighbor)) {
                        visited.add(neighbor);
                        queue.add(neighbor);
                    }
                }
            }

            components.add(component);
        }

        if (components.size() <= 1) return; // 没有分裂，不需要处理

        // 第一个分量复用原网络
        network.setNodes(components.get(0));

        // 其余分量创建新网络
        for (int i = 1; i < components.size(); i++) {
            var newNetwork = createNetwork();
            newNetwork.setNodes(components.get(i));
            addNetwork(newNetwork);
        }
    }
    //endregion

    private void tick(Level _unused) {
        for (var network : List.copyOf(networks)) {
            // 跳过全员不在已加载区块内的网络
            if (network.nodes().stream().noneMatch(n -> n.getOwner().isLoaded())) continue;
            network.tick(level);
        }
    }
}
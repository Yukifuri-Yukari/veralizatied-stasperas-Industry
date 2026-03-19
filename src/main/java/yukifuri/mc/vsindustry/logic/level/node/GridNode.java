package yukifuri.mc.vsindustry.logic.level.node;

import net.minecraft.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import yukifuri.mc.vsindustry.api.level.blockentity.BaseBlockEntity;
import yukifuri.mc.vsindustry.logic.level.grid.PowerGrid;
import yukifuri.mc.vsindustry.util.WorkInProgress;

import java.util.HashMap;
import java.util.Map;

@WorkInProgress
@MethodsReturnNonnullByDefault @FieldsAreNonnullByDefault
public class GridNode {
    //region statics
    @Contract("!null -> new")
    public static GridNode of(
            BaseBlockEntity entity
    ) {
        return new GridNode(entity);
    }
    //endregion

    @Nullable
    private PowerGrid grid;
    private final BaseBlockEntity entity;
    private final Map<Direction, NodeConnection>
            connections = new HashMap<>();
    private boolean isOnline = true;

    private GridNode(
            BaseBlockEntity entity
    ) {
        this.grid = null;
        this.entity = entity;
    }

    //region Getters & Setters
    public BlockPos getPos() {
        return entity.getBlockPos();
    }

    @Nullable
    public PowerGrid getGrid() {
        return grid;
    }

    public void setGrid(@Nullable PowerGrid grid) {
        this.grid = grid;
    }

    public Map<Direction, NodeConnection> getConnections() {
        return connections;
    }

    public BaseBlockEntity getOwner() {
        return entity;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void offline() { isOnline = false; }
    public void online() { isOnline = true; }
    //endregion

    public boolean hasConnections() {
        return !connections.isEmpty();
    }

    public boolean hasConnection(Direction direction) {
        return connections.containsKey(direction);
    }

    public NodeConnection getConnection(Direction direction) {
        return connections.get(direction);
    }

    public void addConnection(NodeConnection connection) {
        connections.put(connection.direction(), connection);
    }

    @Override
    public String toString() {
        String gridId = grid != null
                ? "" + grid.id
                : "null";
        return "GridNode#PowerGrid(serial=" + gridId + ")@" + getPos() + "<->" + connections + ";";
    }

    @Override
    public int hashCode() {
        return getPos().hashCode();
    }
}

package yukifuri.mc.vsindustry.level.node;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Contract;

import java.util.Objects;

/**
 * `from` `to` is where the electric current comes and flows to.
 * <p>
 * This can be interpreted as a single straight cable.
 * (Cables only transfer powers but no items like AE2)
 */
public class NodeConnection {
    @Contract("!null, !null, !null -> new")
    public static NodeConnection of(
            Direction direction, GridNode a, GridNode b
    ) {
        return new NodeConnection(direction, a, b);
    }

    private final Direction direction;
    private final GridNode a;
    private final GridNode b;

    private NodeConnection(
            Direction direction,
            GridNode a,
            GridNode b
    ) {
        this.direction = direction;
        this.a = a;
        this.b = b;
    }

    public Direction direction() {
        return direction;
    }

    public GridNode a() {
        return a;
    }

    public GridNode b() {
        return b;
    }

    public Direction getDirection(GridNode side) {
        if (side == a) return direction;
        if (side == b) return direction.getOpposite();

        throw new IllegalArgumentException();
    }

    public GridNode getOtherSide(GridNode side) {
        if (side == a) return b;
        if (side == b) return a;

        throw new IllegalArgumentException();
    }

    @Override
    public int hashCode() {
        return a.hashCode() ^ b.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof NodeConnection other)) return false;
        return (a == other.a && b == other.b) || (a == other.b && b == other.a);
    }

    @Override
    public String toString() {
        return "NodeConnection[" +
                "direction=" + direction + ", " +
                "a=" + a + ", " +
                "b=" + b + ']';
    }
}

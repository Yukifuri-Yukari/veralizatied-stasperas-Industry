package yukifuri.mc.vsindustry.logic.level.network.base;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;
import yukifuri.mc.vsindustry.api.level.blockentity.BaseBlockEntity;

public abstract class BaseNetworkNode<Network extends BaseNetwork<?>> {
    @Nullable
    protected Network network;
    protected BaseBlockEntity entity;
    protected boolean isOnline = true;

    protected BaseNetworkNode(
            BaseBlockEntity entity
    ) {
        this.network = null;
        this.entity = entity;
    }

    //region Getters & Setters
    public BlockPos getPos() {
        return entity.getBlockPos();
    }

    @Nullable
    public Network getNetwork() {
        return network;
    }

    public void setNetwork(@Nullable Network network) {
        this.network = network;
    }

    public BaseBlockEntity getOwner() {
        return entity;
    }

    public void updateOwner(BaseBlockEntity owner) {
        entity = owner;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void offline() { isOnline = false; }
    public void online() { isOnline = true; }
    //endregion

    @Override
    public String toString() {
        String id = network != null
                ? "" + network.getSerialId()
                : "null";
        return "Node#Network(serial=" + id + ")@" + getPos() + ";";
    }

    @Override
    public int hashCode() {
        return getPos().hashCode();
    }
}

package yukifuri.mc.vsindustry.logic.level.network.power;

import org.jetbrains.annotations.Contract;
import yukifuri.mc.vsindustry.api.level.blockentity.BaseBlockEntity;
import yukifuri.mc.vsindustry.logic.level.network.base.BaseNetworkNode;

public class PowerNetworkNode extends BaseNetworkNode<PowerNetwork> {
    @Contract("!null -> new")
    public static PowerNetworkNode of(
            BaseBlockEntity entity
    ) {
        return new PowerNetworkNode(entity);
    }

    protected PowerNetworkNode(BaseBlockEntity entity) {
        super(entity);
    }
}

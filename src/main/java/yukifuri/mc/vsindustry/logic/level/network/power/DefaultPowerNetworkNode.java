package yukifuri.mc.vsindustry.logic.level.network.power;

import net.minecraft.server.level.ServerLevel;
import yukifuri.mc.vsindustry.logic.level.network.base.BaseEntityNode;
import yukifuri.mc.vsindustry.logic.level.network.base.BaseNetworkManager;

public interface DefaultPowerNetworkNode extends BaseEntityNode<PowerNetworkNode> {
    @Override
    default BaseNetworkManager<?, PowerNetworkNode> getManager(ServerLevel level) {
        return PowerNetworkManager.get(level);
    }
}

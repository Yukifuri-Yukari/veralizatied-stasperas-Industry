package yukifuri.mc.vsindustry.logic.level.network.power;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import yukifuri.mc.vsindustry.logic.level.network.base.BaseNetworkManager;

import java.util.HashMap;
import java.util.Map;

public class PowerNetworkManager extends BaseNetworkManager<PowerNetwork, PowerNetworkNode> {
    public static final Map<ResourceKey<Level>, PowerNetworkManager> MANAGERS = new HashMap<>();

    public static PowerNetworkManager get(ServerLevel level) {
        return MANAGERS.computeIfAbsent(
                level.dimension(),
                k -> new PowerNetworkManager(level)
        );
    }

    public PowerNetworkManager(ServerLevel level) {
        super(level);
    }

    @Override
    protected PowerNetwork createNetwork() {
        return new PowerNetwork();
    }
}

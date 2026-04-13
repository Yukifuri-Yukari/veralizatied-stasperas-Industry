package yukifuri.mc.vsindustry.logic.level.network.power;

import net.minecraft.server.level.ServerLevel;
import yukifuri.mc.vsindustry.VSIndustry;
import yukifuri.mc.vsindustry.logic.level.network.base.BaseNetwork;

public class PowerNetwork extends BaseNetwork<PowerNetworkNode> {
    public void tick(ServerLevel level) {
        long totalSuppliable = 0;
        long totalDemand = 0;

        for (PowerNetworkNode node : nodeSet) {
            var entity = node.getOwner();
            totalSuppliable += entity.powerSuppliable();
            totalDemand += entity.expectedPower();
        }

        if (totalDemand == 0 || totalSuppliable == 0) return;

        /// how much of supply is actually needed (capped at 1000‰)
        long demandRatio = Math.min(1000L, totalDemand * 1000L / totalSuppliable);
        /// how much of demand can be satisfied (capped at 1000‰)
        long supplyRatio = Math.min(1000L, totalSuppliable * 1000L / totalDemand);

        VSIndustry.LOGGER.info("[Network] {} Supply {} Demand {}, SupplyRatio {}, DemandRatio {}",
                this, totalSuppliable, totalDemand, supplyRatio, demandRatio
        );

        for (PowerNetworkNode node : nodeSet) {
            var entity = node.getOwner();
            if (!node.isOnline() || !entity.isLoaded()) continue;
            long suppliable = entity.powerSuppliable();
            if (suppliable > 0) {
                entity.powerConsumed(suppliable * demandRatio / 1000L);
            }
            long demand = entity.expectedPower();
            if (demand <= 0) continue;
            entity.powerAccepted(demand * supplyRatio / 1000L);
        }
    }

    @Override
    public String getNetworkName() {
        return "Power";
    }
}

package yukifuri.mc.vsindustry.gametest;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import yukifuri.mc.vsindustry.block.Cable;
import yukifuri.mc.vsindustry.registries.VBlocks;

public class GameTestMain implements FabricGameTest {
    @GameTest(template = EMPTY_STRUCTURE)
    public void testCableConnection(GameTestHelper helper) {
        BlockPos pos1 = new BlockPos(1, 1, 1);
        BlockPos pos2 = new BlockPos(2, 1, 1);

        helper.setBlock(pos1, VBlocks.CABLE);
        helper.setBlock(pos2, VBlocks.CABLE);

        // succeedIf 每 tick 轮询，直到 lambda 不抛异常
        helper.runAtTickTime(2, () -> {
            Cable.Entity e1 = (Cable.Entity) helper.getBlockEntity(pos1);
            Cable.Entity e2 = (Cable.Entity) helper.getBlockEntity(pos2);

            // getNetwork() 返回 null 说明还没完成初始化，抛异常让框架下一 tick 重试
            if (e1 == null || e1.getNode().getNetwork() == null)
                throw new GameTestAssertException("pos1 node not yet in network");
            if (e2 == null || e2.getNode().getNetwork() == null)
                throw new GameTestAssertException("pos2 node not yet in network");

            // 两个节点必须在同一网络
            if (e1.getNode().getNetwork() != e2.getNode().getNetwork())
                throw new GameTestAssertException("cables not in same network");
        });
    }
}

package yukifuri.mc.vsindustry.api.level.container;

import net.minecraft.world.WorldlyContainer;

/**
 * Combines {@link ProvidedContainer} and {@link WorldlyContainer} so that a block entity
 * can expose a sided inventory (for hopper/pipe automation) while delegating storage
 * to an inner container.
 * <p>
 * 组合 {@link ProvidedContainer} 与 {@link WorldlyContainer},
 * 使方块实体可暴露带方向的物品栏(供漏斗/管道自动化使用),
 * 同时将存储委托给内部容器.
 */
public interface ProvidedWorldlyContainer extends ProvidedContainer, WorldlyContainer {
}

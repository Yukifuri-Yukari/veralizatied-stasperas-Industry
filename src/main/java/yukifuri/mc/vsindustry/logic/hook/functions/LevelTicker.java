package yukifuri.mc.vsindustry.logic.hook.functions;

import net.minecraft.world.level.Level;

@FunctionalInterface
public interface LevelTicker {
    /**
     * Refer to {@link java.util.concurrent.Callable}.
     */
    void run(Level level) throws Exception;
}

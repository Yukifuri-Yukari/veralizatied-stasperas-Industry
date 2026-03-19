package yukifuri.mc.vsindustry.registries;

import yukifuri.mc.vsindustry.logic.hook.TickHandler;

public class VHooks {
    public static void register() {
        TickHandler.getInstance().init();
    }
}

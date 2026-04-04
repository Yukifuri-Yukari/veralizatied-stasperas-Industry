package yukifuri.mc.vsindustry;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yukifuri.mc.vsindustry.logic.level.network.power.PowerNetworkManager;
import yukifuri.mc.vsindustry.logic.network.NetworkProcessor;
import yukifuri.mc.vsindustry.logic.network.packets.VPacket;
import yukifuri.mc.vsindustry.registries.*;

public class VSIndustry implements ModInitializer {
	public static final String MOD_ID = "vs_industry";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Nullable
	private static MinecraftServer server;

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTING.register(VSIndustry::onServerStarting);
		ServerLifecycleEvents.SERVER_STOPPING.register(VSIndustry::onServerStopping);
		VHooks.register();
		VGuis.register();
		VItems.register();
		VBlocks.register();
		VRecipes.register();
		VTabs.register();
	}

	private static void onServerStopping(MinecraftServer server) {
		VSIndustry.server = null;
		NetworkProcessor.Holder.instance = null;
		PowerNetworkManager.MANAGERS.clear();
	}

	private static void onServerStarting(MinecraftServer s) {
		server = s;
		var sp = new NetworkProcessor.ServerProcessor();
		NetworkProcessor.Holder.instance = sp;
		ServerPlayNetworking.registerGlobalReceiver(VPacket.CHANNEL_ID, sp::handle);
	}

	public static boolean isOnClient() {
		return server == null || server.isSameThread();
	}

	public static boolean isOnServer() {
		return server != null && !server.isSameThread();
	}
}

package yukifuri.mc.vsindustry;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import yukifuri.mc.vsindustry.client.registries.VGuis;
import yukifuri.mc.vsindustry.network.NetworkProcessor;
import yukifuri.mc.vsindustry.network.packets.VPacket;

import static yukifuri.mc.vsindustry.VSIndustry.LOGGER;

@Environment(EnvType.CLIENT)
public class VSIndustryClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        VGuis.register();
        var processor = new NetworkProcessor.ClientProcessor();
        ClientPlayNetworking.registerGlobalReceiver(VPacket.CHANNEL_ID, processor::handle);
        NetworkProcessor.Holder.instance = processor;
    }
}

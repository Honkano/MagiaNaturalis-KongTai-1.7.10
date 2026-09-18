package com.github.elenterius.magianaturalis.network;

import com.github.elenterius.magianaturalis.MagiaNaturalis;
import com.github.elenterius.magianaturalis.network.packet.PacketBiomeChange;
import com.github.elenterius.magianaturalis.network.packet.PacketKeyInput;
import com.github.elenterius.magianaturalis.network.packet.PacketPickedBlock;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {

    public static SimpleNetworkWrapper network;

    private static int nextPacketID = 0;

    public static void initPackets() {
        network = NetworkRegistry.INSTANCE.newSimpleChannel(MagiaNaturalis.MOD_ID.toUpperCase());
        network
            .registerMessage(PacketKeyInput.class, PacketKeyInput.KeyInputMessage.class, nextPacketID++, Side.SERVER);
        network.registerMessage(
            PacketPickedBlock.class,
            PacketPickedBlock.PickedBlockMessage.class,
            nextPacketID++,
            Side.SERVER);
        network.registerMessage(
            PacketBiomeChange.class,
            PacketBiomeChange.BiomeChangeMessage.class,
            nextPacketID++,
            Side.CLIENT);
    }

    // private static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends
    // IMessageHandler<REQ, REPLY>> packet, Class<REQ> message) {
    // network.registerMessage(packet, message, nextPacketID, Side.CLIENT);
    // network.registerMessage(packet, message, nextPacketID, Side.SERVER);
    // nextPacketID++;
    // }

}

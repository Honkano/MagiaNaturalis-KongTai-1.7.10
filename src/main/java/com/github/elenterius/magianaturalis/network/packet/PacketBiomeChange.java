package com.github.elenterius.magianaturalis.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.world.biome.BiomeGenBase;

import com.github.elenterius.magianaturalis.network.packet.PacketBiomeChange.BiomeChangeMessage;
import com.github.elenterius.magianaturalis.util.WorldUtil;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class PacketBiomeChange implements IMessageHandler<BiomeChangeMessage, IMessage> {

    public PacketBiomeChange() {
        super();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IMessage onMessage(BiomeChangeMessage message, MessageContext ctx) {
        if (ctx.side.isClient()) {
            // Update biome
            WorldUtil.setBiomeAt(
                Minecraft.getMinecraft().theWorld,
                message.posX,
                message.posZ,
                BiomeGenBase.getBiome(message.biomeID));

            // Update biome-color
            int y = Minecraft.getMinecraft().theWorld.getTopSolidOrLiquidBlock(message.posX, message.posZ);
            Minecraft.getMinecraft().theWorld.markBlockForUpdate(message.posX, y, message.posZ);
        }

        return null;
    }

    public static class BiomeChangeMessage implements IMessage {

        private int posX;
        private int posZ;
        private short biomeID;

        @SuppressWarnings("unused")
        public BiomeChangeMessage() {
            super();
        }

        public BiomeChangeMessage(int x, int z, short biomeId) {
            posX = x;
            posZ = z;
            biomeID = biomeId;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            posX = buf.readInt();
            posZ = buf.readInt();
            biomeID = buf.readShort();
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(posX);
            buf.writeInt(posZ);
            buf.writeShort(biomeID);
        }
    }

}

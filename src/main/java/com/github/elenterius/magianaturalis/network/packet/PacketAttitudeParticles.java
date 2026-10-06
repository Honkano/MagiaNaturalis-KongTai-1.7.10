package com.github.elenterius.magianaturalis.network.packet;

import com.github.elenterius.magianaturalis.client.render.MaskFXHelper;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

/**
 * 【服务端 → 客户端】触发一次粒子
 */
public class PacketAttitudeParticles
    implements IMessageHandler<PacketAttitudeParticles.Message, IMessage> {

    @Override
    public IMessage onMessage(Message msg, MessageContext ctx) {
        if (ctx.side.isClient()) {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            if (player != null) {
                MaskFXHelper.spawn(player, msg.face);
            }
        }
        return null;
    }

    public static class Message implements IMessage {
        public int face;

        public Message() {}

        public Message(int face) {
            this.face = face;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            face = buf.readByte();
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeByte(face);
        }
    }
}
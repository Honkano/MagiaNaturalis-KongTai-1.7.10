package com.github.elenterius.magianaturalis.network.packet;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import com.github.elenterius.magianaturalis.item.focus.BuilderFocusItem;
import com.github.elenterius.magianaturalis.network.packet.PacketPickedBlock.PickedBlockMessage;
import com.github.elenterius.magianaturalis.util.BuilderFocusUtil;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.items.wands.ItemWandCasting;

public class PacketPickedBlock implements IMessageHandler<PickedBlockMessage, IMessage> {

    public PacketPickedBlock() {
        super();
    }

    @Override
    public IMessage onMessage(PickedBlockMessage message, MessageContext ctx) {
        if (ctx.side.isServer()) {
            ItemStack stack = ctx.getServerHandler().playerEntity.inventory.getCurrentItem();
            if (stack != null && stack.getItem() instanceof ItemWandCasting) {
                ItemWandCasting wand = (ItemWandCasting) stack.getItem();
                ItemFocusBasic focus = wand.getFocus(stack);
                if (focus instanceof BuilderFocusItem) {
                    Block block = Block.getBlockById(message.blockID);
                    int meta = message.blockMeta;

                    if (block != null && meta >= 0 && meta <= 15) {
                        BuilderFocusUtil.setPickedBlock(wand.getFocusItem(stack), block, meta);
                    }
                }
            }
        }

        return null;
    }

    public static class PickedBlockMessage implements IMessage {

        private int blockID;
        private byte blockMeta;

        @SuppressWarnings("unused")
        public PickedBlockMessage() {
            super();
        }

        public PickedBlockMessage(int blockId, byte meta) {
            blockID = blockId;
            blockMeta = meta;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            blockID = buf.readInt();
            blockMeta = buf.readByte();
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(blockID);
            buf.writeByte(blockMeta);
        }
    }

}

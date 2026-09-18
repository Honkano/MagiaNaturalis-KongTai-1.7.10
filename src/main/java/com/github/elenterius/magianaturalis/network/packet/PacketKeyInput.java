package com.github.elenterius.magianaturalis.network.packet;

import net.minecraft.item.ItemStack;

import com.github.elenterius.magianaturalis.MagiaNaturalis;
import com.github.elenterius.magianaturalis.item.focus.BuilderFocusItem;
import com.github.elenterius.magianaturalis.network.packet.PacketKeyInput.KeyInputMessage;
import com.github.elenterius.magianaturalis.util.BuilderFocusUtil;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.items.wands.ItemWandCasting;

public class PacketKeyInput implements IMessageHandler<KeyInputMessage, IMessage> {

    public PacketKeyInput() {
        super();
    }

    @Override
    public IMessage onMessage(KeyInputMessage message, MessageContext ctx) {
        if (ctx.side.isServer()) {
            ItemStack stack = ctx.getServerHandler().playerEntity.inventory.getCurrentItem();
            if (stack != null && stack.getItem() instanceof ItemWandCasting) {
                ItemWandCasting wand = (ItemWandCasting) stack.getItem();
                ItemFocusBasic focus = wand.getFocus(stack);
                if (focus instanceof BuilderFocusItem) {
                    ItemStack focusStack = wand.getFocusItem(stack);
                    switch (message.keyId) {
                        case 2:
                            BuilderFocusUtil.cycleSize(focusStack, -1, 1, focus.getMaxAreaSize(focusStack));
                            break;
                        case 3:
                            BuilderFocusUtil.cycleSize(focusStack, 1, 1, focus.getMaxAreaSize(focusStack));
                            break;
                        case 4:
                            BuilderFocusUtil.setMode(
                                focusStack,
                                BuilderFocusUtil.getMode(focusStack)
                                    .cycle());
                            break;
                        case 5:
                            BuilderFocusUtil.setShape(
                                focusStack,
                                BuilderFocusUtil.getShape(focusStack)
                                    .next());
                            break;
                        case 51:
                            BuilderFocusUtil.setShape(
                                focusStack,
                                BuilderFocusUtil.getShape(focusStack)
                                    .prev());
                            break;
                        default:
                            MagiaNaturalis.LOGGER.error("Invalid KEY ID recieved.");
                    }
                }
            }
        }

        return null;
    }

    public static class KeyInputMessage implements IMessage {

        private byte keyId;

        @SuppressWarnings("unused")
        public KeyInputMessage() {
            super();
        }

        public KeyInputMessage(byte customKeyID) {
            keyId = customKeyID;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            keyId = buf.readByte();
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeByte(keyId);
        }
    }

}

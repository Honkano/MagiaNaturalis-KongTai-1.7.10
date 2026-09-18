package com.github.elenterius.magianaturalis.util;

import static net.minecraftforge.common.util.Constants.NBT;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.github.elenterius.magianaturalis.MagiaNaturalis;

public final class BuilderFocusUtil {

    public static final String MOD_TAG_KEY = MagiaNaturalis.MOD_ID;
    public static final String MODE_TAG_KEY = "mode";
    public static final String SHAPE_TAG_KEY = "shape";
    public static final String SIZE_TAG_KEY = "size";

    public static Mode getMode(ItemStack stack) {
        if (stack == null) return Mode.NONE;

        NBTTagList list = NBTUtil.getOrCreate(stack)
            .getTagList(MOD_TAG_KEY, NBT.TAG_COMPOUND);
        if (list == null) return Mode.NONE;

        return Mode.parseId(
            list.getCompoundTagAt(0)
                .getByte(MODE_TAG_KEY));
    }

    public static boolean setMode(ItemStack stack, Mode mode) {
        if (stack == null) return false;

        NBTTagCompound data = NBTUtil.getOrCreate(stack);
        NBTTagList list = data.getTagList(MOD_TAG_KEY, NBT.TAG_COMPOUND);
        NBTTagCompound tempData;

        if (list == null) {
            list = new NBTTagList();
            tempData = new NBTTagCompound();
        } else {
            tempData = list.getCompoundTagAt(0);
        }

        tempData.setByte(MODE_TAG_KEY, mode.id());
        list.appendTag(tempData);

        if (list.tagCount() > 0) {
            data.setTag(MOD_TAG_KEY, list);
            return true;
        }

        return false;
    }

    public static Shape getShape(ItemStack stack) {
        if (stack == null) return Shape.NONE;

        NBTTagList list = NBTUtil.getOrCreate(stack)
            .getTagList(MOD_TAG_KEY, NBT.TAG_COMPOUND);
        if (list == null) return Shape.NONE;

        return Shape.parseId(
            list.getCompoundTagAt(0)
                .getByte(SHAPE_TAG_KEY));
    }

    public static boolean setShape(ItemStack stack, Shape shape) {
        if (stack == null) return false;

        NBTTagCompound data = NBTUtil.getOrCreate(stack);
        NBTTagList list = data.getTagList(MOD_TAG_KEY, NBT.TAG_COMPOUND);
        NBTTagCompound tempData;

        if (list == null) {
            list = new NBTTagList();
            tempData = new NBTTagCompound();
        } else {
            tempData = list.getCompoundTagAt(0);
        }

        tempData.setByte(SHAPE_TAG_KEY, shape.id());
        list.appendTag(tempData);

        if (list.tagCount() > 0) {
            stack.setTagInfo(MOD_TAG_KEY, list);
            return true;
        }

        return false;
    }

    public static int getSize(ItemStack stack) {
        if (stack == null) return 1;

        NBTTagList list = NBTUtil.getOrCreate(stack)
            .getTagList(MOD_TAG_KEY, NBT.TAG_COMPOUND);
        if (list == null) return 1;

        return list.getCompoundTagAt(0)
            .getByte(SIZE_TAG_KEY);
    }

    public static boolean setSize(ItemStack stack, int size) {
        if (stack == null) return false;

        NBTTagCompound data = NBTUtil.getOrCreate(stack);
        NBTTagList list = data.getTagList(MOD_TAG_KEY, NBT.TAG_COMPOUND);
        NBTTagCompound tempData;

        if (list == null) {
            list = new NBTTagList();
            tempData = new NBTTagCompound();
        } else {
            tempData = list.getCompoundTagAt(0);
        }

        tempData.setByte(SIZE_TAG_KEY, (byte) size);
        list.appendTag(tempData);

        if (list.tagCount() > 0) {
            stack.setTagInfo(MOD_TAG_KEY, list);
            return true;
        }

        return false;
    }

    public static boolean cycleSize(ItemStack stack, int amount, int minSize, int maxSize) {
        int size = getSize(stack) + amount;
        if (size > maxSize) return setSize(stack, minSize);
        if (size < minSize) return setSize(stack, maxSize);
        return setSize(stack, size);
    }

    public static boolean setPickedBlock(ItemStack stack, Block block, int metadata) {
        if (stack == null) return false;

        int bid = Block.getIdFromBlock(block);
        if (metadata < 0 || metadata > 15) metadata = 0;

        NBTTagCompound data = NBTUtil.getOrCreate(stack);
        NBTTagList nbttaglist = data.getTagList(MOD_TAG_KEY, NBT.TAG_COMPOUND);
        NBTTagCompound nbttagcompound;

        if (nbttaglist == null) {
            nbttaglist = new NBTTagList();
            nbttagcompound = new NBTTagCompound();
        } else {
            nbttagcompound = nbttaglist.getCompoundTagAt(0);
        }

        nbttagcompound.setInteger("bid", bid);
        nbttagcompound.setByte("bdata", (byte) metadata);

        nbttaglist.appendTag(nbttagcompound);

        if (nbttaglist.tagCount() > 0) {

            stack.setTagInfo(MOD_TAG_KEY, nbttaglist);
            return true;

        }
        return false;
    }

    public static int[] getPickedBlock(ItemStack stack) {
        int[] i = { 0, 0 };
        if (stack == null) return i;

        NBTTagList nbttaglist = NBTUtil.getOrCreate(stack)
            .getTagList(MOD_TAG_KEY, NBT.TAG_COMPOUND);
        if (nbttaglist == null) {
            return i;
        } else {
            i[0] = nbttaglist.getCompoundTagAt(0)
                .getInteger("bid");
            i[1] = nbttaglist.getCompoundTagAt(0)
                .getByte("bdata");
            return i;
        }
    }

    public enum Mode {

        NONE,
        UNIFORM;

        public static Mode parseId(int i) {
            if (i < 0 || i >= values().length) return NONE;
            return values()[i];
        }

        public byte id() {
            return (byte) ordinal();
        }

        public Mode cycle() {
            return this == NONE ? UNIFORM : NONE;
        }

        public String toString() {
            switch (this) {
                case NONE:
                    return Platform.translate("enum.magianaturalis.none");
                case UNIFORM:
                    return Platform.translate("enum.magianaturalis.mode.uniform");
                default:
                    return Platform.translate("enum.magianaturalis.unknown");
            }
        }

    }

    public enum Shape {

        NONE,
        PLANE,
        CUBE,
        PLANE_EXTEND,
        SPHERE;

        public static Shape parseId(int i) {
            if (i < 0 || i >= values().length) return NONE;
            return values()[i];
        }

        public byte id() {
            return (byte) ordinal();
        }

        public Shape next() {
            int id = ordinal() + 1;
            if (id >= values().length) return values()[1];
            return values()[id];
        }

        public Shape prev() {
            int id = ordinal() - 1;
            if (id <= 1) return values()[values().length - 1];
            return values()[id];
        }

        public String toString() {
            switch (this) {
                case CUBE:
                    return Platform.translate("enum.magianaturalis.shape.cube");
                case NONE:
                    return Platform.translate("enum.magianaturalis.none");
                case PLANE:
                    return Platform.translate("enum.magianaturalis.shape.plane");
                case PLANE_EXTEND:
                    return Platform.translate("enum.magianaturalis.shape.plane.extend");
                case SPHERE:
                    return Platform.translate("enum.magianaturalis.shape.sphere");
                default:
                    return Platform.translate("enum.magianaturalis.unknown");
            }
        }

    }

}

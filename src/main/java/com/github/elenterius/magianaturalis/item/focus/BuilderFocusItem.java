package com.github.elenterius.magianaturalis.item.focus;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.github.elenterius.magianaturalis.init.client.MNKeyBindings;
import com.github.elenterius.magianaturalis.util.BuilderFocusUtil;
import com.github.elenterius.magianaturalis.util.BuilderFocusUtil.Shape;
import com.github.elenterius.magianaturalis.util.Platform;
import com.github.elenterius.magianaturalis.util.WorldCoord;
import com.github.elenterius.magianaturalis.util.WorldUtil;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.BlockCoordinates;
import thaumcraft.api.IArchitect;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.items.wands.ItemWandCasting;

public class BuilderFocusItem extends ItemFocusBasic implements IArchitect {

    protected static final AspectList VIS_COST = new AspectList().add(Aspect.ORDER, 5)
        .add(Aspect.EARTH, 5);

    public static double reachDistance = 8.0D;

    public BuilderFocusItem() {
        super();
    }

    @Override
    public void registerIcons(IIconRegister registry) {
        icon = registry.registerIcon(getIconString());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List lines, boolean advancedItemTooltips) {
        super.addInformation(stack, player, lines, advancedItemTooltips);
        lines.add("");
        lines.add(EnumChatFormatting.GRAY + "Mode: " + BuilderFocusUtil.getMode(stack));
        lines.add(EnumChatFormatting.GRAY + "Shape: " + BuilderFocusUtil.getShape(stack));
        lines.add(EnumChatFormatting.GRAY + "Size: " + BuilderFocusUtil.getSize(stack));
        lines.add("");
        lines.add(
            String.format(
                "§8Press §7[%s]§8 or §7[%s]§8 to change size of shape",
                GameSettings.getKeyDisplayString(MNKeyBindings.INCREASE_SIZE_KEY.getKeyCode()),
                GameSettings.getKeyDisplayString(MNKeyBindings.DECREASE_SIZE_KEY.getKeyCode())));
        lines.add(
            String.format(
                "§8Press §7[%s]§8 to change shape",
                GameSettings.getKeyDisplayString(MNKeyBindings.MISC_KEY.getKeyCode())));
        lines.add(
            String.format(
                "§8Press §7[ctrl + %s]§8 to change mode",
                GameSettings.getKeyDisplayString(MNKeyBindings.MISC_KEY.getKeyCode())));
        lines.add(
            String.format(
                "§8Press §7[%s]§8 to pick block type",
                GameSettings.getKeyDisplayString(MNKeyBindings.PICK_BLOCK_KEY.getKeyCode())));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs creativeTabs, List list) {
        ItemStack stack = new ItemStack(item, 1, 0);
        applyUpgrade(stack, FocusUpgradeType.architect, 1);
        BuilderFocusUtil.setSize(stack, 1);
        BuilderFocusUtil.setShape(stack, Shape.CUBE);
        list.add(stack);
    }

    @Override
    public int getFocusColor(ItemStack focusStack) {
        return 0x857B93;
    }

    @Override
    public AspectList getVisCost(ItemStack focusStack) {
        return VIS_COST;
    }

    @Override
    public int getMaxAreaSize(ItemStack focusStack) {
        return 3 + getUpgradeLevel(focusStack, FocusUpgradeType.enlarge) * 3 + 1;
    }

    @Override
    public FocusUpgradeType[] getPossibleUpgradesByRank(ItemStack focusStack, int rank) {
        return new FocusUpgradeType[] { FocusUpgradeType.enlarge, FocusUpgradeType.frugal };
    }

    @Override
    public ItemStack onFocusRightClick(ItemStack wandStack, World world, EntityPlayer player,
        MovingObjectPosition movingObjectPosition) {
        player.swingItem();

        if (Platform.isClient()) return wandStack;

        MovingObjectPosition target = WorldUtil.getMovingObjectPositionFromPlayer(world, player, reachDistance, true);

        if (target != null && target.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            int x = target.blockX;
            int y = target.blockY;
            int z = target.blockZ;

            // Item wandItem = wandStack.getItem();
            // if (wandItem instanceof ItemWandCasting) {
            // if (player.isSneaking()) {
            // ItemStack focusStack = ((ItemWandCasting) wandItem).getFocusItem(wandStack);
            // BuilderFocusUtil.setpickedBlock(focusStack, world.getBlock(x, y, z), world.getBlockMetadata(x, y, z));
            // ((ItemWandCasting) wandItem).setFocus(wandStack, focusStack); //update focus with new NBT data in wand
            // return wandStack;
            // }
            // }

            float hitX = (float) (target.hitVec.xCoord - x);
            float hitY = (float) (target.hitVec.yCoord - y);
            float hitZ = (float) (target.hitVec.zCoord - z);

            hitX = Math.abs(hitX);
            hitY = Math.abs(hitY);
            hitZ = Math.abs(hitZ);

            if (onFocusUse(wandStack, player, world, x, y, z, target.sideHit, hitX, hitY, hitZ)) {
                world.playSoundAtEntity(player, "thaumcraft:wand", 0.25F, 0.9F + world.rand.nextFloat() * 0.2F);
            } else {
                world.playSoundAtEntity(player, "thaumcraft:wandfail", 0.5F, 0.8F + world.rand.nextFloat() * 0.1F);
            }
        }

        return wandStack;
    }

    public boolean onFocusUse(ItemStack wandStack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (!player.capabilities.allowEdit) return false;

        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        ItemStack focusStack = wand.getFocusItem(wandStack);

        int size = BuilderFocusUtil.getSize(focusStack);
        if (size < 1 || size > getMaxAreaSize(focusStack)) return false;

        Shape shape = BuilderFocusUtil.getShape(focusStack);
        if (shape == Shape.NONE) return false;

        Block pblock = null;
        int pbdata = 0;

        if (BuilderFocusUtil.getMode(focusStack) == BuilderFocusUtil.Mode.UNIFORM) {
            pblock = world.getBlock(x, y, z);
            pbdata = world.getBlockMetadata(x, y, z);
        } else {
            int[] i = BuilderFocusUtil.getPickedBlock(focusStack);
            pblock = Block.getBlockById(i[0]);
            pbdata = i[1];
        }

        if (pblock == null || pblock == Blocks.air) return false;
        if (pbdata < 0 || pbdata > 15) return false;

        return buildAction(wandStack, player, world, x, y, z, side, hitX, hitY, hitZ, size, pblock, pbdata);
    }

    private boolean buildAction(ItemStack wandStack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ, int size, Block pickedblock, int pbData) {
        List<WorldCoord> shape = null;
        ForgeDirection face = ForgeDirection.getOrientation(side);
        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();

        switch (BuilderFocusUtil.getShape(wand.getFocusItem(wandStack))) {
            case CUBE:
                x += face.offsetX * size;
                y += face.offsetY * size;
                z += face.offsetZ * size;
                shape = WorldUtil.plot3DCubeArea(player, world, x, y, z, side, hitX, hitY, hitZ, size);
                break;

            case PLANE:
                x += face.offsetX;
                y += face.offsetY;
                z += face.offsetZ;
                shape = WorldUtil.plot2DPlane(player, world, x, y, z, side, hitX, hitY, hitZ, size);
                break;

            case PLANE_EXTEND:
                x += face.offsetX * (size + 1) / 2;
                y += face.offsetY * (size + 1) / 2;
                z += face.offsetZ * (size + 1) / 2;
                shape = WorldUtil.plot2DPlaneExtension(player, world, x, y, z, side, hitX, hitY, hitZ, size);
                break;

            case SPHERE:
                x += face.offsetX * size;
                y += face.offsetY * size;
                z += face.offsetZ * size;
                shape = WorldUtil.plot3DCubeArea(player, world, x, y, z, side, hitX, hitY, hitZ, size);
                break;

            case NONE:
            default:
                break;
        }

        if (shape == null || shape.isEmpty()) return false;

        int blockCount = shape.size();

        if (!player.capabilities.isCreativeMode) {
            int availableOrderVis = wand.getVis(wandStack, Aspect.ORDER);
            if (shape.size() * 5 > availableOrderVis) {
                blockCount = shape.size() - (shape.size() - availableOrderVis / 5);
            }

            if (blockCount == 0) return false;

            ItemStack tempStack = new ItemStack(pickedblock, 1, pbData);
            if (tempStack.getItem() != null) {
                int availableBlocks = 0;
                for (int i = 0; i < player.inventory.mainInventory.length; ++i) {
                    if (player.inventory.mainInventory[i] != null
                        && player.inventory.mainInventory[i].isItemEqual(tempStack)) {
                        availableBlocks += player.inventory.mainInventory[i].stackSize;
                    }
                }

                if (availableBlocks < blockCount) {
                    blockCount = availableBlocks;
                }

                if (blockCount == 0) return false;

                for (int j = 0; j < blockCount; j++) {
                    player.inventory.consumeInventoryItem(tempStack.getItem());
                }

                player.inventoryContainer.detectAndSendChanges();

                int costN = 5 * blockCount;
                if (!ThaumcraftApiHelper.consumeVisFromWand(
                    wandStack,
                    player,
                    new AspectList().add(Aspect.ORDER, costN)
                        .add(Aspect.EARTH, costN),
                    true,
                    false)) {
                    return false;
                }
            } else {
                blockCount = 0;
            }
        }

        if (blockCount == 0) return false;

        for (int i = 0; i < blockCount; i++) {
            WorldCoord temp = shape.get(i);
            world.setBlock(temp.x, temp.y, temp.z, pickedblock, pbData, 3);
        }
        return true;
    }

    @Override
    public ArrayList<BlockCoordinates> getArchitectBlocks(ItemStack stack, World world, int x, int y, int z, int side,
        EntityPlayer player) {
        // TODO: render not working with extended reach distance
        MovingObjectPosition target = WorldUtil.getMovingObjectPositionFromPlayer(world, player, reachDistance, true); // have
                                                                                                                       // to
                                                                                                                       // call
                                                                                                                       // it
                                                                                                                       // here
                                                                                                                       // for
                                                                                                                       // hitVec

        if (target != null && target.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            x = target.blockX;
            y = target.blockY;
            z = target.blockZ;

            Block block1 = player.worldObj.getBlock(x, y, z);
            if (block1 == null) return null;

            int b1damage = block1.getDamageValue(player.worldObj, x, y, z);

            if (stack != null) {
                ItemStack stackFocus = null;

                if (stack.getItem() instanceof ItemWandCasting) {
                    ItemWandCasting wand = ((ItemWandCasting) stack.getItem());
                    stackFocus = wand.getFocusItem(stack);
                } else if (stack.getItem() instanceof BuilderFocusItem) {
                    stackFocus = stack;
                } else {
                    return null;
                }

                float hitX = (float) (target.hitVec.xCoord - x);
                float hitY = (float) (target.hitVec.yCoord - y);
                float hitZ = (float) (target.hitVec.zCoord - z);

                hitX = Math.abs(hitX);
                hitY = Math.abs(hitY);
                hitZ = Math.abs(hitZ);

                ForgeDirection face = ForgeDirection.getOrientation(target.sideHit);

                ArrayList<BlockCoordinates> blocks = null;
                int size = BuilderFocusUtil.getSize(stackFocus);
                if (size < 1 || size > getMaxAreaSize(stackFocus)) return null;

                switch (BuilderFocusUtil.getShape(stackFocus)) {
                    case CUBE:
                        x += face.offsetX * size;
                        y += face.offsetY * size;
                        z += face.offsetZ * size;
                        blocks = (ArrayList) WorldUtil
                            .plot3DCubeArea(player, player.worldObj, x, y, z, target.sideHit, hitX, hitY, hitZ, size);
                        break;

                    case PLANE:
                        x += face.offsetX;
                        y += face.offsetY;
                        z += face.offsetZ;
                        blocks = (ArrayList) WorldUtil
                            .plot2DPlane(player, player.worldObj, x, y, z, target.sideHit, hitX, hitY, hitZ, size);
                        break;

                    case PLANE_EXTEND:
                        x += face.offsetX * (size + 1) / 2;
                        y += face.offsetY * (size + 1) / 2;
                        z += face.offsetZ * (size + 1) / 2;
                        blocks = (ArrayList) WorldUtil.plot2DPlaneExtension(
                            player,
                            player.worldObj,
                            x,
                            y,
                            z,
                            target.sideHit,
                            hitX,
                            hitY,
                            hitZ,
                            size);
                        break;

                    case SPHERE:
                        x += face.offsetX * size;
                        y += face.offsetY * size;
                        z += face.offsetZ * size;
                        blocks = (ArrayList) WorldUtil
                            .plot3DCubeArea(player, player.worldObj, x, y, z, target.sideHit, hitX, hitY, hitZ, size);
                        break;

                    case NONE:
                    default:
                        break;
                }

                return blocks;
            }
        }
        return null;
    }

    @Override
    public boolean showAxis(ItemStack stack, World world, EntityPlayer player, int side, EnumAxis axis) {
        return false;
    }

}

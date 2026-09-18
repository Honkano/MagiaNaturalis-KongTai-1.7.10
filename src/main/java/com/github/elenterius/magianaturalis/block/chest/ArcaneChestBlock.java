package com.github.elenterius.magianaturalis.block.chest;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.ForgeDirection;

import com.github.elenterius.magianaturalis.MagiaNaturalis;
import com.github.elenterius.magianaturalis.client.render.RenderUtil;
import com.github.elenterius.magianaturalis.util.NBTUtil;
import com.github.elenterius.magianaturalis.util.Platform;
import com.github.elenterius.magianaturalis.util.access.UserAccess;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.utils.InventoryUtils;

public class ArcaneChestBlock extends BlockContainer {

    private ArcaneChestType cachedChestType;

    public ArcaneChestBlock() {
        super(Material.wood);
        setResistance(999.0F);
        setHardness(8.0F);
        setStepSound(soundTypeWood);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(item, 1, ArcaneChestType.GREAT_WOOD.id()));
        list.add(new ItemStack(item, 1, ArcaneChestType.SILVER_WOOD.id()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister ir) {
        blockIcon = ir.registerIcon("thaumcraft:woodplain");
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return RenderUtil.RENDER_ID_1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(World world, MovingObjectPosition target, EffectRenderer effectRenderer) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        ArcaneChestBlockEntity chest = (ArcaneChestBlockEntity) world
            .getTileEntity(target.blockX, target.blockY, target.blockZ);

        if (chest != null && player != null) {
            if (chest.getOwner()
                .equals(
                    player.getGameProfile()
                        .getId())) {
                return false;
            } else {
                if (chest.accessList != null && chest.accessList.size() > 0)
                    for (UserAccess user : chest.accessList) if (user.getUUID()
                        .equals(
                            player.getGameProfile()
                                .getId()))
                        return user.getAccessLevel() > 1;
            }
        }

        float f = (float) target.hitVec.xCoord - target.blockX;
        float f1 = (float) target.hitVec.yCoord - target.blockY;
        float f2 = (float) target.hitVec.zCoord - target.blockZ;
        MagiaNaturalis.proxyTC4.blockWard(
            world,
            target.blockX,
            target.blockY,
            target.blockZ,
            ForgeDirection.getOrientation(target.sideHit),
            f,
            f1,
            f2);
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new ArcaneChestBlockEntity();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        int metadata = BlockPistonBase.determineOrientation(world, x, y, z, entity);
        world.setBlockMetadataWithNotify(x, y, z, metadata, 3);
        ArcaneChestBlockEntity chest = (ArcaneChestBlockEntity) world.getTileEntity(x, y, z);

        if (chest != null) {
            EntityPlayer player = (EntityPlayer) entity;
            chest.setOwner(player.getUniqueID());
            chest.setChestType(ArcaneChestType.parseId((byte) stack.getItemDamage()));

            ItemStack[] items = NBTUtil.loadInventoryFromNBT(stack, chest.getSizeInventory());
            chest.setInventory(items);

            ArrayList<UserAccess> users = NBTUtil.loadUserAccessFromNBT(stack);
            if (!users.isEmpty()) chest.accessList = users;
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int i, float f0, float f1,
        float f3) {
        if (player.isSneaking()) return false;
        if (Platform.isClient()) return true;

        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof ArcaneChestBlockEntity) {
            ArcaneChestBlockEntity chest = (ArcaneChestBlockEntity) tile;
            boolean hasAccess = false;
            if (player.capabilities.isCreativeMode || player.getGameProfile()
                .getId()
                .equals(chest.getOwner())) {
                hasAccess = true;
            } else {
                if (chest.accessList != null && !chest.accessList.isEmpty()) {
                    for (UserAccess user : chest.accessList) {
                        if (user.getUUID()
                            .equals(
                                player.getGameProfile()
                                    .getId())) {
                            hasAccess = user.hasAccess();
                            break;
                        }
                    }
                }
            }

            if (hasAccess) {
                player.openGui(MagiaNaturalis.instance, 2, world, x, y, z);
            } else {
                world.playSoundEffect(x, y, z, "thaumcraft:doorfail", 0.66F, 1.0F);
                player.addChatMessage(
                    new ChatComponentText(
                        EnumChatFormatting.DARK_PURPLE
                            + Platform.translate("chat.magianaturalis.chest.access.denied")));
            }
        }

        return true;
    }

    @Override
    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
        if (!Platform.isServer()) return;

        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof ArcaneChestBlockEntity) {
            ItemStack curStack = player.getCurrentEquippedItem();
            if (curStack != null && curStack.getItem() == ConfigItems.itemKey) {
                ArcaneChestBlockEntity chest = (ArcaneChestBlockEntity) tile;
                int keyAccessLevel = 0;

                if (player.capabilities.isCreativeMode || player.getGameProfile()
                    .getId()
                    .equals(chest.getOwner())) {
                    keyAccessLevel = 2;
                } else {
                    if (chest.accessList != null && !chest.accessList.isEmpty())
                        for (UserAccess user : chest.accessList) if (user.getUUID()
                            .equals(
                                player.getGameProfile()
                                    .getId())) {
                                        if (user.getAccessLevel() > 0) keyAccessLevel = 1;
                                        break;
                                    }
                }

                if (curStack.hasTagCompound() && curStack.stackTagCompound.hasKey("location")) {
                    String loc = x + "," + y + "," + z;
                    if (!loc.equals(curStack.stackTagCompound.getString("location"))) {
                        player.addChatMessage(new ChatComponentText("§5§o" + Platform.translate("tc.key7")));
                    } else if (loc.equals(curStack.stackTagCompound.getString("location"))) {
                        if (keyAccessLevel > 0) {
                            player.addChatMessage(new ChatComponentText("§5§o" + Platform.translate("tc.key8")));
                        } else {
                            chest.accessList.add(
                                new UserAccess(
                                    player.getGameProfile()
                                        .getId(),
                                    (byte) curStack.getItemDamage()));
                            world.markBlockForUpdate(x, y, z);

                            if (!player.capabilities.isCreativeMode) if (--curStack.stackSize <= 0)
                                player.inventory.mainInventory[player.inventory.currentItem] = null;

                            player.addChatMessage(
                                new ChatComponentText(
                                    "§5§o" + Platform.translate("chat.magianaturalis.key.access.chest")));
                            world.playSoundEffect(x, y, z, "thaumcraft:key", 1.0F, 0.9F);
                        }
                    }
                } else if (!curStack.hasTagCompound()) {
                    if (keyAccessLevel > 0) {
                        String loc = x + "," + y + "," + z;
                        ItemStack stack = new ItemStack(ConfigItems.itemKey, 1, curStack.getItemDamage());
                        stack.setTagInfo("location", new NBTTagString(loc));
                        stack.setTagInfo("type", new NBTTagByte((byte) -1));

                        if (!player.capabilities.isCreativeMode) if (--curStack.stackSize <= 0)
                            player.inventory.mainInventory[player.inventory.currentItem] = null;

                        if (!player.inventory.addItemStackToInventory(stack)) {
                            world.spawnEntityInWorld(
                                new EntityItem(world, player.posX, player.posY, player.posZ, stack));
                        }
                        // player.inventoryContainer.detectAndSendChanges();
                        world.playSoundEffect(x, y, z, "thaumcraft:key", 1.0F, 0.9F);
                    } else {
                        player.addChatMessage(
                            new ChatComponentText(
                                "§5§o" + Platform.translate("chat.magianaturalis.chest.access.denied")));
                        world.playSoundEffect(x, y, z, "thaumcraft:doorfail", 0.66F, 1.0F);
                    }
                }
            }
        }
    }

    @Override
    public boolean canHarvestBlock(EntityPlayer player, int meta) {
        return true;
    }

    @Override
    public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int x, int y, int z) {
        ArcaneChestBlockEntity chest = (ArcaneChestBlockEntity) world.getTileEntity(x, y, z);
        if (chest != null && player != null) {
            if (chest.getOwner()
                .equals(
                    player.getGameProfile()
                        .getId())) {
                return ForgeHooks.blockStrength(this, player, world, x, y, z);
            } else {
                if (chest.accessList != null && !chest.accessList.isEmpty())
                    for (UserAccess user : chest.accessList) if (user.getUUID()
                        .equals(
                            player.getGameProfile()
                                .getId())) {
                                    if (user.getAccessLevel() > 1)
                                        return ForgeHooks.blockStrength(this, player, world, x, y, z);
                                    break;
                                }
            }
        }
        return 0.0F;
    }

    @Override
    public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
        ArcaneChestBlockEntity chest = (ArcaneChestBlockEntity) world.getTileEntity(x, y, z);
        if (chest != null && entity instanceof EntityPlayer) {
            if (chest.getOwner()
                .equals(entity.getUniqueID())) {
                return true;
            } else {
                if (chest.accessList != null && !chest.accessList.isEmpty())
                    for (UserAccess user : chest.accessList) if (user.getUUID()
                        .equals(entity.getUniqueID())) return user.getAccessLevel() > 1;
            }
        }
        return false;
    }

    @Override
    public void onBlockExploded(World world, int x, int y, int z, Explosion explosion) {}

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        ArcaneChestBlockEntity chest = (ArcaneChestBlockEntity) world.getTileEntity(x, y, z);
        if (chest != null) {
            cachedChestType = chest.getChestType();
        }

        InventoryUtils.dropItems(world, x, y, z);
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<>();

        if (cachedChestType != null) {
            ItemStack stack = new ItemStack(this, 1, cachedChestType.id());
            cachedChestType = null;
            drops.add(stack);
        }

        return drops;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        float f = 0.0625F;
        return AxisAlignedBB.getBoundingBox(x + f, y, z + f, x + 1 - f, y + 1 - f, z + 1 - f);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess iBAccess, int x, int y, int z) {
        setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        ArcaneChestBlockEntity chest = (ArcaneChestBlockEntity) world.getTileEntity(x, y, z);
        if (chest == null) return null;

        return new ItemStack(
            this,
            1,
            chest.getChestType()
                .id());
    }

}

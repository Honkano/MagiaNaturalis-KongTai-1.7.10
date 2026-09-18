package com.github.elenterius.magianaturalis.item.alchemy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.github.elenterius.magianaturalis.MagiaNaturalis;
import com.github.elenterius.magianaturalis.util.Platform;
import com.github.elenterius.magianaturalis.util.alchemy.BlockMorpher;
import com.google.common.collect.Sets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.BlockCoordinates;
import thaumcraft.api.IArchitect;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.config.ConfigBlocks;

public class AlchemicalStoneItem extends Item implements IArchitect {

    private static final Set<Block> MORPHABLE_BLOCKS = Sets.newHashSet(
        Blocks.wool,
        Blocks.log,
        Blocks.log2,
        ConfigBlocks.blockMagicalLog,
        Blocks.stonebrick,
        Blocks.stained_hardened_clay,
        Blocks.carpet,
        Blocks.sandstone,
        Blocks.quartz_block,
        Blocks.cobblestone,
        Blocks.cobblestone_wall,
        Blocks.mossy_cobblestone,
        Blocks.dirt,
        Blocks.grass,
        Blocks.stained_glass,
        Blocks.stained_glass_pane);

    protected IIcon[] icons = new IIcon[2];

    public AlchemicalStoneItem() {
        super();
        maxStackSize = 1;
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    public boolean isQuicksilverStone(ItemStack stack) {
        return stack.getItemDamage() == 1;
    }

    public boolean isMutationStone(ItemStack stack) {
        return stack.getItemDamage() == 0;
    }

    @Override
    public Item setTextureName(String iconString) {
        this.iconString = null;
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister icon) {
        icons[0] = icon.registerIcon(MagiaNaturalis.rlString("mutation_stone"));
        icons[1] = icon.registerIcon(MagiaNaturalis.rlString("quicksilver_stone"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        return icons[damage];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        if (isQuicksilverStone(stack)) {
            list.add(EnumChatFormatting.DARK_PURPLE + "Of Twisting and Molding Status Effects");
        } else {
            list.add(EnumChatFormatting.DARK_PURPLE + "Mold the Visual");
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(this, 1, 0)); // Mutation Stone
        list.add(new ItemStack(this, 1, 1)); // Quicksilver Stone
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "." + stack.getItemDamage();
    }

    @Override
    public boolean doesContainerItemLeaveCraftingGrid(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        // do nothing
    }

    @Override
    public boolean hasContainerItem() {
        return true;
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        return stack;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int par7,
        float par8, float par9, float par10) {
        if (Platform.isClient()) return false;

        if (isMutationStone(stack)) {
            boolean success = BlockMorpher.setMorphOrRotation(
                world,
                x,
                y,
                z,
                world.getBlock(x, y, z),
                world.getBlockMetadata(x, y, z),
                player.isSneaking());
            if (success) world.playSoundAtEntity(player, "thaumcraft:zap", 0.5F, 1.0F);
            return success;
        }

        return false;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!Platform.isServer()) return stack;
        if (!isQuicksilverStone(stack)) return stack;
        if (player.isSneaking()) return stack;

        if (!MagiaNaturalis.proxyTC4.playerKnowledge
            .hasDiscoveredAspect(player.getCommandSenderName(), Aspect.EXCHANGE)) {
            player.addPotionEffect(new PotionEffect(Potion.confusion.getId(), 144, 1));
            return stack;
        }

        // noinspection unchecked,rawtypes
        List<PotionEffect> activePotionEffects = new ArrayList<>(player.getActivePotionEffects()); // make copy to avoid
                                                                                                   // concurrent
                                                                                                   // modification
                                                                                                   // exceptions

        for (PotionEffect effect : activePotionEffects) {
            if (player.inventory.consumeInventoryItem(Items.glowstone_dust)) {
                if (effect.getAmplifier() + 1 <= 2) {
                    player.addPotionEffect(
                        new PotionEffect(
                            effect.getPotionID(),
                            (int) (effect.getDuration() * 0.3F),
                            effect.getAmplifier() + 1));
                } else {
                    player.addPotionEffect(new PotionEffect(Potion.confusion.getId(), 288, 2));
                    player.addPotionEffect(new PotionEffect(Potion.poison.getId(), 144, 0));
                }
            } else {
                player.addPotionEffect(new PotionEffect(Potion.confusion.getId(), 144, 1));
                break;
            }
        }
        player.inventoryContainer.detectAndSendChanges();

        return stack;
    }

    @Override
    public ArrayList<BlockCoordinates> getArchitectBlocks(ItemStack stack, World world, int x, int y, int z, int side,
        EntityPlayer player) {
        if (!isMutationStone(stack)) return null;

        Block block = world.getBlock(x, y, z);
        if (!block.isAir(world, x, y, z) && canMorphBlock(block)) {
            ArrayList<BlockCoordinates> blocks = new ArrayList<>();
            blocks.add(new BlockCoordinates(x, y, z));
            return blocks;
        }

        return null;
    }

    private boolean canMorphBlock(Block block) {
        if (block instanceof BlockStairs || block instanceof BlockSlab || block instanceof BlockRotatedPillar)
            return true;
        return MORPHABLE_BLOCKS.contains(block);
    }

    @Override
    public boolean showAxis(ItemStack stack, World world, EntityPlayer player, int side, EnumAxis axis) {
        return false;
    }

}

package com.github.elenterius.magianaturalis.block.geopylon;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.util.ForgeDirection;

import com.github.elenterius.magianaturalis.util.Platform;
import com.github.elenterius.magianaturalis.util.WorldUtil;

import cpw.mods.fml.common.network.NetworkRegistry;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.wands.IWandable;
import thaumcraft.common.blocks.BlockCosmeticSolid;
import thaumcraft.common.lib.events.EssentiaHandler;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXBlockSparkle;
import thaumcraft.common.lib.world.biomes.BiomeHandler;

public class GeoPylonBlockEntity extends TileThaumcraft implements IAspectContainer, IWandable {

    public int ticks = 0;
    public BiomeGenBase cachedBiome = null;
    public boolean idle = true;
    int morphX = 0;
    int morphZ = 0;
    BiomeGenBase lastBiome = null;
    AspectList morphCost = new AspectList();
    AspectList realCost = new AspectList();

    @Override
    public void updateEntity() {
        if (!Platform.isServer()) return;

        boolean update = false;

        if (!idle && ++ticks % 5 == 0) {
            if (validateStructure()) {
                update = handleBiomeMorphing(8, cachedBiome);
            } else {
                idle = true;
                realCost = new AspectList();
                update = true;

                if (morphX == 0) {
                    morphZ--;
                    morphX = 1024;
                } else morphX--;
            }
        }

        if (update) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            markDirty();
        }
    }

    public boolean validateStructure() {
        if (!worldObj.isAirBlock(xCoord, yCoord - 1, zCoord)) return false;

        for (int i = 1; i <= 3; i++) {
            Block block = worldObj.getBlock(xCoord, yCoord - 1 - i, zCoord);
            if (!(block instanceof BlockCosmeticSolid
                && worldObj.getBlockMetadata(xCoord, yCoord - 1 - i, zCoord) == 0)) {
                return false;
            }
        }

        return true;
    }

    public boolean handleBiomeMorphing(int radius, BiomeGenBase newBiome) {
        boolean update = false;
        boolean isComplete = false;
        if (realCost.visSize() > 0) {
            for (Aspect aspect : realCost.getAspects()) if (realCost.getAmount(aspect) > 0)
                if (EssentiaHandler.drainEssentia(this, aspect, ForgeDirection.UNKNOWN, 12)) {
                    realCost.reduce(aspect, 1);
                    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                    markDirty();
                    break;
                }

            if ((realCost.visSize() <= 0)) {
                isComplete = true;
                update = true;
                realCost = new AspectList();
                int posX = xCoord - radius + morphX;
                int posZ = zCoord - radius + morphZ;
                int posY = worldObj.getTopSolidOrLiquidBlock(posX, posZ);
                WorldUtil.setBiomeAt(worldObj, posX, posZ, newBiome);

                worldObj.getChunkFromBlockCoords(posX, posZ)
                    .setChunkModified();
                worldObj.markBlockForUpdate(posX, posY, posZ);

                PacketHandler.INSTANCE.sendToAllAround(
                    new PacketFXBlockSparkle(xCoord, yCoord, zCoord, newBiome.color),
                    new NetworkRegistry.TargetPoint(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 32.0D));
            }
        } else isComplete = true;

        if (isComplete && ticks % 20 == 0) {
            if (morphZ < radius * 2) {
                if (morphX < radius * 2) {
                    morphX++;
                } else {
                    morphX = 0;
                    morphZ++;
                }

                int posX = xCoord - radius + morphX;
                int posZ = zCoord - radius + morphZ;
                BiomeGenBase oldBiome = worldObj.getBiomeGenForCoords(posX, posZ);

                if (newBiome != oldBiome
                    && Math.sqrt((morphX - radius) * (morphX - radius) + (morphZ - radius) * (morphZ - radius))
                        <= radius) {
                    if (lastBiome != newBiome) // prevent recalculation of Cost every Cycle
                    {
                        lastBiome = newBiome;
                        morphCost = calculateMorphCost(newBiome);
                    }
                    realCost = morphCost.copy();
                    return true;
                }
            } else {
                morphZ = 0;
                idle = true;
                update = true;
            }
        }

        return update;
    }

    public AspectList calculateMorphCost(BiomeGenBase newBiome) {
        BiomeDictionary.Type[] biomeTypes = BiomeDictionary.getTypesForBiome(newBiome);
        AspectList aspectCost = new AspectList();
        for (Type biomeType : biomeTypes) if (biomeType != null) {
            if (biomeType != Type.MAGICAL) // Because Thaumcraft adds null aspect for magical biome type
            {
                Aspect aspect = (Aspect) BiomeHandler.biomeInfo.get(biomeType)
                    .get(1);
                if (aspect != null) {
                    int aura = (int) BiomeHandler.biomeInfo.get(biomeType)
                        .get(0);
                    aspectCost.add(aspect, Math.round(aura * 2F / 100F));
                }
            } else aspectCost.add(Aspect.MAGIC, 2);
        }
        return aspectCost;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        morphX = data.getByte("morphX");
        morphZ = data.getByte("morphZ");
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setByte("morphX", (byte) morphX);
        data.setByte("morphZ", (byte) morphZ);
    }

    @Override
    public void readCustomNBT(NBTTagCompound data) // Syncs to Client
    {
        realCost.readFromNBT(data);
        idle = data.getBoolean("idle");

        int id = data.getInteger("biomeID");
        cachedBiome = id >= 0 ? BiomeGenBase.getBiome(id) : null;
    }

    @Override
    public void writeCustomNBT(NBTTagCompound data) // Syncs to Client
    {
        realCost.writeToNBT(data);
        data.setBoolean("idle", idle);
        data.setInteger("biomeID", cachedBiome != null ? cachedBiome.biomeID : -1);
    }

    @Override
    public AspectList getAspects() {
        return realCost;
    }

    @Override
    public void setAspects(AspectList aspects) {}

    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return false;
    }

    @Override
    public int addToContainer(Aspect tag, int amount) {
        return 0;
    }

    @Override
    public boolean takeFromContainer(Aspect tag, int amount) {
        return false;
    }

    @Override
    public boolean takeFromContainer(AspectList ot) {
        return false;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        return false;
    }

    @Override
    public boolean doesContainerContain(AspectList ot) {
        return false;
    }

    @Override
    public int containerContains(Aspect tag) {
        return 0;
    }

    @Override
    public int onWandRightClick(World world, ItemStack wandStack, EntityPlayer player, int x, int y, int z, int side,
        int md) {
        if (player.isSneaking()) {
            realCost = calculateMorphCost(cachedBiome);
        } else {
            idle = !idle;
        }
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        markDirty();
        return -1;
    }

    @Override
    public ItemStack onWandRightClick(World world, ItemStack wandStack, EntityPlayer player) {
        return null;
    }

    @Override
    public void onUsingWandTick(ItemStack wandStack, EntityPlayer player, int count) {}

    @Override
    public void onWandStoppedUsing(ItemStack wandStack, World world, EntityPlayer player, int count) {}

}

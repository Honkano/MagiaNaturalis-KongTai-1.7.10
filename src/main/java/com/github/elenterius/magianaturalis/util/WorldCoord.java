package com.github.elenterius.magianaturalis.util;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import thaumcraft.api.BlockCoordinates;

public class WorldCoord extends BlockCoordinates {

    public WorldCoord(TileEntity tile) {
        super(tile);
    }

    public WorldCoord(BlockCoordinates blockCoordinates) {
        super(blockCoordinates);
    }

    public WorldCoord(int x, int y, int z) {
        super(x, y, z);
    }

    public void add(ForgeDirection direction, int length) {
        this.x += direction.offsetX * length;
        this.y += direction.offsetY * length;
        this.z += direction.offsetZ * length;
    }

    public void add(int x, int y, int z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    public void subtract(int x, int y, int z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
    }

}

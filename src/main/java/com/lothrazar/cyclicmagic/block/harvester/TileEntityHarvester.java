/*******************************************************************************
 * The MIT License (MIT)
 * 
 * Copyright (C) 2014-2018 Sam Bassett (aka Lothrazar)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.lothrazar.cyclicmagic.block.harvester;

import java.util.ArrayList;
import java.util.List;
import com.lothrazar.cyclicmagic.block.core.TileEntityBaseMachineInvo;
import com.lothrazar.cyclicmagic.gui.ITilePreviewToggle;
import com.lothrazar.cyclicmagic.gui.ITileRedstoneToggle;
import com.lothrazar.cyclicmagic.util.UtilHarvester;
import com.lothrazar.cyclicmagic.util.UtilInventoryTransfer;
import com.lothrazar.cyclicmagic.util.UtilItemStack;
import com.lothrazar.cyclicmagic.util.UtilParticle;
import com.lothrazar.cyclicmagic.util.UtilShape;
import com.lothrazar.cyclicmagic.util.UtilWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityHarvester extends TileEntityBaseMachineInvo implements ITileRedstoneToggle, ITilePreviewToggle, ITickable {

  private static final int MAX_SIZE = 7;//radius 7 translates to 15x15 area (center block + 7 each side)
  private int size = MAX_SIZE;//default to the old fixed size, backwards compat
  public static int TIMER_FULL = 200;

  public static enum Fields {
    TIMER, REDSTONE, SIZE, RENDERPARTICLES, HARVESTMODE;
  }

  private int normalModeIfZero = 0;//if this == 1, then do full field at once

  public TileEntityHarvester() {
    super(3 * 9);
    this.initEnergy(BlockHarvester.FUEL_COST);
    this.timer = TIMER_FULL;
    this.setSlotsForExtract(0, this.getSizeInventory());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public AxisAlignedBB getRenderBoundingBox() {
    return TileEntity.INFINITE_EXTENT_AABB;
  }

  @Override
  public void readFromNBT(NBTTagCompound tags) {
    super.readFromNBT(tags);
    this.size = tags.getInteger(NBT_SIZE);
    this.needsRedstone = tags.getInteger(NBT_REDST);
    this.renderParticles = tags.getInteger(NBT_RENDER);
    this.normalModeIfZero = tags.getInteger("HM");
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound tags) {
    tags.setInteger(NBT_REDST, this.needsRedstone);
    tags.setInteger(NBT_RENDER, renderParticles);
    tags.setInteger(NBT_SIZE, size);
    tags.setInteger("HM", normalModeIfZero);
    return super.writeToNBT(tags);
  }

  @Override
  public void update() {
    if (this.isRunning() == false) {
      return;
    }
    if (this.isInventoryFull()) {
      return;
    }
    if (this.updateEnergyIsBurning() == false) {
      return;
    }
    if (this.updateTimerIsZero()) {
      timer = TIMER_FULL;//harvest worked!
      if (this.normalModeIfZero == 0) {
        if (tryHarvestSingle(getTargetPos()) == false) {
          timer = 1;//harvest didnt work, try again really quick
        }
      }
      else {
        tryHarvestArea();
      }
    }
  }

  private void tryHarvestArea() {
    List<BlockPos> shape = getShapeFilled();
    for (BlockPos posCurrent : shape) {
      this.tryHarvestSingle(posCurrent);
      if (this.isInventoryFull()) {
        //still might drop items for example if there is room for wheat, but seeds come in
        //but if that happens, just get item collectors or faster piping .
        //worth keepin for single cropss like netherwart
        return;
      }
    }
  }

  private boolean tryHarvestSingle(BlockPos harvestPos) {
    NonNullList<ItemStack> drops = UtilHarvester.harvestSingle(getWorld(), harvestPos);
    if (drops.size() > 0) {
      this.updateEnergyIsBurning();
      if (isPreviewVisible()) {
        UtilParticle.spawnParticle(getWorld(), EnumParticleTypes.DRAGON_BREATH, harvestPos);
      }
      setOutputItems(drops);
      return true;
    }
    else {
      return false;
    }
  }

  private void setOutputItems(List<ItemStack> output) {
    ArrayList<ItemStack> toDrop = UtilInventoryTransfer.dumpToIInventory(output, this, 0, this.getSizeInventory());
    if (!toDrop.isEmpty()) {
      for (ItemStack s : toDrop) {
        UtilItemStack.dropItemStackInWorld(this.getWorld(), this.getPos().up(), s);
      }
    }
  }

  public BlockPos getTargetCenter() {
    //move center over that much, not including exact horizontal
    return this.getPos().offset(this.getCurrentFacing(), this.size + 1);
  }

  private BlockPos getTargetPos() {
    return UtilWorld.getRandomPos(getWorld().rand, getTargetCenter(), this.size);
  }

  @Override
  public int[] getFieldOrdinals() {
    return super.getFieldArray(getFieldCount());
  }

  @Override
  public int getFieldCount() {
    return Fields.values().length;
  }

  @Override
  public int getField(int id) {
    switch (Fields.values()[id]) {
      case TIMER:
        return timer;
      case REDSTONE:
        return this.needsRedstone;
      case SIZE:
        return this.size;
      case RENDERPARTICLES:
        return this.renderParticles;
      case HARVESTMODE:
        return this.normalModeIfZero;
    }
    return -1;
  }

  @Override
  public void setField(int id, int value) {
    switch (Fields.values()[id]) {
      case TIMER:
        this.timer = value;
      break;
      case REDSTONE:
        this.needsRedstone = value;
      break;
      case SIZE:
        if (value > MAX_SIZE) {
          value = 0;
        }
        size = value;
      break;
      case RENDERPARTICLES:
        this.renderParticles = value % 2;
      break;
      case HARVESTMODE:
        this.normalModeIfZero = value % 2;
      break;
    }
  }

  @Override
  public void toggleNeedsRedstone() {
    int val = this.needsRedstone + 1;
    if (val > 1) {
      val = 0;//hacky lazy way
    }
    this.setField(Fields.REDSTONE.ordinal(), val);
  }

  @Override
  public boolean onlyRunIfPowered() {
    return this.needsRedstone == 1;
  }

  @Override
  public List<BlockPos> getShape() {
    return UtilShape.squareHorizontalHollow(getTargetCenter(), this.size);
  }

  private List<BlockPos> getShapeFilled() {
    return UtilShape.squareHorizontalFull(getTargetCenter(), this.size);
  }

  @Override
  public boolean isPreviewVisible() {
    return this.getField(Fields.RENDERPARTICLES.ordinal()) == 1;
  }
}

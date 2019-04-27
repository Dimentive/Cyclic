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
package com.lothrazar.cyclicmagic.block.solidifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.lothrazar.cyclicmagic.block.core.TileEntityBaseMachineFluid;
import com.lothrazar.cyclicmagic.data.ITileRedstoneToggle;
import com.lothrazar.cyclicmagic.liquid.FluidTankFixDesync;
import com.lothrazar.cyclicmagic.util.Const;
import com.lothrazar.cyclicmagic.util.UtilItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;

public class TileSolidifier extends TileEntityBaseMachineFluid implements ITileRedstoneToggle, ITickable {

  public static final int RECIPE_SIZE = 4;
  public static final int SLOT_OUTPUT = RECIPE_SIZE;
  public static final int TANK_FULL = 16 * 1000;
  public final static int TIMER_FULL = Const.TICKS_PER_SEC * 5;

  public static enum Fields {
    REDSTONE, TIMER, RECIPELOCKED;
  }

  private int recipeIsLocked = 0;
  private InventoryCrafting crafting = new InventoryCrafting(new ContainerDummyHydrator(), RECIPE_SIZE / 2, RECIPE_SIZE / 2);
  private RecipeSolidifier currentRecipe;

  public TileSolidifier() {
    super(RECIPE_SIZE + 1);// 4in, 1out 
    tank = new FluidTankFixDesync(TANK_FULL, this);
    timer = TIMER_FULL;
    this.setSlotsForInsert(0, 3);
    this.setSlotsForExtract(4);
  }

  @Override
  public int[] getFieldOrdinals() {
    return super.getFieldArray(Fields.values().length);
  }

  @Override
  public int getFieldCount() {
    return getFieldOrdinals().length;
  }

  @Override
  public void update() {
    currentRecipe = null;
    if (this.currentRecipe == null) {
      this.findMatchingRecipe();
      this.updateLockSlots();
    }
    if (this.isRunning() == false || this.isInventoryFull(RECIPE_SIZE)) {
      return;//dont drain power when full  
    }
    //no fluid? stop now 
    if (this.getCurrentFluidStackAmount() == 0) {
      return;
    }
    if (currentRecipe == null || this.updateEnergyIsBurning() == false) {
      return;
    }
    if (this.updateTimerIsZero()) { // time to burn!
      if (tryProcessRecipe()) {
        this.timer = TIMER_FULL;
      }
    }
  }

  private void updateLockSlots() {
    if (this.recipeIsLocked == 1) {
      if (this.currentRecipe != null) {
        List<Integer> slotsImport = new ArrayList<Integer>();
        for (int slot = 0; slot < RECIPE_SIZE; slot++) {
          if (this.getStackInSlot(slot).isEmpty() == false) {
            slotsImport.add(slot);
          }
        }
        this.setSlotsForInsert(slotsImport);
      }
    }
    else {//all are free game
      this.setSlotsForInsert(Arrays.asList(0, 1, 2, 3));
    }
  }

  public boolean tryProcessRecipe() {
    if (currentRecipe != null) {
      if (this.getCurrentFluidStackAmount() >= currentRecipe.getFluidCost()
          && this.inventoryHasRoom(4, currentRecipe.getRecipeOutput().copy())) {
        if (currentRecipe.tryPayCost(this, this.tank, this.recipeIsLocked == 1)) {
          //only create the output if cost was successfully paid
          this.sendOutputItem(currentRecipe.getRecipeOutput().copy());
        }
        return true;
      }
    }
    return false;
  }

  /**
   * try to match a shaped or shapeless recipe
   * 
   * @return
   */
  private void findMatchingRecipe() {
    boolean allAir = true;
    for (int i = 0; i < RECIPE_SIZE; i++) {
      //if ANY slot is non empty, we will get an && false which makes false 
      allAir = (allAir && this.getStackInSlot(i).isEmpty());
      this.crafting.setInventorySlotContents(i, this.getStackInSlot(i).copy());
    }
    if (allAir) {//short cut 
      return;
    }
    for (RecipeSolidifier rec : RecipeSolidifier.recipes) {
      if (rec.matches(this.crafting, world)) {
        currentRecipe = rec;
      }
    }
  }

  public void sendOutputItem(ItemStack itemstack) {
    if (!itemstack.isEmpty() && itemstack.getMaxStackSize() != 0) {
      itemstack = tryMergeStackIntoSlot(itemstack, SLOT_OUTPUT);
    }
    if (!itemstack.isEmpty() && itemstack.getMaxStackSize() != 0) { //FULL 
      UtilItemStack.dropItemStackInWorld(this.getWorld(), this.pos.up(), itemstack);
    }
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound compound) {
    compound.setInteger("rlock", recipeIsLocked);
    return super.writeToNBT(compound);
  }

  @Override
  public void readFromNBT(NBTTagCompound compound) {
    super.readFromNBT(compound);
    this.recipeIsLocked = compound.getInteger("rlock");
  }

  @Override
  public int getField(int id) {
    switch (Fields.values()[id]) {
      case REDSTONE:
        return this.needsRedstone;
      case TIMER:
        return this.timer;
      case RECIPELOCKED:
        return this.recipeIsLocked;
    }
    return -1;
  }

  @Override
  public void setField(int id, int value) {
    switch (Fields.values()[id]) {
      case REDSTONE:
        this.needsRedstone = value;
      break;
      case TIMER:
        this.timer = value;
      break;
      case RECIPELOCKED:
        this.recipeIsLocked = value % 2;
        this.updateLockSlots();
      break;
    }
  }

  @Override
  public void toggleNeedsRedstone() {
    this.needsRedstone = (this.needsRedstone + 1) % 2;
  }

  @Override
  public boolean onlyRunIfPowered() {
    return this.needsRedstone == 1;
  }

  public float getFillRatio() {
    return tank.getFluidAmount() / tank.getCapacity();
  }

  /**
   * For the crafting inventory, since its never in GUI and is just used for auto processing
   * 
   * @author Sam
   */
  public static class ContainerDummyHydrator extends Container {

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
      return false;
    }
  }
}

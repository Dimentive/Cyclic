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
package com.lothrazar.cyclicmagic.block.cablepump.item;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import com.lothrazar.cyclicmagic.block.cable.TileEntityCableBase;
import com.lothrazar.cyclicmagic.block.cablepump.TileEntityBasePump;
import com.lothrazar.cyclicmagic.data.ITileRedstoneToggle;
import com.lothrazar.cyclicmagic.data.ITileStackWrapper;
import com.lothrazar.cyclicmagic.gui.container.StackWrapper;
import com.lothrazar.cyclicmagic.util.UtilItemStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;

public class TileEntityItemPump extends TileEntityBasePump implements ITileStackWrapper, ITickable, ITileRedstoneToggle {

  private NonNullList<StackWrapper> stacksWrapped = NonNullList.withSize(9, new StackWrapper());
  private static final int SLOT_TRANSFER = 0;
  //  private static int TRANSFER_ITEM_TICK_DELAY = 0;

  public static enum Fields {
    REDSTONE, FILTERTYPE, SPEED;
  }

  private int itemTransferCooldown = 0;
  private int filterType = 0;

  public TileEntityItemPump() {
    super(1);
    this.setSlotsForExtract(0);
    this.setSlotsForInsert(0);
    this.speed = 1;
  }

  @Override
  public int[] getFieldOrdinals() {
    return super.getFieldArray(Fields.values().length);
  }

  @Override
  public int getField(int id) {
    switch (Fields.values()[id]) {
      case FILTERTYPE:
        return this.filterType;
      case REDSTONE:
        return this.needsRedstone;
      case SPEED:
        return speed;
    }
    return 0;
  }

  @Override
  public void setField(int id, int value) {
    switch (Fields.values()[id]) {
      case FILTERTYPE:
        this.filterType = value % 2;
      break;
      case REDSTONE:
        this.needsRedstone = value % 2;
      break;
      case SPEED:
        this.speed = value;
      break;
    }
  }

  private boolean isWhitelist() {
    //default is zero, and default blacklist makes sense -> it is empty, so everythings allowed
    return this.filterType == 1;
  }

  private boolean isStackInvalid(ItemStack stackToTest) {
    List<ItemStack> inventoryContents = getFilterNonempty();
    //edge case: if list is empty ?? should be covered already
    if (OreDictionary.containsMatch(true,
        NonNullList.<ItemStack> from(ItemStack.EMPTY, inventoryContents.toArray(new ItemStack[0])),
        stackToTest)) {
      //the item that I target matches something in my filter
      //meaning, if i am in whitelist mode, it is valid, it matched the allowed list
      //if I am in blacklist mode, nope not valid
      return !this.isWhitelist();
    }
    //here is the opposite: i did NOT match the list
    return this.isWhitelist();
  }

  private List<ItemStack> getFilterNonempty() {
    List<ItemStack> filt = new ArrayList<ItemStack>();
    for (StackWrapper wrap : this.stacksWrapped) {
      if (wrap.isEmpty() == false) {
        filt.add(wrap.getStack().copy());
      }
    }
    return filt;
  }

  @Override
  public EnumFacing getCurrentFacing() {
    // weird hack IDK when its needed
    //but it makes sure this always returns where the white connectory connector exists
    EnumFacing facingTo = super.getCurrentFacing();
    if (facingTo.getAxis().isVertical()) {
      facingTo = facingTo.getOpposite();
    }
    return facingTo;
  }

  /**
   * for every side connected to me pull fluid in from it UNLESS its my current facing direction. for THAT side, i push fluid out from me pull first then push
   *
   * TODO: UtilFluid that does a position, a facing, and tries to move fluid across
   *
   *
   */
  @Override
  public void update() {
    if (this.isRunning() == false) {
      return;
    }
    this.tryExport();
    this.tryImport();
  }

  public void tryExport() {
    if (this.getStackInSlot(SLOT_TRANSFER).isEmpty()) {
      return;//im empty nothing to give
    }
    boolean outputSuccess = false;
    ItemStack stackToExport = this.getStackInSlot(SLOT_TRANSFER).copy();
    List<EnumFacing> sidesOut = getSidesNotFacing();
    for (EnumFacing facingDir : sidesOut) {
      if (this.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facingDir) == false) {
        continue;
      }
      EnumFacing themFacingMe = facingDir.getOpposite();
      BlockPos posTarget = pos.offset(facingDir);
      TileEntity tileTarget = world.getTileEntity(posTarget);
      if (tileTarget == null ||
          tileTarget.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, themFacingMe) == false) {
        continue;
      }
      stackToExport = this.getStackInSlot(SLOT_TRANSFER).copy();
      if (stackToExport.isEmpty()) {
        continue;
      }
      ItemStack leftAfterDeposit = UtilItemStack.tryDepositToHandler(world, posTarget, themFacingMe, stackToExport);
      if (leftAfterDeposit.isEmpty() || leftAfterDeposit.getCount() != stackToExport.getCount()) {
        this.setInventorySlotContents(SLOT_TRANSFER, leftAfterDeposit);
        //one or more was put in
        outputSuccess = true;
      }
      if (outputSuccess && world.getTileEntity(pos.offset(facingDir)) instanceof TileEntityCableBase) {
        TileEntityCableBase cable = (TileEntityCableBase) world.getTileEntity(pos.offset(facingDir));
        if (cable.isItemPipe()) {
          cable.updateIncomingItemFace(themFacingMe);
        }
      }
      if (outputSuccess) {
        break;
      }
    }
  }

  public void tryImport() {
    if (this.getStackInSlot(SLOT_TRANSFER).isEmpty() == false) {
      return;//im full leave me alone
    }
    EnumFacing importFromSide = this.getCurrentFacing();
    BlockPos posTarget = pos.offset(importFromSide);
    TileEntity tileTarget = world.getTileEntity(posTarget);
    if (tileTarget == null) {
      return;
    }
    ItemStack itemTarget;
    if (tileTarget.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, importFromSide.getOpposite())) {
      IItemHandler itemHandlerFrom = tileTarget.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, importFromSide.getOpposite());
      for (int i = 0; i < itemHandlerFrom.getSlots(); i++) {
        itemTarget = itemHandlerFrom.getStackInSlot(i);
        if (itemTarget.isEmpty()) {
          continue;
        }
        //check against whitelist/blacklist system
        if (this.isStackInvalid(itemTarget)) {
          //          ModCyclic.logger.log("not valid " + itemTarget.getDisplayName());
          continue;
        }
        //passed filter check, so do the transaction
        ItemStack pulled = itemHandlerFrom.extractItem(i, this.speed, false);
        if (pulled != null && pulled.isEmpty() == false) {
          this.setInventorySlotContents(SLOT_TRANSFER, pulled.copy());
          return;
        }
      }
    }
  }

  @Override
  public void readFromNBT(NBTTagCompound compound) {
    super.readFromNBT(compound);
    itemTransferCooldown = compound.getInteger("itemTransferCooldown");
    needsRedstone = compound.getInteger(NBT_REDST);
    filterType = compound.getInteger("wbtype");
    readStackWrappers(stacksWrapped, compound);
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound compound) {
    writeStackWrappers(stacksWrapped, compound);
    compound.setInteger(NBT_REDST, needsRedstone);
    compound.setInteger("itemTransferCooldown", itemTransferCooldown);
    compound.setInteger("wbtype", filterType);
    return super.writeToNBT(compound);
  }

  @Override
  public void toggleNeedsRedstone() {
    int val = (this.needsRedstone + 1) % 2;
    this.setField(Fields.REDSTONE.ordinal(), val);
  }

  @Override
  public boolean onlyRunIfPowered() {
    return this.needsRedstone == 1;
  }

  @Override
  public StackWrapper getStackWrapper(int i) {
    return stacksWrapped.get(i);
  }

  @Override
  public void setStackWrapper(int i, StackWrapper stack) {
    stacksWrapped.set(i, stack);
  }

  @Override
  public int getWrapperCount() {
    return stacksWrapped.size();
  }

  @Override
  public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
    if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      return false;
    }
    return super.hasCapability(capability, facing);
  }
}

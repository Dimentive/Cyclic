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
package com.lothrazar.cyclicmagic.block.cable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import com.google.common.collect.Maps;
import com.lothrazar.cyclicmagic.ModCyclic;
import com.lothrazar.cyclicmagic.block.IFacingBlacklist;
import com.lothrazar.cyclicmagic.block.core.TileEntityBaseMachineFluid;
import com.lothrazar.cyclicmagic.liquid.FluidTankBase;
import com.lothrazar.cyclicmagic.util.UtilChat;
import com.lothrazar.cyclicmagic.util.UtilFluid;
import com.lothrazar.cyclicmagic.util.UtilItemStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public abstract class TileEntityCableBase extends TileEntityBaseMachineFluid implements ITickable, IFacingBlacklist {

  private static final int SLOT = 0;
  private static final int TIMER_SIDE_INPUT = 15;
  public static final int TRANSFER_FLUID_PER_TICK = 1000;
  //config
  //TODO: timer to slow down item rate
  public static final int TRANSFER_ENERGY_PER_TICK = 16 * 1000;
  private static final int TICKS_TEXT_CACHED = TIMER_SIDE_INPUT * 2;
  private int labelTimer = 0;
  private String labelText = "";
  private boolean itemTransport = false;
  private boolean fluidTransport = false;
  private boolean energyTransport = false;
  private Map<EnumFacing, Integer> mapIncomingFluid = Maps.newHashMap();
  protected Map<EnumFacing, Integer> mapIncomingItems = Maps.newHashMap();
  private Map<EnumFacing, Integer> mapIncomingEnergy = Maps.newHashMap();
  private Map<EnumFacing, Boolean> mapBlacklist = Maps.newHashMap();

  public TileEntityCableBase(boolean hasItems, boolean hasFluid, boolean hasEnergy) {
    super((hasItems) ? 1 : 0);
    if (hasFluid) {
      tank = new FluidTankBase(TRANSFER_FLUID_PER_TICK);
    }
    if (hasEnergy) {
      initEnergy(0, TRANSFER_ENERGY_PER_TICK);
    }
    itemTransport = hasItems;
    fluidTransport = hasFluid;
    energyTransport = hasEnergy;
    for (EnumFacing f : EnumFacing.values()) {
      mapIncomingFluid.put(f, 0);
      mapIncomingItems.put(f, 0);
      mapIncomingEnergy.put(f, 0);
      mapBlacklist.put(f, false);
    }
  }

  public boolean isItemPipe() {
    return this.itemTransport;
  }

  public boolean isFluidPipe() {
    return this.fluidTransport;
  }

  public boolean isEnergyPipe() {
    return this.energyTransport;
  }

  @Override
  public void readFromNBT(NBTTagCompound compound) {
    super.readFromNBT(compound);
    labelText = compound.getString("label");
    labelTimer = compound.getInteger("labelt");
    for (EnumFacing f : EnumFacing.values()) {
      mapIncomingItems.put(f, compound.getInteger(f.getName() + "_incoming"));
      mapIncomingFluid.put(f, compound.getInteger(f.getName() + "_incfluid"));
      mapIncomingEnergy.put(f, compound.getInteger(f.getName() + "_incenergy"));
      mapBlacklist.put(f, compound.getBoolean(f.getName() + "_blocked"));
    }
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound compound) {
    super.writeToNBT(compound);
    compound.setString("label", labelText);
    compound.setInteger("labelt", labelTimer);
    for (EnumFacing f : EnumFacing.values()) {
      compound.setInteger(f.getName() + "_incoming", mapIncomingItems.get(f));
      compound.setInteger(f.getName() + "_incfluid", mapIncomingFluid.get(f));
      compound.setInteger(f.getName() + "_incenergy", mapIncomingEnergy.get(f));
      compound.setBoolean(f.getName() + "_blocked", mapBlacklist.get(f));
    }
    return compound;
  }

  public String getLabelTextOrEmpty() {
    return labelText.isEmpty() ? UtilChat.lang("cyclic.item.empty") : this.labelText;
  }

  /**
   * with normal item movement it moves too fast for user to read cache the current item for a few ticks so full item pipes dont show empty or flashing fast text
   */
  private void tickLabelText() {
    this.labelTimer--;
    if (this.labelTimer > 0) {
      return;
    }
    this.labelTimer = 0;
    this.labelText = "";
    List<String> validLabels = new ArrayList<String>();
    if (this.isItemPipe() && this.getStackInSlot(0).isEmpty() == false) {
      validLabels.add(this.getIncomingStringsItem());
    }
    if (this.isFluidPipe() && this.getCurrentFluidStack() != null) {
      // FluidStack fs = this.getCurrentFluidStack();
      validLabels.add(this.getIncomingStringsFluid());
    }
    if (this.isEnergyPipe() &&
        this.energyStorage != null && this.energyStorage.getEnergyStored() > 0) {
      validLabels.add(this.getIncomingStringsEnergy());
    }
    //if its a multi pipe pick a random one
    if (validLabels.size() > 0) {
      this.labelText = validLabels.get(MathHelper.getInt(this.world.rand, 0, validLabels.size() - 1));
      this.labelTimer = TICKS_TEXT_CACHED;
    }
  }

  private String getIncomingStringsFromMap(Map<EnumFacing, Integer> map) {
    String in = "";
    for (EnumFacing f : EnumFacing.values()) {
      if (map.get(f) > 0)
        in += f.name().toLowerCase() + " ";
    }
    return in.trim();
  }

  private String getIncomingStringsFluid() {
    String tmpName = this.getCurrentFluidStackAmount() + " " + this.getCurrentFluidStack().getLocalizedName();
    String incoming = getIncomingStringsFromMap(this.mapIncomingFluid);
    if (incoming.isEmpty() == false) {
      tmpName += " " + UtilChat.lang("cyclic.fluid.flowing") + incoming;
    }
    return tmpName;
  }

  private String getIncomingStringsItem() {
    String tmpName = this.getStackInSlot(0).getDisplayName();
    String incoming = getIncomingStringsFromMap(this.mapIncomingItems);
    if (incoming.isEmpty() == false) {
      tmpName += " " + UtilChat.lang("cyclic.item.flowing") + incoming;
    }
    return tmpName;
  }

  private String getIncomingStringsEnergy() {
    String tmpName = this.energyStorage.getEnergyStored() + "";
    String incoming = getIncomingStringsFromMap(this.mapIncomingEnergy);
    if (incoming.isEmpty() == false) {
      tmpName += " " + UtilChat.lang("cyclic.fluid.flowing") + incoming;
    }
    return tmpName;
  }

  public void updateIncomingFluidFace(EnumFacing inputFrom) {
    mapIncomingFluid.put(inputFrom, TIMER_SIDE_INPUT);
  }

  private boolean isFluidIncomingFromFace(EnumFacing face) {
    return mapIncomingFluid.get(face) > 0;
  }

  private boolean isEnergyIncomingFromFace(EnumFacing face) {
    return mapIncomingEnergy.get(face) > 0;
  }

  public void updateIncomingEnergyFace(EnumFacing inputFrom) {
    mapIncomingEnergy.put(inputFrom, TIMER_SIDE_INPUT);
  }

  public void updateIncomingItemFace(EnumFacing inputFrom) {
    this.mapIncomingItems.put(inputFrom, TIMER_SIDE_INPUT);
  }

  private boolean isItemIncomingFromFace(EnumFacing face) {
    return mapIncomingItems.get(face) > 0;
  }

  @Override
  public void update() {
    this.tickLabelText();
    if (this.fluidTransport) {
      this.tickDownIncomingFluidFaces();
    }
    if (this.itemTransport) {
      this.tickDownIncomingItemFaces();
    }
    if (this.energyTransport) {
      this.tickDownIncomingPowerFaces();
    }
    //tick down any incoming sides
    //now look over any sides that are NOT incoming, try to export
    //Actually shuffle the positions. if we are at a 3 way juncture, spread out where it goes first
    try {
      if (!world.isRemote)
        tickCableFlow();
    }
    catch (Exception e) {
      // errors from other mods as well as this.
      //example:  mcjty.rftools.blocks.powercell.PowerCellTileEntity.getNetwork(PowerCellTileEntity.java:155)
      ModCyclic.logger.error("Error outputing from cable", e);
    }
  }

  private void tickCableFlow() {
    ArrayList<EnumFacing> shuffledFaces = new ArrayList<>();
    for (int i = 0; i < EnumFacing.values().length; i++) {
      shuffledFaces.add(EnumFacing.values()[i]);
    }
    Collections.shuffle(shuffledFaces);
    for (EnumFacing exportToSide : shuffledFaces) {
      if (this.isItemPipe() && this.isItemIncomingFromFace(exportToSide) == false
          && this.getBlacklist(exportToSide) == false) {
        moveItems(exportToSide);
      }
      if (this.isFluidPipe() && this.isFluidIncomingFromFace(exportToSide) == false
          && this.getBlacklist(exportToSide) == false) {
        //ok, fluid is not incoming from here. so lets output some
        moveFluid(exportToSide);
      }
      if (this.isEnergyPipe() && this.isEnergyIncomingFromFace(exportToSide) == false
          && this.getBlacklist(exportToSide) == false) {
        moveEnergy(exportToSide);
      }
    }
  }

  private void moveItems(EnumFacing myFacingDir) {
    if (this.getStackInSlot(SLOT).isEmpty()) {
      return;
    }
    ItemStack stackToExport = this.getStackInSlot(SLOT).copy();
    //ok,  not incoming from here. so lets output some
    BlockPos posTarget = pos.offset(myFacingDir);
    TileEntity tileTarget = world.getTileEntity(posTarget);
    if (tileTarget == null) {
      return;
    }
    boolean outputSuccess = false;
    EnumFacing themFacingMe = myFacingDir.getOpposite();
    ItemStack leftAfterDeposit = UtilItemStack.tryDepositToHandler(world, posTarget, themFacingMe, stackToExport);
    if (leftAfterDeposit.isEmpty() || leftAfterDeposit.getCount() != stackToExport.getCount()) {
      //   if (leftAfterDeposit.getCount() < stackToExport.getCount()) { //something moved!
      //then save result
      //tood capability for sided
      this.setInventorySlotContents(SLOT, leftAfterDeposit);
      outputSuccess = true;
    }
    if (outputSuccess && tileTarget instanceof TileEntityCableBase) {
      //TODO: not so compatible with other fluid systems. itl do i guess
      TileEntityCableBase cable = (TileEntityCableBase) tileTarget;
      if (cable.isItemPipe()) {
        cable.updateIncomingItemFace(themFacingMe);
      }
    }
  }

  private void moveFluid(EnumFacing myFacingDir) {
    EnumFacing themFacingMe = myFacingDir.getOpposite();
    if (tank.getFluidAmount() <= 0) {
      return;
    }
    int toFlow = TRANSFER_FLUID_PER_TICK;
    if (hasAnyIncomingFluidFaces() && toFlow >= tank.getFluidAmount()) {
      toFlow = tank.getFluidAmount();//NOPE// - 1;//keep at least 1 unit in the tank if flow is moving
    }
    BlockPos posTarget = pos.offset(myFacingDir);
    boolean outputSuccess = UtilFluid.tryFillPositionFromTank(world, posTarget, themFacingMe, tank, toFlow);
    if (outputSuccess) {
      TileEntity tileTarget = world.getTileEntity(posTarget);
      if (tileTarget instanceof TileEntityCableBase) {
        //TODO: not so compatible with other fluid systems. itl do i guess
        TileEntityCableBase cable = (TileEntityCableBase) tileTarget;
        if (cable.isFluidPipe()) {
          cable.updateIncomingFluidFace(themFacingMe);
        }
      }
    }
  }

  private void moveEnergy(EnumFacing myFacingDir) {
    IEnergyStorage handlerHere = this.getCapability(CapabilityEnergy.ENERGY, myFacingDir);
    if (handlerHere.getEnergyStored() == 0) {
      return;
    }
    EnumFacing themFacingMe = myFacingDir.getOpposite();
    BlockPos posTarget = pos.offset(myFacingDir);
    TileEntity tileTarget = world.getTileEntity(posTarget);
    if (tileTarget == null || tileTarget.hasCapability(CapabilityEnergy.ENERGY, themFacingMe) == false) {
      return;
    }
    IEnergyStorage handlerOutput = tileTarget.getCapability(CapabilityEnergy.ENERGY, themFacingMe);
    if (handlerHere != null && handlerOutput != null
        && handlerHere.canExtract() && handlerOutput.canReceive()) {
      //first simulate
      int drain = handlerHere.extractEnergy(TRANSFER_ENERGY_PER_TICK, true);
      if (drain > 0) {
        //now push it into output, but find out what was ACTUALLY taken
        int filled = handlerOutput.receiveEnergy(drain, false);
        //now actually drain that much from here
        handlerHere.extractEnergy(filled, false);
        if (filled > 0 && tileTarget instanceof TileEntityCableBase) {
          //TODO: not so compatible with other fluid systems. itl do i guess
          TileEntityCableBase cable = (TileEntityCableBase) tileTarget;
          if (cable.isEnergyPipe()) {
            cable.updateIncomingEnergyFace(themFacingMe);
          }
        }
      }
    }
  }

  public boolean hasAnyIncomingFluidFaces() {
    for (EnumFacing f : EnumFacing.values()) {
      if (mapIncomingFluid.get(f) > 0)
        return true;
    }
    return false;
  }

  public boolean hasAnyIncomingEnergyFaces() {
    for (EnumFacing f : EnumFacing.values()) {
      if (mapIncomingEnergy.get(f) > 0)
        return true;
    }
    return false;
  }

  public void tickDownIncomingFluidFaces() {
    for (EnumFacing f : EnumFacing.values()) {
      if (mapIncomingFluid.get(f) > 0)
        mapIncomingFluid.put(f, mapIncomingFluid.get(f) - 1);
    }
  }

  public void tickDownIncomingItemFaces() {
    for (EnumFacing f : EnumFacing.values()) {
      if (mapIncomingItems.get(f) > 0)
        mapIncomingItems.put(f, mapIncomingItems.get(f) - 1);
    }
  }

  public void tickDownIncomingPowerFaces() {
    for (EnumFacing f : EnumFacing.values()) {
      if (mapIncomingEnergy.get(f) > 0)
        mapIncomingEnergy.put(f, mapIncomingEnergy.get(f) - 1);
    }
  }

  @Override
  public AxisAlignedBB getRenderBoundingBox() {
    double renderExtention = 1.0d;
    AxisAlignedBB bb = new AxisAlignedBB(pos.getX() - renderExtention, pos.getY() - renderExtention, pos.getZ() - renderExtention,
        pos.getX() + 1 + renderExtention, pos.getY() + 1 + renderExtention, pos.getZ() + 1 + renderExtention);
    return bb;
  }

  @Override
  public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
    if (mapBlacklist != null && facing != null && mapBlacklist.get(facing)) {
      return false;//announce that capability does not exist on this side. items and all.
    }
    if (capability == CapabilityEnergy.ENERGY) {
      return this.isEnergyPipe();
    }
    if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
      return this.isFluidPipe();
    }
    return super.hasCapability(capability, facing);
  }

  @Override
  public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @javax.annotation.Nullable net.minecraft.util.EnumFacing facing) {
    if (this.isEnergyPipe() && capability == CapabilityEnergy.ENERGY) {
      return CapabilityEnergy.ENERGY.cast(this.energyStorage);
    }
    else if (facing == null) {
      return super.getCapability(capability, null);
    }
    else
      return super.getCapability(capability, facing.getOpposite());
  }

  @Override
  public boolean getBlacklist(final EnumFacing side) {
    return mapBlacklist.get(side);
  }

  @Override
  public void toggleBlacklist(final EnumFacing side) {
    mapBlacklist.put(side, !mapBlacklist.get(side));
  }
}

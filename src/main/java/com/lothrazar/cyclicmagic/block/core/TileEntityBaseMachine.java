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
package com.lothrazar.cyclicmagic.block.core;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class TileEntityBaseMachine extends TileEntity {

  public boolean isPowered() {
    return this.getWorld().isBlockPowered(this.getPos());
  }

  /**
   * thanks to darkhax
   * 
   * @return
   */
  public boolean isValid() {
    return world != null && !this.isInvalid() && this.getWorld().isBlockLoaded(this.getPos());
  }

  public IBlockState getState() {
    if (world == null || pos == null) {
      return null;
    }
    return world.getBlockState(pos);
  }

  @Override
  public void markDirty() {
    IBlockState st = this.getState();
    world.notifyBlockUpdate(pos, st, st, 3);
    super.markDirty();
  }

  public boolean isRunning() {
    if (!this.isValid()) {
      return false;
    }
    //if im not powered. , then run if onlyRunIfPowered == false 
    return this.isPowered() || !this.onlyRunIfPowered();
  }

  public boolean onlyRunIfPowered() {
    return false;//default is no, dont only run if powered, just go
  }

  public EnumFacing getCurrentFacing() {
    if (this.getBlockType() instanceof BlockBaseFacingOmni) {
      return BlockBaseFacingOmni.getCurrentFacing(world, pos);
    }
    if (!(this.getBlockType() instanceof BlockBaseFacing)) {
      return EnumFacing.UP;
    }
    BlockBaseFacing b = ((BlockBaseFacing) this.getBlockType());
    EnumFacing facing;
    if (b == null || this.getWorld().getBlockState(this.getPos()) == null || b.getFacingFromState(this.getWorld().getBlockState(this.getPos())) == null)
      facing = EnumFacing.UP;
    else
      facing = b.getFacingFromState(this.getWorld().getBlockState(this.getPos()));
    return facing;
  }

  protected BlockPos getCurrentFacingPos() {
    return this.getPos().offset(this.getCurrentFacing());
  }

  /**
   * Block the data being lost when block stat e changes THANKS TO http://www.minecraftforge.net/forum/index.php?topic=29544.0
   */
  @Override
  public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
    return (oldState.getBlock() != newSate.getBlock());// : oldState != newSate;
  }

  @Override
  public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
    // Extracts data from a packet (S35PacketUpdateTileEntity) that was sent
    // from the server. Called on client only.
    this.readFromNBT(pkt.getNbtCompound());
    super.onDataPacket(net, pkt);
  }

  @Override
  public SPacketUpdateTileEntity getUpdatePacket() {//getDescriptionPacket() {
    // Gathers data into a packet (S35PacketUpdateTileEntity) that is to be
    // sent to the client. Called on server only.
    NBTTagCompound syncData = getUpdateTag();
    // ModCyclic.logger.error(this.getBlockType() + " NBT " + syncData);
    return new SPacketUpdateTileEntity(this.pos, 1, syncData);
  }

  @Override
  public NBTTagCompound getUpdateTag() {
    //thanks http://www.minecraftforge.net/forum/index.php?topic=39162.0
    NBTTagCompound syncData = new NBTTagCompound();
    this.writeToNBT(syncData);//this calls writeInternal
    return syncData;
  }

  public int getDimension() {
    if (this.world == null || this.world.provider == null) {
      return 0;
    }
    return this.world.provider.getDimension();
  }
}

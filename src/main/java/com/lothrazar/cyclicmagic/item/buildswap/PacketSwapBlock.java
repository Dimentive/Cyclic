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
package com.lothrazar.cyclicmagic.item.buildswap;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.lothrazar.cyclicmagic.ModCyclic;
import com.lothrazar.cyclicmagic.item.buildswap.ItemBuildSwapper.ActionType;
import com.lothrazar.cyclicmagic.item.buildswap.ItemBuildSwapper.WandType;
import com.lothrazar.cyclicmagic.util.UtilItemStack;
import com.lothrazar.cyclicmagic.util.UtilPlaceBlocks;
import com.lothrazar.cyclicmagic.util.UtilPlayer;
import com.lothrazar.cyclicmagic.util.UtilWorld;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSwapBlock implements IMessage, IMessageHandler<PacketSwapBlock, IMessage> {

  private BlockPos pos;
  private ItemBuildSwapper.ActionType actionType;
  private ItemBuildSwapper.WandType wandType;
  private EnumFacing side;
  private EnumHand hand;

  public PacketSwapBlock() {}

  public PacketSwapBlock(BlockPos mouseover, EnumFacing s,
      ItemBuildSwapper.ActionType t, ItemBuildSwapper.WandType w, EnumHand hand) {
    pos = mouseover;
    actionType = t;
    wandType = w;
    side = s;
    this.hand = hand;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    NBTTagCompound tags = ByteBufUtils.readTag(buf);
    int x = tags.getInteger("x");
    int y = tags.getInteger("y");
    int z = tags.getInteger("z");
    pos = new BlockPos(x, y, z);
    int s = tags.getInteger("s");
    side = EnumFacing.values()[s];
    int t = tags.getInteger("t");
    actionType = ItemBuildSwapper.ActionType.values()[t];
    int w = tags.getInteger("w");
    wandType = ItemBuildSwapper.WandType.values()[w];
    int hnd = tags.getInteger("hand");
    hand = EnumHand.values()[hnd];
  }

  @Override
  public void toBytes(ByteBuf buf) {
    NBTTagCompound tags = new NBTTagCompound();
    tags.setInteger("x", pos.getX());
    tags.setInteger("y", pos.getY());
    tags.setInteger("z", pos.getZ());
    tags.setInteger("t", actionType.ordinal());
    tags.setInteger("w", wandType.ordinal());
    tags.setInteger("s", side.ordinal());
    tags.setInteger("hand", hand.ordinal());
    ByteBufUtils.writeTag(buf, tags);
  }

  @Override
  public IMessage onMessage(final PacketSwapBlock message, final MessageContext ctx) {
    if (ctx.side.isServer() && message.pos != null) {
      ModCyclic.proxy.getThreadFromContext(ctx).addScheduledTask(new Runnable() {

        @Override
        public void run() {
          handle(message, ctx);
        }
      });
    }
    return null;
  }

  private void handle(PacketSwapBlock message, MessageContext ctx) {
    EntityPlayer player = ctx.getServerHandler().player;
    World world = player.getEntityWorld();
    //we already have center, now go around
    //      message.pos.offset(message.side.rotateAround(axis))
    IBlockState replacedBlockState;
    IBlockState newToPlace;
    IBlockState matched = null;
    if (message.wandType == WandType.MATCH) {
      matched = world.getBlockState(message.pos);
    }
    List<BlockPos> places = getSelectedBlocks(world, message.pos, message.actionType, message.wandType, message.side, matched);
    Map<BlockPos, Integer> processed = new HashMap<BlockPos, Integer>();
    // maybe dont randomly take blocks from inventory. maybe do a pick block.. or an inventory..i dont know
    //seems ok, and also different enough to be fine
    BlockPos curPos;
    try {
      synchronized (places) {
        for (Iterator<BlockPos> i = places.iterator(); i.hasNext();) {
          curPos = i.next();
          if (processed.containsKey(curPos) == false) {
            processed.put(curPos, 0);
          }
          if (processed.get(curPos) > 0) {
            continue; //dont process the same location more than once per click
          }
          processed.put(curPos, processed.get(curPos) + 1);// ++
          int slot = UtilPlayer.getFirstSlotWithBlock(player);
          if (slot < 0) {
            continue;//you have no materials left
          }
          if (world.getTileEntity(curPos) != null) {
            continue;//ignore tile entities IE do not break chests / etc
          }
          replacedBlockState = world.getBlockState(curPos);
          Block replacedBlock = replacedBlockState.getBlock();
          if (world.isAirBlock(curPos) || replacedBlockState == null) {
            continue;
          }
          //TODO: CLEANUP/REFACTOR THIS
          String itemName = UtilItemStack.getStringForBlock(replacedBlock);
          boolean isInBlacklist = false;
          for (String s : ItemBuildSwapper.swapBlacklist) {//dont use .contains on the list. must use .equals on string
            if (s != null && s.equals(itemName)) {
              isInBlacklist = true;
              break;
            }
          }
          if (isInBlacklist) {
            continue;
          }
          if (UtilItemStack.getBlockHardness(replacedBlockState, world, curPos) < 0) {
            continue;//since we know -1 is unbreakable
          }
          newToPlace = UtilPlayer.getBlockstateFromSlot(player, slot);
          //wait, do they match? are they the same? do not replace myself
          if (UtilWorld.doBlockStatesMatch(replacedBlockState, newToPlace)) {
            continue;
          }
          //break it and drop the whatever
          //the destroy then set was causing exceptions, changed to setAir // https://github.com/PrinceOfAmber/Cyclic/issues/114
          ItemStack stackBuildWith = player.inventory.getStackInSlot(slot);
          if (stackBuildWith.isEmpty() || stackBuildWith.getCount() <= 0) {
            continue;
          }
          world.setBlockToAir(curPos);
          boolean success = false;
          ItemStack itemStackHeld = player.getHeldItem(message.hand);
          //TODO: maybe toggle between
          //place item block gets slabs in top instead of bottom. but tries to do facing stairs
          // success = UtilPlaceBlocks.placeItemblock(world, curPos, stackBuildWith, player);
          if (!success) {
            success = UtilPlaceBlocks.placeStateSafe(world, player, curPos, newToPlace);
          }
          if (success) {
            UtilPlayer.decrStackSize(player, slot);
          }
          if (success) {
            world.playEvent(2001, curPos, Block.getStateId(replacedBlockState));
            //always break with PLAYER CONTEXT in mind
            replacedBlock.harvestBlock(world, player, curPos, replacedBlockState, null, itemStackHeld);
            ItemStack held = player.getHeldItem(message.hand);
            if (!held.isEmpty() && held.getItem() instanceof ItemBuildSwapper) {
              UtilItemStack.damageItem(player, held);
            }
          }
        } // close off the for loop   
      }
    }
    catch (ConcurrentModificationException e) {
      //possible reason why i cant do a trycatch // http://stackoverflow.com/questions/18752320/trycatch-concurrentmodificationexception-catching-30-of-the-time
      ModCyclic.logger.error("ConcurrentModificationException");
      ModCyclic.logger.error(e.getMessage());// message is null??
      ModCyclic.logger.error(e.getStackTrace().toString());
    }
  }

  public static List<BlockPos> getSelectedBlocks(World world, BlockPos pos, ActionType actionType, WandType wandType, EnumFacing side, IBlockState matched) {
    List<BlockPos> places = new ArrayList<BlockPos>();
    int xMin = pos.getX();
    int yMin = pos.getY();
    int zMin = pos.getZ();
    int xMax = pos.getX();
    int yMax = pos.getY();
    int zMax = pos.getZ();
    boolean isVertical = (side == EnumFacing.UP || side == EnumFacing.DOWN);
    int offsetH = 0;
    int offsetW = 0;
    switch (actionType) {
      case SINGLE:
        places.add(pos);
        offsetW = offsetH = 0;
      break;
      case X3:
        offsetW = offsetH = 1;
      break;
      case X5:
        offsetW = offsetH = 2;
      break;
      case X7:
        offsetW = offsetH = 3;
      break;
      case X9:
        offsetW = offsetH = 4;
      break;
      case X19:
        offsetH = 0;
        offsetW = 4;
      break;
      case X91:
        offsetH = 4;
        offsetW = 0;
      break;
      default:
      break;
    }
    if (actionType != ActionType.SINGLE) {
      if (isVertical) {
        //then we just go in all horizontal directions
        xMin -= offsetH;
        xMax += offsetH;
        zMin -= offsetW;
        zMax += offsetW;
      }
      //we hit a horizontal side
      else if (side == EnumFacing.EAST || side == EnumFacing.WEST) {
        //          WEST(4, 5, 1, "west", EnumFacing.AxisDirection.NEGATIVE, EnumFacing.Axis.X, new Vec3i(-1, 0, 0)),
        //          EAST(5, 4, 3, "east", EnumFacing.AxisDirection.POSITIVE, EnumFacing.Axis.X, new Vec3i(1, 0, 0));
        //now we go in a vertical plane
        zMin -= offsetH;
        zMax += offsetH;
        yMin -= offsetW;
        yMax += offsetW;
      }
      else {
        //axis hit was north/south, so we go in YZ
        xMin -= offsetH;
        xMax += offsetH;
        yMin -= offsetW;
        yMax += offsetW;
      }
      places = UtilWorld.getPositionsInRange(pos, xMin, xMax, yMin, yMax, zMin, zMax);
    }
    List<BlockPos> retPlaces = new ArrayList<BlockPos>();
    for (BlockPos p : places) {
      if (world.isAirBlock(p)) {
        continue;
      }
      if (wandType == WandType.MATCH && matched != null &&
          !UtilWorld.doBlockStatesMatch(matched, world.getBlockState(p))) {
        //we have saved the one we clicked on so only that gets replaced
        continue;
      }
      retPlaces.add(p);
    }
    return retPlaces;
  }
}

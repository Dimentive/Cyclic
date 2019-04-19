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
package com.lothrazar.cyclicmagic.playerupgrade.spell;

import javax.annotation.Nullable;
import com.lothrazar.cyclicmagic.ModCyclic;
import com.lothrazar.cyclicmagic.item.cyclicwand.InventoryWand;
import com.lothrazar.cyclicmagic.item.cyclicwand.ItemCyclicWand;
import com.lothrazar.cyclicmagic.item.cyclicwand.PacketSpellFromServer;
import com.lothrazar.cyclicmagic.util.UtilChat;
import com.lothrazar.cyclicmagic.util.UtilPlaceBlocks;
import com.lothrazar.cyclicmagic.util.UtilSound;
import com.lothrazar.cyclicmagic.util.UtilSpellCaster;
import com.lothrazar.cyclicmagic.util.UtilWorld;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpellRangeBuild extends BaseSpellRange implements ISpellFromServer {

  final static int max = 32;// max search range
  private PlaceType type;

  public static enum PlaceType {
    PLACE, UP, DOWN, LEFT, RIGHT;
  }

  public SpellRangeBuild(int id, String n, PlaceType t) {
    super.init(id, n);
    this.type = t;
  }

  @Override
  public boolean cast(World world, EntityPlayer p, ItemStack wand, BlockPos pos, EnumFacing side) {
    if (world.isRemote) {
      // only client side can call this method. mouseover does not exist on server
      BlockPos mouseover = ModCyclic.proxy.getBlockMouseoverExact(maxRange);
      BlockPos offset = ModCyclic.proxy.getBlockMouseoverOffset(maxRange);
      EnumFacing sideMouseover = ModCyclic.proxy.getSideMouseover(maxRange);
      if (mouseover != null && offset != null) {
        ModCyclic.network.sendToServer(new PacketSpellFromServer(mouseover, offset, sideMouseover, this.getID()));
      }
      ItemStack heldWand = UtilSpellCaster.getPlayerWandIfHeld(p);
      if (!heldWand.isEmpty()) {
        int itemSlot = ItemCyclicWand.BuildType.getSlot(heldWand);
        IBlockState state = InventoryWand.getToPlaceFromSlot(heldWand, itemSlot);
        if (state != null && state.getBlock() != null && offset != null) {
          UtilSound.playSoundPlaceBlock(world, offset, state.getBlock());
        }
      }
    }
    return true;
  }

  @Override
  public void castFromServer(BlockPos posMouseover, BlockPos posOffset, @Nullable EnumFacing sideMouseover, EntityPlayer p) {
    World world = p.getEntityWorld();
    ItemStack heldWand = UtilSpellCaster.getPlayerWandIfHeld(p);
    if (heldWand.isEmpty()) {
      return;
    }
    int itemSlot = ItemCyclicWand.BuildType.getSlot(heldWand);
    IBlockState state = InventoryWand.getToPlaceFromSlot(heldWand, itemSlot);
    if (state == null || state.getBlock() == null) {
      //one last chance to update slot, in case something happened
      ItemCyclicWand.BuildType.setNextSlot(heldWand);
      itemSlot = ItemCyclicWand.BuildType.getSlot(heldWand);
      state = InventoryWand.getToPlaceFromSlot(heldWand, itemSlot);
      if (state == null || state.getBlock() == null) {
        UtilChat.sendStatusMessage(p, "wand.inventory.empty");
        return;
      }
    }
    BlockPos posToPlaceAt = null;
    EnumFacing facing = null;
    EnumFacing playerFacing = p.getHorizontalFacing();
    switch (type) {
      case DOWN:
        facing = EnumFacing.DOWN;
      break;
      case UP:
        facing = EnumFacing.UP;
      break;
      case LEFT:
        switch (playerFacing) {
          case DOWN:
          break;
          case EAST:
            facing = EnumFacing.NORTH;
          break;
          case NORTH:
            facing = EnumFacing.WEST;
          break;
          case SOUTH:
            facing = EnumFacing.EAST;
          break;
          case UP:
          break;
          case WEST:
            facing = EnumFacing.SOUTH;
          break;
          default:
          break;
        }
      break;
      case RIGHT:
        switch (playerFacing) {
          case DOWN:
          break;
          case EAST:
            facing = EnumFacing.SOUTH;
          break;
          case NORTH:
            facing = EnumFacing.EAST;
          break;
          case SOUTH:
            facing = EnumFacing.WEST;
          break;
          case UP:
          break;
          case WEST:
            facing = EnumFacing.NORTH;
          break;
          default:
          break;
        }
      break;
      case PLACE:
      break;
      default:
      break;
    }
    if (facing == null) {
      posToPlaceAt = posOffset;
    }
    else {
      posToPlaceAt = UtilWorld.nextAirInDirection(world, posMouseover, facing, max, null);
      //      BlockPos posLoop = posMouseover;
      //      for (int i = 0; i < max; i++) {
      //        if (world.isAirBlock(posLoop)) {
      //          posToPlaceAt = posLoop;
      //          break;
      //        }
      //        else {
      //          posLoop = posLoop.offset(facing);
      //        }
      //      }
    }
    //    if (UtilPlaceBlocks.placeStateSafe(world, p, posToPlaceAt, state)) {
    //    ItemStack cur = InventoryWand.getFromSlot(heldWand, itemSlot);
    if (sideMouseover == null) {
      sideMouseover = p.getHorizontalFacing();
    }
    //onitemuse is decrementing stack size of player.hand, which is the wand! bad MC
    // && cur.onItemUse(p, world, posToPlaceAt, p.getActiveHand(), sideMouseover, 0.5F, 0.5F, 0.5F) == EnumActionResult.SUCCESS
    if (posToPlaceAt != null && UtilPlaceBlocks.placeStateSafe(world, p, posToPlaceAt, state)) {
      if (p.capabilities.isCreativeMode == false) {
        InventoryWand.decrementSlot(heldWand, itemSlot);
      }
      ItemCyclicWand.BuildType.setNextSlot(heldWand);
      // yes im spawning particles on the server side, but the
      // util handles that
      this.spawnParticle(world, p, posMouseover);
      Block newSpot = null;
      if (world.getBlockState(posToPlaceAt) != null) {
        newSpot = world.getBlockState(posToPlaceAt).getBlock();
        this.playSound(world, p, newSpot, posToPlaceAt);
      }
    }
  }
}

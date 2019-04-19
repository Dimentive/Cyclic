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
package com.lothrazar.cyclicmagic.module.dispenser;

import com.lothrazar.cyclicmagic.util.UtilPlantable;
import com.lothrazar.cyclicmagic.util.UtilWorld;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BehaviorPlantSeed extends BehaviorDefaultDispenseItem {

  @Override
  public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
    World world = source.getWorld();
    // we want to place in front of the dispenser 
    //which is based on where its facing
    //changed in 1.10
    BlockPos posForPlant = UtilWorld.convertIposToBlockpos(BlockDispenser.getDispensePosition(source));
    //source.getBlockPos().offset(BlockDispenser.getFacing(source.getBlockMetadata()));
    ItemStack returning = UtilPlantable.tryPlantSeed(world, posForPlant, stack);
    if (returning == null)
      return super.dispenseStack(source, stack);
    else
      return returning;
  }
}

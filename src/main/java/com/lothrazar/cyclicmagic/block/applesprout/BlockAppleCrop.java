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
package com.lothrazar.cyclicmagic.block.applesprout;

import java.util.List;
import java.util.Random;
import com.lothrazar.cyclicmagic.IContent;
import com.lothrazar.cyclicmagic.data.IHasRecipe;
import com.lothrazar.cyclicmagic.guide.GuideCategory;
import com.lothrazar.cyclicmagic.registry.BlockRegistry;
import com.lothrazar.cyclicmagic.registry.RecipeRegistry;
import com.lothrazar.cyclicmagic.util.Const;
import com.lothrazar.cyclicmagic.util.UtilChat;
import com.lothrazar.cyclicmagic.util.UtilOreDictionary;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockAppleCrop extends BlockCrops implements IHasRecipe, IContent {

  private static int GROWTH_TICKRATE = 500;
  private static final double BONEMEAL_CHANCE = 0.35D;
  private static final AxisAlignedBB[] GROWING_AABB = { new AxisAlignedBB(0.25D, 0.9D, 0.25D, 0.75D, 1.0D, 0.75D), new AxisAlignedBB(0.25D, 0.8D, 0.25D, 0.75D, 1.0D, 0.75D), new AxisAlignedBB(0.25D, 0.7D, 0.25D, 0.75D, 1.0D, 0.75D), new AxisAlignedBB(0.25D, 0.5D, 0.25D, 0.75D, 1.0D, 0.75D), new AxisAlignedBB(0.25D, 0.4D, 0.25D, 0.75D, 1.0D, 0.75D), new AxisAlignedBB(0.25D, 0.3D, 0.25D, 0.75D, 1.0D, 0.75D), new AxisAlignedBB(0.25D, 0.2D, 0.25D, 0.75D, 1.0D, 0.75D), new AxisAlignedBB(0.25D, 0.2D, 0.25D, 0.75D, 1.0D, 0.75D) };

  public BlockAppleCrop() {
    super();
    setLightOpacity(0);
  }

  @Override
  public String getContentName() {
    return "apple";
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack stack, World playerIn, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced) {
    String myTooltip = this.getTranslationKey() + ".tooltip";
    tooltip.add(UtilChat.lang(myTooltip));
  }

  @Override
  public void observedNeighborChange(IBlockState observerState, World world, BlockPos pos, Block changedBlock, BlockPos changedBlockPos) {
    if (canStay(world, pos) == false) {
      world.destroyBlock(pos, true);
    }
    else {
      super.observedNeighborChange(observerState, world, pos, changedBlock, changedBlockPos);
    }
  }

  @Override
  public int tickRate(World world) {
    return GROWTH_TICKRATE;
  }

  @Override
  protected boolean canSustainBush(IBlockState state) {
    return true;//override the looking for farmland thing
  }

  @Override
  public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
    if (canStay(world, pos) == false) {
      return false;
    }
    return this.canPlaceBlockAt(world, pos);
  }

  @Override
  public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
    if (canStay(world, pos) == false) {
      world.destroyBlock(pos, true);
    }
    else {
      grow(world, rand, pos, state);
    }
  }

  @Override
  public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state) {
    return world.rand.nextFloat() < BONEMEAL_CHANCE;
  }

  @Override
  public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
    if (canStay(world, pos) == false) {
      world.destroyBlock(pos, true);
    }
    else if (world.isRemote == false) {
      int age = state.getValue(AGE).intValue();
      if (age < this.getMaxAge()) {
        world.setBlockState(pos, getStateForAge(age + 1), 3);
        world.scheduleUpdate(new BlockPos(pos), this, tickRate(world));
      }
    }
  }

  private IBlockState getStateForAge(int age) {
    return getDefaultState().withProperty(AGE, age);
  }

  @Override
  protected Item getSeed() {
    return Item.getItemFromBlock(this);
  }

  @Override
  protected Item getCrop() {
    return Items.APPLE;
  }

  @Override
  public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess world, BlockPos pos) {
    return NULL_AABB;
  }

  @Override
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
    return GROWING_AABB[state.getValue(AGE).intValue()];
  }

  @Override
  public IRecipe addRecipe() {
    return RecipeRegistry.addShapedRecipe(new ItemStack(this, 2),
        "as",
        "sa",
        'a', new ItemStack(Items.APPLE),
        's', "stickWood");
  }

  private boolean canStay(World world, BlockPos pos) {
    //can only grow/survive if leaves above 
    Block blockAbove = world.getBlockState(pos.up()).getBlock();
    return UtilOreDictionary.doesMatchOreDict(new ItemStack(blockAbove), "treeLeaves");
  }

  @Override
  public void register() {
    BlockRegistry.registerBlock(this, getContentName(), GuideCategory.BLOCK);
  }

  private boolean enabled;

  @Override
  public boolean enabled() {
    return enabled;
  }

  @Override
  public void syncConfig(Configuration config) {
    String category = Const.ConfigCategory.content;
    enabled = config.getBoolean(getContentName(), category, true, Const.ConfigCategory.contentDefaultText);
    GROWTH_TICKRATE = config.getInt("AppleGrowthTicks", Const.ConfigCategory.blocks, 500, 1, 99999, "Ticks for apple sprout to grow, 1 will grow almost instantly");
  }
}

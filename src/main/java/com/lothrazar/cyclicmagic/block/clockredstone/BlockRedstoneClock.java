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
package com.lothrazar.cyclicmagic.block.clockredstone;

import java.util.Random;
import com.lothrazar.cyclicmagic.IContent;
import com.lothrazar.cyclicmagic.ModCyclic;
import com.lothrazar.cyclicmagic.block.core.BlockBaseHasTile;
import com.lothrazar.cyclicmagic.data.IHasRecipe;
import com.lothrazar.cyclicmagic.gui.ForgeGuiHandler;
import com.lothrazar.cyclicmagic.guide.GuideCategory;
import com.lothrazar.cyclicmagic.registry.BlockRegistry;
import com.lothrazar.cyclicmagic.registry.RecipeRegistry;
import com.lothrazar.cyclicmagic.util.Const;
import com.lothrazar.cyclicmagic.util.UtilParticle;
import net.minecraft.block.BlockLever;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockRedstoneClock extends BlockBaseHasTile implements IHasRecipe, IContent {

  private static final int PARTICLE_DENSITY = 2;
  public static final PropertyBool POWERED = BlockLever.POWERED;
  private static final PropertyBool U = PropertyBool.create("u");
  private static final PropertyBool D = PropertyBool.create("d");
  private static final PropertyBool N = PropertyBool.create("n");
  private static final PropertyBool E = PropertyBool.create("e");
  private static final PropertyBool S = PropertyBool.create("s");
  private static final PropertyBool W = PropertyBool.create("w");

  public BlockRedstoneClock() {
    super(Material.IRON);
    this.setGuiId(ForgeGuiHandler.GUI_INDEX_CLOCK);
  }

  @Override
  public void register() {
    BlockRegistry.registerBlock(this, getContentName(), GuideCategory.BLOCK);
    BlockRegistry.registerTileEntity(TileEntityClock.class, getContentName() + "_te");
  }

  @Override
  public String getContentName() {
    return "clock";
  }

  private boolean enabled;

  @Override
  public boolean enabled() {
    return enabled;
  }

  @Override
  public void syncConfig(Configuration config) {
    enabled = config.getBoolean("Clock", Const.ConfigCategory.content, true, Const.ConfigCategory.contentDefaultText);
  }

  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { POWERED, U, D, N, E, S, W });
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    return this.getDefaultState().withProperty(POWERED, false);
  }

  @Override
  public boolean canProvidePower(IBlockState state) {
    return true;
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return (state.getValue(POWERED) ? 1 : 0);
  }

  @Override
  public TileEntity createTileEntity(World worldIn, IBlockState state) {
    return new TileEntityClock();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
    if (stateIn.getValue(POWERED)) {
      UtilParticle.spawnParticle(worldIn, EnumParticleTypes.REDSTONE, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, PARTICLE_DENSITY);
    }
  }

  private int getPower(IBlockAccess world, BlockPos pos, EnumFacing side) {
    if (world.getTileEntity(pos) instanceof TileEntityClock) {
      TileEntityClock clock = ((TileEntityClock) world.getTileEntity(pos));
      return clock.getPowerForSide(side);
    }
    return 0;
  }

  @Override
  public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
    return blockState.getValue(POWERED) ? getPower(blockAccess, pos, side.getOpposite()) : 0;
  }

  @Override
  public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
    return blockState.getValue(POWERED) ? getPower(blockAccess, pos, side.getOpposite()) : 0;
  }

  @SuppressWarnings("deprecation")
  @Override
  public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
    TileEntity tile = world.getTileEntity(pos);
    if (tile instanceof TileEntityClock) {
      TileEntityClock clock = (TileEntityClock) tile;
      try {
        boolean powered = state.getValue(POWERED);
        state = state.withProperty(U, clock.getSideHasPower(EnumFacing.UP) && powered);
        state = state.withProperty(D, clock.getSideHasPower(EnumFacing.DOWN) && powered);
        state = state.withProperty(N, clock.getSideHasPower(EnumFacing.NORTH) && powered);
        state = state.withProperty(E, clock.getSideHasPower(EnumFacing.EAST) && powered);
        state = state.withProperty(S, clock.getSideHasPower(EnumFacing.SOUTH) && powered);
        state = state.withProperty(W, clock.getSideHasPower(EnumFacing.WEST) && powered);
      }
      catch (Exception e) {
        ModCyclic.logger.error("fail clock getActualState", e);
      }
      // 
    }
    return super.getActualState(state, world, pos);
  }

  @Override
  public IRecipe addRecipe() {
    return RecipeRegistry.addShapedOreRecipe(new ItemStack(this),
        "iri",
        "rqr",
        "iri",
        'i', "ingotIron",
        'r', "dustRedstone",
        'q', Items.REPEATER);
  }
}

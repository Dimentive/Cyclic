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
package com.lothrazar.cyclicmagic.block.tank;

import java.util.ArrayList;
import java.util.List;
import com.lothrazar.cyclicmagic.IContent;
import com.lothrazar.cyclicmagic.block.core.BlockBase;
import com.lothrazar.cyclicmagic.block.core.IBlockHasTESR;
import com.lothrazar.cyclicmagic.data.IHasRecipe;
import com.lothrazar.cyclicmagic.guide.GuideCategory;
import com.lothrazar.cyclicmagic.guide.GuideRegistry;
import com.lothrazar.cyclicmagic.registry.BlockRegistry;
import com.lothrazar.cyclicmagic.registry.RecipeRegistry;
import com.lothrazar.cyclicmagic.util.Const;
import com.lothrazar.cyclicmagic.util.UtilChat;
import com.lothrazar.cyclicmagic.util.UtilNBT;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFluidTank extends BlockBase implements ITileEntityProvider, IHasRecipe, IBlockHasTESR, IContent {

  public static final PropertyBool TANK_ABOVE = PropertyBool.create("above");
  public static final PropertyBool TANK_BELOW = PropertyBool.create("below");
  public static final int heightCheckMax = 16;

  public BlockFluidTank() {
    super(Material.GLASS);
    this.setHardness(7F);
    this.setResistance(7F);
    this.setSoundType(SoundType.GLASS);
    this.setHarvestLevel("pickaxe", 1);
    this.setTranslucent();
  }

  @Override
  public void register() {
    BlockRegistry.registerBlock(this, new ItemBlockFluidTank(this), "block_storeempty", null);
    BlockRegistry.registerTileEntity(TileEntityFluidTank.class, "bucketstorage");
    GuideRegistry.register(GuideCategory.BLOCK, this, null, null);
  }

  private boolean enabled;

  @Override
  public boolean enabled() {
    return enabled;
  }

  @Override
  public String getContentName() {
    return "block_storeempty";
  }

  @Override
  public void syncConfig(Configuration config) {
    enabled = config.getBoolean("BucketBlocks", Const.ConfigCategory.content, true, getContentName() + ", the Fluid Tank. "
        + Const.ConfigCategory.contentDefaultText);
  }

  @SuppressWarnings("deprecation")
  @Override
  @SideOnly(Side.CLIENT)
  public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
    boolean tileAbove = world.getTileEntity(pos.up()) instanceof TileEntityFluidTank;
    boolean tileBelow = world.getTileEntity(pos.down()) instanceof TileEntityFluidTank;
    return super.getActualState(state, world, pos)
        .withProperty(TANK_ABOVE, tileAbove)
        .withProperty(TANK_BELOW, tileBelow);
  }

  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { TANK_ABOVE, TANK_BELOW });
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return 0;
  }

  @Override
  public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
    //?? TE null? http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/modification-development/2677315-solved-tileentity-returning-null
    //http://www.minecraftforge.net/forum/index.php?/topic/38048-19-solved-blockgetdrops-and-tileentity/
    List<ItemStack> ret = new ArrayList<ItemStack>();
    Item item = Item.getItemFromBlock(this);//this.getItemDropped(state, rand, fortune);
    TileEntity ent = world.getTileEntity(pos);
    ItemStack stack = new ItemStack(item);
    if (ent != null && ent instanceof TileEntityFluidTank) {
      TileEntityFluidTank te = (TileEntityFluidTank) ent;
      FluidStack fs = te.getCurrentFluidStack();
      if (fs != null) {
        UtilNBT.setItemStackNBTVal(stack, NBT_FLUIDSIZE, fs.amount);
        String resourceStr = FluidRegistry.getFluidName(fs.getFluid());
        UtilNBT.setItemStackNBTVal(stack, NBT_FLUIDTYPE, resourceStr);
      }
    }
    ret.add(stack);
    return ret;
  }

  @Override
  public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
    FluidStack fluid = ItemBlockFluidTank.copyFluidFromStack(stack);
    if (fluid != null) {
      TileEntityFluidTank container = (TileEntityFluidTank) worldIn.getTileEntity(pos);
      container.fill(fluid, true);
    }
  }

  @SideOnly(Side.CLIENT)
  @Override
  public BlockRenderLayer getRenderLayer() {
    return BlockRenderLayer.TRANSLUCENT; // http://www.minecraftforge.net/forum/index.php?topic=18754.0
  }

  @Override
  public boolean isOpaqueCube(IBlockState state) { // http://greyminecraftcoder.blogspot.ca/2014/12/transparent-blocks-18.html
    return false;
  }

  @Override
  public TileEntity createNewTileEntity(World worldIn, int meta) {
    return new TileEntityFluidTank();
  }

  //start of 'fixing getDrops to not have null tile entity', using pattern from forge BlockFlowerPot patch
  @Override
  public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
    if (willHarvest) {
      return true;
    } //If it will harvest, delay deletion of the block until after getDrops
    return super.removedByPlayer(state, world, pos, player, willHarvest);
  }

  @Override
  public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack tool) {
    super.harvestBlock(world, player, pos, state, te, tool);
    world.setBlockToAir(pos);
  }

  //end of fixing getdrops
  @Override
  public IRecipe addRecipe() {
    return RecipeRegistry.addShapedRecipe(new ItemStack(this),
        "igi",
        "gog",
        "igi",
        'o', "obsidian", 'i', "ingotIron", 'g', "blockGlass");
  }

  @Override
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
    if (player.getHeldItem(hand).getItem() == Item.getItemFromBlock(this)) {
      return false;
    }
    // check the TE
    boolean success = FluidUtil.interactWithFluidHandler(player, hand, world, pos, side);
    int heightCheck = 0;
    BlockPos posLoop = new BlockPos(pos);
    //connected tanks: try to move  up again
    while (!success && heightCheck < heightCheckMax) {
      heightCheck++;
      posLoop = posLoop.up();
      success = FluidUtil.interactWithFluidHandler(player, hand, world, posLoop, side);
    }
    if (world.isRemote == false) { //server side
      TileEntityFluidTank te = (TileEntityFluidTank) world.getTileEntity(pos);
      if (te != null) {
        FluidStack fs = te.getCurrentFluidStack();
        if (fs != null) {
          String amtStr = fs.amount + " / " + te.getCapacity() + " ";
          UtilChat.sendStatusMessage(player, UtilChat.lang("cyclic.fluid.amount") + amtStr + fs.getLocalizedName());
        }
        else {
          UtilChat.sendStatusMessage(player, UtilChat.lang("cyclic.fluid.empty"));
        }
      }
    }
    // otherwise return true if it is a fluid handler to prevent in world placement
    return success || FluidUtil.getFluidHandler(player.getHeldItem(hand)) != null || super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void initModel() {
    ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFluidTank.class, new FluidTESR());
  }
}

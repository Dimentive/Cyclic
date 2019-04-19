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

import com.lothrazar.cyclicmagic.IContent;
import com.lothrazar.cyclicmagic.block.core.BlockBaseHasTile;
import com.lothrazar.cyclicmagic.block.core.IBlockHasTESR;
import com.lothrazar.cyclicmagic.data.IHasRecipe;
import com.lothrazar.cyclicmagic.gui.ForgeGuiHandler;
import com.lothrazar.cyclicmagic.guide.GuideCategory;
import com.lothrazar.cyclicmagic.registry.BlockRegistry;
import com.lothrazar.cyclicmagic.registry.RecipeRegistry;
import com.lothrazar.cyclicmagic.util.Const;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockSolidifier extends BlockBaseHasTile implements IContent, IHasRecipe, IBlockHasTESR {

  public BlockSolidifier() {
    super(Material.IRON);
    this.setHardness(3.0F).setResistance(5.0F);
    this.setGuiId(ForgeGuiHandler.GUI_INDEX_SOLIDIFIER);
    this.setTranslucent();
  }

  @Override
  public TileEntity createTileEntity(World worldIn, IBlockState state) {
    return new TileSolidifier();
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void initModel() {
    ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    ClientRegistry.bindTileEntitySpecialRenderer(TileSolidifier.class, new FluidSolidifierTESR());
  }

  @Override
  public IRecipe addRecipe() {
    RecipeSolidifier.initAllRecipes();
    return RecipeRegistry.addShapedRecipe(new ItemStack(this),
        " s ",
        "lgl",
        "ooo",
        'l', Blocks.WATERLILY,
        'o', "dustGlowstone",
        'g', "blockIron",
        's', Blocks.OBSERVER);
  }

  @Override
  public int getComparatorInputOverride(IBlockState blockState, World world, BlockPos pos) {
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof TileSolidifier) {
      float fill = ((TileSolidifier) te).getFillRatio();
      return (int) (15 * fill);
    }
    return 0;
  }

  @Override
  public String getContentName() {
    return "solidifier";
  }

  @Override
  public void register() {
    BlockRegistry.registerBlock(this, getContentName(), GuideCategory.BLOCKMACHINE);
    GameRegistry.registerTileEntity(TileSolidifier.class, getContentName() + "_te");
  }

  private boolean enabled;

  @Override
  public boolean enabled() {
    return enabled;
  }

  @Override
  public void syncConfig(Configuration config) {
    enabled = config.getBoolean(getContentName(), Const.ConfigCategory.content, true, Const.ConfigCategory.contentDefaultText);
  }
}

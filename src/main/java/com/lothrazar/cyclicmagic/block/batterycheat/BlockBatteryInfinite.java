package com.lothrazar.cyclicmagic.block.batterycheat;

import com.lothrazar.cyclicmagic.IContent;
import com.lothrazar.cyclicmagic.block.core.BlockBaseHasTile;
import com.lothrazar.cyclicmagic.data.IHasRecipe;
import com.lothrazar.cyclicmagic.guide.GuideCategory;
import com.lothrazar.cyclicmagic.registry.BlockRegistry;
import com.lothrazar.cyclicmagic.registry.RecipeRegistry;
import com.lothrazar.cyclicmagic.util.Const;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

public class BlockBatteryInfinite extends BlockBaseHasTile implements IHasRecipe, IContent {

  public BlockBatteryInfinite() {
    super(Material.ROCK);
  }

  @Override
  public void register() {
    BlockRegistry.registerBlock(this, getContentName(), GuideCategory.BLOCKMACHINE);
    BlockRegistry.registerTileEntity(TileEntityBatteryInfinite.class, Const.MODID + getContentName() + "_te");
  }

  @Override
  public String getContentName() {
    return "battery_infinite";
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

  @Override
  public IRecipe addRecipe() {
    return RecipeRegistry.addShapedOreRecipe(new ItemStack(this),
        "uc",
        "cu",
        'c', new ItemStack(Blocks.COMMAND_BLOCK), 'u', new ItemStack(Blocks.BARRIER));
  }

  @Override
  public TileEntity createTileEntity(World worldIn, IBlockState state) {
    return new TileEntityBatteryInfinite();
  }
}

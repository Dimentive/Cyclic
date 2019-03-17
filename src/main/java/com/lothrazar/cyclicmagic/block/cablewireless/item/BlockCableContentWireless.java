package com.lothrazar.cyclicmagic.block.cablewireless.item;

import com.lothrazar.cyclicmagic.block.core.BlockBaseHasTile;
import com.lothrazar.cyclicmagic.data.IHasRecipe;
import com.lothrazar.cyclicmagic.gui.ForgeGuiHandler;
import com.lothrazar.cyclicmagic.registry.RecipeRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockCableContentWireless extends BlockBaseHasTile implements IHasRecipe {

  public BlockCableContentWireless() {
    super(Material.IRON);
    this.setGuiId(ForgeGuiHandler.GUI_INDEX_W_CONTENT);
    this.setTranslucent();
  }

  @Override
  public TileEntity createTileEntity(World worldIn, IBlockState state) {
    return new TileCableContentWireless();
  }

  @Override
  public IRecipe addRecipe() {
    return RecipeRegistry.addShapedRecipe(new ItemStack(this),
        "iri",
        "cgc",
        "iri",
        'i', Blocks.IRON_BARS,
        'c', Items.COMPARATOR,
        'r', Items.DIAMOND,
        'g', "blockGold");
  }
}

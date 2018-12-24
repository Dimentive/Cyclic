package com.lothrazar.cyclicmagic.compat.jei;

import java.util.ArrayList;
import java.util.List;
import com.lothrazar.cyclicmagic.block.packager.RecipePackager;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

public class PackagerWrapper implements IRecipeWrapper {

  private RecipePackager src;

  public PackagerWrapper(RecipePackager source) {
    this.src = source;
  }

  public ItemStack getOut() {
    return src.getRecipeOutput();
  }

  @Override
  public void getIngredients(IIngredients ingredients) {
    List<ItemStack> ing = new ArrayList<ItemStack>();
    for (ItemStack wtf : src.getInput()) {
      ing.add(wtf.copy());
    }
    ingredients.setInputs(VanillaTypes.ITEM, ing);
    ingredients.setOutput(VanillaTypes.ITEM, src.getRecipeOutput());
  }
  //
  //  public int size() {
  //    int size = 0;
  //    for (ItemStack s : src.getInput()) {
  //      if (s.isEmpty() == false) {
  //        size++;
  //      }
  //    }
  //    return size;
  //  }
}
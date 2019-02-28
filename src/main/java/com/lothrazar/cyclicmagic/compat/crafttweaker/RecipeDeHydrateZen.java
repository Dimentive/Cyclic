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
package com.lothrazar.cyclicmagic.compat.crafttweaker;

import com.lothrazar.cyclicmagic.ModCyclic;
import com.lothrazar.cyclicmagic.block.dehydrator.RecipeDeHydrate;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.cyclicmagic.Dehydrator")
@ZenRegister
public class RecipeDeHydrateZen {

  @Optional.Method(modid = "crafttweaker")
  @ZenMethod
  public static void removeShapedRecipe(IItemStack output) {
    ModCyclic.logger.info("ZenScript: attempt remove dehydrator recipe for " + output.getDisplayName());
    ItemStack out = toStack(output);
    for (RecipeDeHydrate rec : RecipeDeHydrate.recipes) {
      if (rec.getRecipeOutput().isItemEqual(out)) {
        RecipeDeHydrate.recipes.remove(rec);
        ModCyclic.logger.info("ZenScript: removed dehydrator recipe for " + output.getDisplayName());
      }
    }
  }

  @Deprecated
  @Optional.Method(modid = "crafttweaker")
  @ZenMethod
  public static void removeShapelessRecipe(IItemStack output) {
    removeShapedRecipe(output);
  }

  @Optional.Method(modid = "crafttweaker")
  @ZenMethod
  public static void addRecipe(IItemStack output, IItemStack inputs, int ticks) {
    //bak compatibility 
    ModCyclic.logger.info("ZenScript: dehydrator addRecipe: outputs, inputs, ticks, waterCreated=default:  " + output.getDisplayName());
    RecipeDeHydrate.addRecipe(new RecipeDeHydrate(toStack(inputs), toStack(output), ticks, 100));
  }

  @Optional.Method(modid = "crafttweaker")
  @ZenMethod
  public static void addRecipe(IItemStack output, IItemStack inputs, int ticks, int waterCreated) {
    ModCyclic.logger.info("ZenScript: dehydrator addRecipe: outputs, inputs, ticks, waterCreated:  " + output.getDisplayName());
    RecipeDeHydrate.addRecipe(new RecipeDeHydrate(toStack(inputs), toStack(output), ticks, waterCreated));
  }

  /**
   * THANKS TO https://github.com/jaredlll08/MTLib/blob/1.12/src/main/java/com/blamejared/mtlib/helpers/InputHelper.java @ https://github.com/jaredlll08/MTLib which is MIT license
   * https://github.com/jaredlll08/MTLib/blob/1.12/LICENSE.md
   */
  @Optional.Method(modid = "crafttweaker")
  public static ItemStack toStack(IItemStack iStack) {
    if (iStack == null) {
      return ItemStack.EMPTY;
    }
    else {
      Object internal = iStack.getInternal();
      if (!(internal instanceof ItemStack)) {
        return ItemStack.EMPTY;
      }
      return (ItemStack) internal;
    }
  }

  /**
   * THANKS TO https://github.com/jaredlll08/MTLib/blob/1.12/src/main/java/com/blamejared/mtlib/helpers/InputHelper.java @ https://github.com/jaredlll08/MTLib which is MIT license
   * https://github.com/jaredlll08/MTLib/blob/1.12/LICENSE.md
   */
  @Optional.Method(modid = "crafttweaker")
  public static ItemStack[] toStacks(IItemStack[] iStack) {
    if (iStack == null) {
      return null;
    }
    else {
      ItemStack[] output = new ItemStack[iStack.length];
      for (int i = 0; i < iStack.length; i++) {
        output[i] = toStack(iStack[i]);
      }
      return output;
    }
  }
}

package com.lothrazar.cyclicmagic.block.packager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.google.common.collect.Lists;
import com.lothrazar.cyclicmagic.util.Const;
import com.lothrazar.cyclicmagic.util.UtilItemStack;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RecipePackager extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

  public static ArrayList<RecipePackager> recipes = new ArrayList<>();
  private NonNullList<ItemStack> input;
  private ItemStack output = ItemStack.EMPTY;

  public RecipePackager(ItemStack out, ItemStack in) {
    this(out, new ItemStack[] { in });
  }

  public RecipePackager(ItemStack out, ItemStack[] in) {
    if (in.length > 6 || in.length == 0) {
      throw new IllegalArgumentException("Input array must be length 6 or less");
    }
    input = NonNullList.withSize(in.length, ItemStack.EMPTY);
    for (int i = 0; i < in.length; i++) {
      if (in[i] != null && in[i].isEmpty() == false)
        input.set(i, in[i]);
    }
    output = out;
    this.setRegistryName(new ResourceLocation(Const.MODID, "packager" + UUID.randomUUID().toString() + out.getTranslationKey()));
  }

  public NonNullList<ItemStack> getInput() {
    return input;
  }

  @Override
  public boolean matches(InventoryCrafting inv, World worldIn) {
    //    ShapelessRecipes matches has been copied to here
    List<ItemStack> inventory = Lists.newArrayList();
    for (int i = 0; i < inv.getHeight(); ++i) {
      for (int j = 0; j < inv.getWidth(); ++j) {
        ItemStack itemstack = inv.getStackInRowAndColumn(j, i);
        if (!itemstack.isEmpty()) {
          inventory.add(itemstack.copy());
        }
      }
    }
    int matches = 0;
    for (ItemStack invo : inventory) {
      for (ItemStack recipe : this.getInput()) {
        if (UtilItemStack.isItemStackEqualIgnoreCount(recipe, invo)
            && recipe.getCount() <= invo.getCount()) {
          matches++;
          break;
        }
      }
    }
    return matches >= input.size();
  }

  @Override
  public ItemStack getCraftingResult(InventoryCrafting inv) {
    return output.copy();
  }

  @Override
  public boolean canFit(int width, int height) {
    return width == 3 && height == 2;
  }

  @Override
  public ItemStack getRecipeOutput() {
    return output.copy();
  }

  public static void initAllRecipes() {
    addRecipe(new RecipePackager(new ItemStack(Blocks.DIRT, 4, 1),
        new ItemStack[] { new ItemStack(Blocks.DIRT, 2, 0), new ItemStack(Blocks.GRAVEL, 2) }));
    addRecipe(new RecipePackager(new ItemStack(Blocks.IRON_BLOCK), new ItemStack(Items.IRON_INGOT, 9)));
    addRecipe(new RecipePackager(new ItemStack(Items.IRON_INGOT), new ItemStack(Items.IRON_NUGGET, 9)));
    addRecipe(new RecipePackager(new ItemStack(Blocks.GOLD_BLOCK), new ItemStack(Items.GOLD_INGOT, 9)));
    addRecipe(new RecipePackager(new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.GOLD_NUGGET, 9)));
    addRecipe(new RecipePackager(new ItemStack(Blocks.MELON_BLOCK), new ItemStack(Items.MELON, 9)));
    addRecipe(new RecipePackager(new ItemStack(Blocks.COAL_BLOCK), new ItemStack(Items.COAL, 9)));
    addRecipe(new RecipePackager(new ItemStack(Blocks.REDSTONE_BLOCK), new ItemStack(Items.REDSTONE, 9)));
    addRecipe(new RecipePackager(new ItemStack(Blocks.GLOWSTONE), new ItemStack(Items.GLOWSTONE_DUST, 4)));
    addRecipe(new RecipePackager(new ItemStack(Blocks.BRICK_BLOCK), new ItemStack(Items.BRICK, 4)));
    addRecipe(new RecipePackager(new ItemStack(Blocks.NETHER_BRICK), new ItemStack(Items.NETHERBRICK, 4)));
    addRecipe(new RecipePackager(new ItemStack(Blocks.QUARTZ_BLOCK), new ItemStack(Items.QUARTZ, 4)));
    addRecipe(new RecipePackager(new ItemStack(Blocks.HAY_BLOCK), new ItemStack(Items.WHEAT, 9)));
    addRecipe(new RecipePackager(new ItemStack(Blocks.EMERALD_BLOCK), new ItemStack(Items.EMERALD, 9)));
    addRecipe(new RecipePackager(new ItemStack(Blocks.DIAMOND_BLOCK), new ItemStack(Items.DIAMOND, 9)));
    addRecipe(new RecipePackager(new ItemStack(Blocks.MAGMA), new ItemStack(Items.MAGMA_CREAM, 4)));
    addRecipe(new RecipePackager(new ItemStack(Blocks.NETHER_WART_BLOCK), new ItemStack(Items.NETHER_WART, 9)));
    addRecipe(new RecipePackager(new ItemStack(Blocks.SLIME_BLOCK), new ItemStack(Items.SLIME_BALL, 9)));
    addRecipe(new RecipePackager(new ItemStack(Blocks.PURPUR_BLOCK), new ItemStack(Items.CHORUS_FRUIT_POPPED, 4)));
    addRecipe(new RecipePackager(new ItemStack(Blocks.CLAY), new ItemStack(Items.CLAY_BALL, 4)));
    addRecipe(new RecipePackager(new ItemStack(Blocks.SNOW), new ItemStack(Items.SNOWBALL, 4)));
    addRecipe(new RecipePackager(new ItemStack(Blocks.SNOW), new ItemStack(Blocks.SNOW_LAYER, 8)));
    addRecipe(new RecipePackager(new ItemStack(Blocks.PRISMARINE, 1, 0), new ItemStack(Items.PRISMARINE_SHARD, 4)));
    addRecipe(new RecipePackager(new ItemStack(Blocks.SANDSTONE), new ItemStack(Blocks.SAND, 4, 0)));
    addRecipe(new RecipePackager(new ItemStack(Blocks.RED_SANDSTONE), new ItemStack(Blocks.SAND, 4, 1)));
    //cant have both \0/ so just use crafter for the pris bricks
    //    addRecipe(new RecipePackage(new ItemStack(Blocks.PRISMARINE, 1, 1), new ItemStack(Items.PRISMARINE_SHARD, 9)));
    addRecipe(new RecipePackager(new ItemStack(Blocks.LAPIS_BLOCK), new ItemStack(Items.DYE, 9, EnumDyeColor.BLUE.getDyeDamage())));
    addRecipe(new RecipePackager(new ItemStack(Blocks.BONE_BLOCK), new ItemStack(Items.DYE, 9, EnumDyeColor.WHITE.getDyeDamage())));
  }

  public static void addRecipe(RecipePackager rec) {
    recipes.add(rec);
  }
}

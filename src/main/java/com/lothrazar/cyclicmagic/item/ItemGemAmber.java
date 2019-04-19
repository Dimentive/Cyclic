package com.lothrazar.cyclicmagic.item;

import com.lothrazar.cyclicmagic.IContent;
import com.lothrazar.cyclicmagic.data.IHasOreDict;
import com.lothrazar.cyclicmagic.data.IHasRecipe;
import com.lothrazar.cyclicmagic.guide.GuideCategory;
import com.lothrazar.cyclicmagic.registry.ItemRegistry;
import com.lothrazar.cyclicmagic.util.Const;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.config.Configuration;

public class ItemGemAmber extends Item implements IHasOreDict, IHasRecipe, IContent {

  @Override
  public String[] getOreDict() {
    return new String[] { "gemAmber" };
  }

  @Override
  public IRecipe addRecipe() {
    return null;
    //    RecipeRegistry.addShapedOreRecipe(new ItemStack(this, 2),
    //        "ofo",
    //        "bob",
    //        "ofo",
    //        'b', Items.SNOWBALL,
    //        'o', "logWood",
    //        'f', Items.NETHERBRICK);
  }

  private boolean enabled;

  @Override
  public boolean enabled() {
    return enabled;
  }

  @Override
  public String getContentName() {
    return "crystallized_amber";
  }

  @Override
  public void syncConfig(Configuration config) {
    enabled = config.getBoolean(getContentName(), Const.ConfigCategory.content, true, Const.ConfigCategory.contentDefaultText
        + "  Warning, removing this crafting item may cause some recipes to not work correctly or be too inexpensive. "
        + " So be prepared to customize recipes if you disable this.  It has ore dictionary 'gemObsidian' ");
  }

  @Override
  public void register() {
    ItemRegistry.register(this, getContentName(), GuideCategory.GEAR);
  }
}

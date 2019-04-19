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
package com.lothrazar.cyclicmagic.item.torchmagic;

import com.lothrazar.cyclicmagic.IContent;
import com.lothrazar.cyclicmagic.data.IHasRecipe;
import com.lothrazar.cyclicmagic.entity.EntityThrowableDispensable;
import com.lothrazar.cyclicmagic.guide.GuideCategory;
import com.lothrazar.cyclicmagic.item.core.BaseItemProjectile;
import com.lothrazar.cyclicmagic.registry.ItemRegistry;
import com.lothrazar.cyclicmagic.registry.RecipeRegistry;
import com.lothrazar.cyclicmagic.util.Const;
import com.lothrazar.cyclicmagic.util.UtilPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;

public class ItemProjectileTorch extends BaseItemProjectile implements IHasRecipe, IContent {

  public ItemProjectileTorch() {
    super();
  }

  @Override
  public String getContentName() {
    return "ender_torch";
  }

  @Override
  public void register() {
    ItemRegistry.register(this, getContentName(), GuideCategory.ITEMTHROW);
    EntityTorchBolt.register();
    EntityTorchBolt.item = this;
  }

  private boolean enabled;

  @Override
  public boolean enabled() {
    return enabled;
  }

  @Override
  public void syncConfig(Configuration config) {
    enabled = config.getBoolean("EnderTorch", Const.ConfigCategory.content, true, Const.ConfigCategory.contentDefaultText);
  }

  @Override
  public EntityThrowableDispensable getThrownEntity(World world, ItemStack held, double x, double y, double z) {
    return new EntityTorchBolt(world, x, y, z, false);
  }

  @Override
  public IRecipe addRecipe() {
    RecipeRegistry.addShapelessRecipe(new ItemStack(this, 1),
        new ItemStack(Blocks.TALLGRASS, 1, OreDictionary.WILDCARD_VALUE),
        "torch");
    return RecipeRegistry.addShapelessRecipe(new ItemStack(this, 1),
        "treeLeaves",
        "torch");
  }

  @Override
  public void onItemThrow(ItemStack held, World world, EntityPlayer player, EnumHand hand) {
    this.doThrow(world, player, hand, new EntityTorchBolt(world, player, false));
    UtilPlayer.decrStackSize(player, hand);
  }

  @Override
  public SoundEvent getSound() {
    // TODO Auto-generated method stub
    return SoundEvents.ENTITY_EGG_THROW;
  }
}

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
package com.lothrazar.cyclicmagic.item.merchant;

import com.lothrazar.cyclicmagic.IContent;
import com.lothrazar.cyclicmagic.ModCyclic;
import com.lothrazar.cyclicmagic.data.IHasRecipe;
import com.lothrazar.cyclicmagic.gui.ForgeGuiHandler;
import com.lothrazar.cyclicmagic.item.core.BaseItem;
import com.lothrazar.cyclicmagic.registry.ItemRegistry;
import com.lothrazar.cyclicmagic.registry.RecipeRegistry;
import com.lothrazar.cyclicmagic.util.Const;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemMerchantAlmanac extends BaseItem implements IHasRecipe, IContent {

  public static final int radius = 5;

  public ItemMerchantAlmanac() {
    super();
    this.setMaxStackSize(1);
  }

  @Override
  public void register() {
    ItemRegistry.register(this, "tool_trade");
    ModCyclic.instance.events.register(this);
  }

  @Override
  public String getContentName() {
    return "tool_trade";
  }

  private boolean enabled;

  @Override
  public boolean enabled() {
    return enabled;
  }

  @Override
  public void syncConfig(Configuration config) {
    enabled = config.getBoolean("Merchant Almanac", Const.ConfigCategory.content, true, getContentName() + Const.ConfigCategory.contentDefaultText);
  }

  @SubscribeEvent
  public void onEntityInteractEvent(EntityInteract event) {
    if (event.getTarget() instanceof EntityVillager && event.getEntityPlayer().getHeldItem(event.getHand()).getItem() == this) {
      BlockPos p = event.getTarget().getPosition();
      event.getEntityPlayer().openGui(ModCyclic.instance, ForgeGuiHandler.GUI_INDEX_VILLAGER, event.getWorld(),
          p.getX(), p.getY(), p.getZ());
      //      event.setCanceled(true);
    }
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
    BlockPos p = player.getPosition();
    if (world.isRemote == false) {
      player.openGui(ModCyclic.instance, ForgeGuiHandler.GUI_INDEX_VILLAGER, world, p.getX(), p.getY(), p.getZ());
    }
    return new ActionResult<ItemStack>(EnumActionResult.PASS, player.getHeldItem(hand));
  }

  @Override
  public IRecipe addRecipe() {
    RecipeRegistry.addShapedRecipe(new ItemStack(this), " e ", " b ", " q ",
        'e', "gemEmerald",
        'b', Items.BOOK,
        'q', Blocks.BROWN_MUSHROOM);
    return RecipeRegistry.addShapedRecipe(new ItemStack(this), " e ", " b ", " q ",
        'e', "gemEmerald",
        'b', Items.BOOK,
        'q', Blocks.RED_MUSHROOM);
  }
}

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
package com.lothrazar.cyclicmagic.item;

import com.lothrazar.cyclicmagic.IContent;
import com.lothrazar.cyclicmagic.ModCyclic;
import com.lothrazar.cyclicmagic.data.IHasRecipe;
import com.lothrazar.cyclicmagic.item.core.BaseItem;
import com.lothrazar.cyclicmagic.registry.ItemRegistry;
import com.lothrazar.cyclicmagic.registry.RecipeRegistry;
import com.lothrazar.cyclicmagic.util.Const;
import com.lothrazar.cyclicmagic.util.UtilChat;
import com.lothrazar.cyclicmagic.util.UtilSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SoulstoneItem extends BaseItem implements IHasRecipe, IContent {

  private static final float HEALTH_AFTER_TRIGGER = 6;//3 hearts 

  public SoulstoneItem() {
    super();
    this.setMaxStackSize(1);
  }

  @Override
  public String getContentName() {
    return "soulstone";
  }

  @Override
  public void register() {
    ItemRegistry.register(this, getContentName());
    ModCyclic.instance.events.register(this);
  }

  private boolean enabled;

  @Override
  public boolean enabled() {
    return enabled;
  }

  @Override
  public void syncConfig(Configuration config) {
    enabled = config.getBoolean("Soulstone", Const.ConfigCategory.content, true, Const.ConfigCategory.contentDefaultText);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean hasEffect(ItemStack stack) {
    return true;
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void onLivingDamageEvent(LivingDamageEvent event) {
    //used to be net.minecraftforge.event.entity.living.LivingHurtEvent
    float currentHealth = event.getEntityLiving().getAbsorptionAmount()
        + event.getEntityLiving().getHealth();
    if (currentHealth - event.getAmount() <= 0 && event.getEntityLiving() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) event.getEntityLiving();
      for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
        ItemStack stack = player.inventory.getStackInSlot(i);
        if (stack.getItem() instanceof SoulstoneItem) {
          player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
          player.setHealth(HEALTH_AFTER_TRIGGER);
          applyPotions(player);
          UtilSound.playSound(player, SoundEvents.BLOCK_GLASS_BREAK);
          UtilChat.addChatMessage(player, event.getEntityLiving().getName() + UtilChat.lang("item.soulstone.used"));
          event.setCanceled(true);
          break;
        }
      }
    }
  }

  private void applyPotions(EntityPlayer p) {
    int time = Const.TICKS_PER_SEC * 30;
    p.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, time));
    time = Const.TICKS_PER_SEC * 60;//a full minute
    p.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, time));
    p.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, time, 4));
    //and bad luck lasts much longer
    time = Const.TICKS_PER_SEC * 60 * 10;
    p.addPotionEffect(new PotionEffect(MobEffects.UNLUCK, time));
    p.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, time, 1));
  }

  @Override
  public IRecipe addRecipe() {
    return RecipeRegistry.addShapedRecipe(new ItemStack(this),
        " a ", "bsc", " d ",
        's', "netherStar",
        'a', Items.GOLDEN_APPLE,
        'b', Items.POISONOUS_POTATO,
        'c', Blocks.PURPUR_BLOCK,
        'd', "gemEmerald");
  }
}

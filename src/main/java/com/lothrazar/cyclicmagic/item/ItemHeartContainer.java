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

import java.util.List;
import com.lothrazar.cyclicmagic.ModCyclic;
import com.lothrazar.cyclicmagic.capability.IPlayerExtendedProperties;
import com.lothrazar.cyclicmagic.command.CommandHearts;
import com.lothrazar.cyclicmagic.config.IHasConfig;
import com.lothrazar.cyclicmagic.data.IHasRecipe;
import com.lothrazar.cyclicmagic.item.core.ItemFoodCreative;
import com.lothrazar.cyclicmagic.net.PacketSyncPlayerHealth;
import com.lothrazar.cyclicmagic.registry.CapabilityRegistry;
import com.lothrazar.cyclicmagic.registry.RecipeRegistry;
import com.lothrazar.cyclicmagic.registry.SoundRegistry;
import com.lothrazar.cyclicmagic.util.Const;
import com.lothrazar.cyclicmagic.util.UtilChat;
import com.lothrazar.cyclicmagic.util.UtilEntity;
import com.lothrazar.cyclicmagic.util.UtilSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemHeartContainer extends ItemFoodCreative implements IHasRecipe, IHasConfig {

  private static final int numFood = 2;
  private int heartChangeOnEat = 1;
  public static int defaultHearts = 10;
  private static int maxHearts = 20;

  public ItemHeartContainer(int heartChangeOnEat) {
    super(numFood, false);
    this.heartChangeOnEat = heartChangeOnEat;
  }

  @Override
  protected void onFoodEaten(ItemStack par1ItemStack, World world, EntityPlayer player) {
    if (canEat(player) == false) {
      return;
    }
    IPlayerExtendedProperties prop = CapabilityRegistry.getPlayerProperties(player);
    int healthChange = 2 * heartChangeOnEat;
    //one heart is 2 health points (half heart = 1 health)
    int newVal = UtilEntity.incrementMaxHealth(player, healthChange);
    prop.setMaxHealth(newVal);
    UtilChat.sendStatusMessage(player, (newVal / 2) + "");
    UtilSound.playSound(player, SoundRegistry.heart_container);
  }

  @Override
  public IRecipe addRecipe() {
    if (heartChangeOnEat > 0)
      return RecipeRegistry.addShapelessRecipe(new ItemStack(this), Items.RABBIT_STEW, Blocks.WATERLILY, Items.PUMPKIN_PIE,
          "gemDiamond", Items.COOKIE, "blockEmerald",
          new ItemStack(Items.FISH, 1, ItemFishFood.FishType.SALMON.getMetadata()), new ItemStack(Items.GOLDEN_APPLE, 1, 0), Items.POISONOUS_POTATO);
    else
      return RecipeRegistry.addShapelessRecipe(new ItemStack(this), Items.BEETROOT, Items.STICK, Items.SUGAR,
          "dirt", Items.CAKE, "cobblestone",
          new ItemStack(Items.SPIDER_EYE), Items.APPLE, Items.SPIDER_EYE);
  }

  @SubscribeEvent
  public void onPlayerWarp(PlayerChangedDimensionEvent event) {
    IPlayerExtendedProperties props = CapabilityRegistry.getPlayerProperties(event.player);
    if (props.getMaxHealth() > 0 && event.player instanceof EntityPlayerMP) {
      //force clientside hearts to visually update
      ModCyclic.network.sendTo(new PacketSyncPlayerHealth(props.getMaxHealth()), (EntityPlayerMP) event.player);
    }
  }

  @Override
  public void syncConfig(Configuration config) {
    maxHearts = config.getInt("HeartContainerMax", Const.ConfigCategory.modpackMisc,
        20, 10, 100, "Maximum number of heart containers you can get by eating heart containers.  Does not limit the /" + CommandHearts.name + " command");
    defaultHearts = config.getInt("HeartContainerDefault", Const.ConfigCategory.player,
        10, 1, 100, "Default number of heart containers a new player will start with when first joining the world.  Will not affect existing players once they have joined.  (For Maximum heart limit given by the heart container see 'modpacks' category in the config file)");
  }

  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
    tooltip.add(UtilChat.lang(this.getTranslationKey() + ".tooltip"));
  }

  private boolean canEat(EntityPlayer player) {
    //this line is KEY to stop user from eating food at max health
    // ( which was causing the refund issue in https://github.com/PrinceOfAmber/Cyclic/issues/270 )
    double currentHearts = UtilEntity.getMaxHealth(player) / 2;
    if (currentHearts + heartChangeOnEat > maxHearts || currentHearts + heartChangeOnEat < 1) {
      return false;
    }
    return true;
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer player, EnumHand hand) {
    if (canEat(player) == false) {
      return new ActionResult<ItemStack>(EnumActionResult.FAIL, player.getHeldItem(hand));
    }
    //otherwise continueto normal food process
    return super.onItemRightClick(worldIn, player, hand);
  }
}

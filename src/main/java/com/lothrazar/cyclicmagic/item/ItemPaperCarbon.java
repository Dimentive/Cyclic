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
import com.lothrazar.cyclicmagic.IContent;
import com.lothrazar.cyclicmagic.data.IHasRecipe;
import com.lothrazar.cyclicmagic.item.core.BaseItem;
import com.lothrazar.cyclicmagic.registry.ItemRegistry;
import com.lothrazar.cyclicmagic.registry.RecipeRegistry;
import com.lothrazar.cyclicmagic.util.Const;
import com.lothrazar.cyclicmagic.util.UtilChat;
import com.lothrazar.cyclicmagic.util.UtilParticle;
import com.lothrazar.cyclicmagic.util.UtilSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPaperCarbon extends BaseItem implements IHasRecipe, IContent {

  public static final String name = "carbon_paper";
  public static int NOTE_EMPTY = -1;
  private static final String KEY_SIGN0 = "sign_0";
  private static final String KEY_SIGN1 = "sign_1";
  private static final String KEY_SIGN2 = "sign_2";
  private static final String KEY_SIGN3 = "sign_3";
  private static final String KEY_NOTE = "note";

  public ItemPaperCarbon() {
    super();
    this.setMaxStackSize(1);
  }

  private static void setItemStackNBT(ItemStack item, String prop, String value) {
    item.getTagCompound().setString(prop, value);
  }

  private static String getItemStackNBT(ItemStack item, String prop) {
    String s = item.getTagCompound().getString(prop);
    if (s == null) {
      s = "";
    }
    return s;
  }

  public static void copySign(World world, EntityPlayer entityPlayer, TileEntitySign sign, ItemStack held) {
    if (held.getTagCompound() == null) {
      held.setTagCompound(new NBTTagCompound());
    }
    setItemStackNBT(held, KEY_SIGN0, ITextComponent.Serializer.componentToJson(sign.signText[0]));
    setItemStackNBT(held, KEY_SIGN1, ITextComponent.Serializer.componentToJson(sign.signText[1]));
    setItemStackNBT(held, KEY_SIGN2, ITextComponent.Serializer.componentToJson(sign.signText[2]));
    setItemStackNBT(held, KEY_SIGN3, ITextComponent.Serializer.componentToJson(sign.signText[3]));
    held.getTagCompound().setByte(KEY_NOTE, (byte) NOTE_EMPTY);
    // entityPlayer.swingItem();
  }

  public static void pasteSign(World world, EntityPlayer entityPlayer, TileEntitySign sign, ItemStack held) {
    if (held.getTagCompound() == null) {
      held.setTagCompound(new NBTTagCompound());
    }
    try {
      sign.signText[0] = ITextComponent.Serializer.jsonToComponent(getItemStackNBT(held, KEY_SIGN0));
      sign.signText[1] = ITextComponent.Serializer.jsonToComponent(getItemStackNBT(held, KEY_SIGN1));
      sign.signText[2] = ITextComponent.Serializer.jsonToComponent(getItemStackNBT(held, KEY_SIGN2));
      sign.signText[3] = ITextComponent.Serializer.jsonToComponent(getItemStackNBT(held, KEY_SIGN3));
      return;
    }
    catch (Exception e) {
      //legacy support below
    }
    sign.signText[0] = new TextComponentTranslation(getItemStackNBT(held, KEY_SIGN0));
    sign.signText[1] = new TextComponentTranslation(getItemStackNBT(held, KEY_SIGN1));
    sign.signText[2] = new TextComponentTranslation(getItemStackNBT(held, KEY_SIGN2));
    sign.signText[3] = new TextComponentTranslation(getItemStackNBT(held, KEY_SIGN3));
  }

  public static void copyNote(World world, EntityPlayer entityPlayer, TileEntityNote noteblock, ItemStack held) {
    if (held.getTagCompound() == null) {
      held.setTagCompound(new NBTTagCompound());
    }
    held.getTagCompound().setByte(KEY_NOTE, noteblock.note);
  }

  public static void pasteNote(World world, EntityPlayer entityPlayer, TileEntityNote noteblock, ItemStack held) {
    if (held.getTagCompound() == null) {
      return;
    } // nothing ot paste
    if (held.getTagCompound().getByte(KEY_NOTE) == NOTE_EMPTY) {
      return;
    }
    noteblock.note = held.getTagCompound().getByte(KEY_NOTE);
  }

  @Override
  public String getContentName() {
    return "carbon_paper";
  }

  @Override
  public void register() {
    ItemRegistry.register(this, getContentName());
  }

  private boolean enabled;

  @Override
  public boolean enabled() {
    return enabled;
  }

  @Override
  public void syncConfig(Configuration config) {
    enabled = config.getBoolean("CarbonPaper", Const.ConfigCategory.content, true, Const.ConfigCategory.contentDefaultText);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void addInformation(ItemStack held, World player, List<String> list, net.minecraft.client.util.ITooltipFlag advanced) {
    boolean isEmpty = (held.getTagCompound() == null);
    if (isEmpty) {
      list.add(UtilChat.lang("item.carbon_paper.tooltip"));
      return;
    }
    String sign = getItemStackNBT(held, KEY_SIGN0) + getItemStackNBT(held, KEY_SIGN1) + getItemStackNBT(held, KEY_SIGN2) + getItemStackNBT(held, KEY_SIGN3);
    if (sign.length() > 0) {
      list.add(getItemStackNBT(held, KEY_SIGN0));
      list.add(getItemStackNBT(held, KEY_SIGN1));
      list.add(getItemStackNBT(held, KEY_SIGN2));
      list.add(getItemStackNBT(held, KEY_SIGN3));
    }
    String s = noteToString(held.getTagCompound().getByte(KEY_NOTE));
    if (s != null) {
      list.add(UtilChat.lang("item.carbon_paper.note") + s);
    }
  }

  @Override
  public EnumActionResult onItemUseFirst(EntityPlayer entityPlayer, World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, EnumHand hand) {
    TileEntity container = world.getTileEntity(pos);
    boolean isValid = false;
    ItemStack held = entityPlayer.getHeldItem(hand);
    boolean isEmpty = (held.getTagCompound() == null);
    if (container instanceof TileEntitySign) {
      TileEntitySign sign = (TileEntitySign) container;
      if (isEmpty) {
        copySign(world, entityPlayer, sign, held);
      }
      else {
        pasteSign(world, entityPlayer, sign, held);
      }
      isValid = true;
    }
    if (container instanceof TileEntityNote) {
      TileEntityNote noteblock = (TileEntityNote) container;
      if (isEmpty) {
        copyNote(world, entityPlayer, noteblock, held);
      }
      else {
        pasteNote(world, entityPlayer, noteblock, held);
      }
      isValid = true;
    }
    if (isValid) {
      UtilParticle.spawnParticle(world, EnumParticleTypes.PORTAL, pos.getX(), pos.getY(), pos.getZ());
      UtilSound.playSound(entityPlayer, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH);
    }
    return EnumActionResult.PASS;
  }

  public static String noteToString(byte note) {
    String s = null;
    switch (note) {
      case 0:
        s = TextFormatting.YELLOW + "F#";
      break;// yellow
      case 1:
        s = TextFormatting.YELLOW + "G";
      break;
      case 2:
        s = TextFormatting.YELLOW + "G#";
      break;
      case 3:
        s = TextFormatting.YELLOW + "A";
      break;// or
      case 4:
        s = TextFormatting.YELLOW + "A#";
      break;// or
      case 5:
        s = TextFormatting.RED + "B";
      break;// red
      case 6:
        s = TextFormatting.RED + "C";
      break;// red
      case 7:
        s = TextFormatting.DARK_RED + "C#";
      break;
      case 8:
        s = TextFormatting.DARK_RED + "D";
      break;
      case 9:
        s = TextFormatting.LIGHT_PURPLE + "D#";
      break;// pink
      case 10:
        s = TextFormatting.LIGHT_PURPLE + "E";
      break;
      case 11:
        s = TextFormatting.DARK_PURPLE + "F";
      break;// purp
      case 12:
        s = TextFormatting.DARK_PURPLE + "F#";
      break;
      case 13:
        s = TextFormatting.DARK_PURPLE + "G";
      break;
      case 14:
        s = TextFormatting.DARK_BLUE + "G#";
      break;
      case 15:
        s = TextFormatting.DARK_BLUE + "A";
      break;// blue
      case 16:
        s = TextFormatting.BLUE + "A#";
      break;
      case 17:
        s = TextFormatting.BLUE + "B";
      break;
      case 18:
        s = TextFormatting.DARK_AQUA + "C";
      break;// lt blue?
      case 19:
        s = TextFormatting.AQUA + "C#";
      break;
      case 20:
        s = TextFormatting.AQUA + "D";
      break;// EnumChatFormatting.GREEN
      case 21:
        s = TextFormatting.GREEN + "D#";
      break;// there is no light green or dark green...
      case 22:
        s = TextFormatting.GREEN + "E";
      break;
      case 23:
        s = TextFormatting.AQUA + "F";
      break;
      case 24:
        s = TextFormatting.AQUA + "F#";
      break;// EnumChatFormatting.GREEN
    }
    return s;
  }

  @Override
  public IRecipe addRecipe() {
    RecipeRegistry.addShapelessRecipe(new ItemStack(this), new ItemStack(this));
    return RecipeRegistry.addShapedRecipe(new ItemStack(this, 8), "ppp", "pcp", "ppp",
        'c', new ItemStack(Items.COAL, 1, 1), // charcoal
        'p', "paper");
    //also let you clean off the paper , make one with no NBT
  }
}

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
package com.lothrazar.cyclicmagic.util;

import java.util.ArrayList;
import java.util.UUID;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;

public class UtilNBT {

  public static int intColor(int r, int g, int b) {
    return (r * 65536 + g * 256 + b);
  }

  public static String posToStringCSV(BlockPos position) {
    if (position == null) {
      return "";
    }
    return position.getX() + "," + position.getY() + "," + position.getZ();
  }

  public static void setTagBlockPos(NBTTagCompound item, BlockPos pos) {
    if (pos == null) {
      return;
    }
    item.setInteger("xpos", pos.getX());
    item.setInteger("ypos", pos.getY());
    item.setInteger("zpos", pos.getZ());
  }

  public static BlockPos getTagBlockPos(NBTTagCompound item) {
    if (item == null
        || !item.hasKey("xpos")) {
      return null;
    }
    return new BlockPos(item.getInteger("xpos"), item.getInteger("ypos"), item.getInteger("zpos"));
  }

  public static void setItemStackBlockPos(ItemStack item, BlockPos pos) {
    if (pos == null) {
      return;
    }
    UtilNBT.setItemStackNBTVal(item, "xpos", pos.getX());
    UtilNBT.setItemStackNBTVal(item, "ypos", pos.getY());
    UtilNBT.setItemStackNBTVal(item, "zpos", pos.getZ());
  }

  public static BlockPos getItemStackBlockPos(ItemStack item) {
    if (item.isEmpty()
        || item.getTagCompound() == null
        || !item.getTagCompound().hasKey("xpos")) {
      return null;
    }
    return new BlockPos(getItemStackNBTVal(item, "xpos"), getItemStackNBTVal(item, "ypos"), getItemStackNBTVal(item, "zpos"));
  }

  public static void setItemStackNBTVal(ItemStack item, String prop, int value) {
    if (item.isEmpty()) {
      return;
    }
    getItemStackNBT(item).setInteger(prop, value);
  }

  public static void setItemStackNBTVal(ItemStack item, String prop, String value) {
    if (item.isEmpty()) {
      return;
    }
    getItemStackNBT(item).setString(prop, value);
  }

  public static int getItemStackNBTVal(ItemStack held, String prop) {
    if (held.isEmpty()) {
      return 0;
    }
    NBTTagCompound tags = getItemStackNBT(held);
    if (!tags.hasKey(prop)) {
      return 0;
    }
    return tags.getInteger(prop);
  }

  /**
   * empty string if null or empty, otherwise the value in string form for tooltips
   * 
   * @param held
   * @param prop
   * @return
   */
  public static String getItemStackDisplayInteger(ItemStack held, String prop) {
    if (held.isEmpty()) {
      return "";
    }
    NBTTagCompound tags = getItemStackNBT(held);
    if (!tags.hasKey(prop)) {
      return "";
    }
    return tags.getInteger(prop) + "";
  }

  public static NBTTagCompound getItemStackNBT(ItemStack held) {
    if (held.getTagCompound() == null) {
      held.setTagCompound(new NBTTagCompound());
    }
    return held.getTagCompound();
  }

  public static BlockPos stringCSVToBlockPos(String csv) {
    String[] spl = csv.split(",");
    // on server i got java.lang.ClassCastException: java.lang.String cannot
    // be cast to java.lang.Integer
    // ?? is it from this?
    BlockPos p = null;
    try {
      if (spl != null && spl.length == 3 && spl[0] != "")
        p = new BlockPos(Integer.parseInt(spl[0]), Integer.parseInt(spl[1]), Integer.parseInt(spl[2]));
    }
    catch (java.lang.ClassCastException e) {
      //      ModCyclic.logger.info("exc: bad string: " + csv);
    }
    return p;
  }

  public static void incrementPlayerIntegerNBT(EntityPlayer player, String prop, int inc) {
    int prev = player.getEntityData().getInteger(prop);
    prev += inc;
    player.getEntityData().setInteger(prop, prev);
  }

  public static void writeTagsToInventory(IInventory invo, NBTTagCompound tags, String key) {
    NBTTagList items = tags.getTagList(key, tags.getId());
    ItemStack stack;
    int slot;
    for (int i = 0; i < items.tagCount(); ++i) {
      // tagAt(int) has changed to getCompoundTagAt(int)
      NBTTagCompound item = items.getCompoundTagAt(i);
      stack = UtilNBT.itemFromNBT(item);
      slot = item.getInteger("slot");
      // list.add(ItemStack.loadItemStackFromNBT(item));
      invo.setInventorySlotContents(slot, stack);
    }
  }

  public static NBTTagCompound writeInventoryToTag(IInventory invo, NBTTagCompound returnTag, String key) {
    ItemStack chestItem;
    NBTTagCompound itemTag;
    NBTTagList nbttaglist = new NBTTagList();
    for (int i = 0; i < invo.getSizeInventory(); i++) {
      // zeroes to avoid nulls, and signify nothing goes there
      chestItem = invo.getStackInSlot(i);
      if (chestItem.isEmpty() || chestItem.getCount() == 0) {
        continue;
      } // not an error; empty chest slot
      itemTag = chestItem.writeToNBT(new NBTTagCompound());
      itemTag.setInteger("slot", i);
      nbttaglist.appendTag(itemTag);
      // its either in the bag, or dropped on the player
      invo.setInventorySlotContents(i, ItemStack.EMPTY);
    }
    returnTag.setTag(key, nbttaglist);
    return returnTag;
  }

  public static NBTTagCompound writeInventoryToNewTag(IInventory invo, String key) {
    return writeInventoryToTag(invo, new NBTTagCompound(), key);
  }

  public static int countItemsFromNBT(NBTTagCompound tags, String key) {
    if (tags == null) {
      return 0;
    }
    NBTTagList items = tags.getTagList(key, tags.getId());
    if (items == null) {
      return 0;
    }
    return items.tagCount();
  }

  public static ArrayList<ItemStack> readItemsFromNBT(NBTTagCompound tags, String key) {
    ArrayList<ItemStack> list = new ArrayList<ItemStack>();
    NBTTagList items = tags.getTagList(key, tags.getId());
    for (int i = 0; i < items.tagCount(); ++i) {
      // tagAt(int) has changed to getCompoundTagAt(int)
      NBTTagCompound item = items.getCompoundTagAt(i);
      list.add(UtilNBT.itemFromNBT(item));
    }
    return list;
  }

  public static ItemStack enchantItem(Item item, Enchantment ench, short level) {
    ItemStack stack = new ItemStack(item);
    stack.addEnchantment(ench, level);
    return stack;
  }

  public static ItemStack buildEnchantedBook(Enchantment ench, short level) {
    ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
    //NOT THIS: if you are putting it on a normal stack (sword/weapon) yeah then that way
    //		stack.addEnchantment(ench, level);
    ItemEnchantedBook.addEnchantment(stack, new EnchantmentData(ench, level));
    //just to test it
    return stack;
  }

  public static ItemStack buildEnchantedNametag(String customNameTag) {
    // build multi-level NBT tag so it matches a freshly enchanted one
    ItemStack nameTag = new ItemStack(Items.NAME_TAG, 1);
    NBTTagCompound nbt = new NBTTagCompound();
    NBTTagCompound display = new NBTTagCompound();
    display.setString("Name", customNameTag);// NOT "CustomName" implied by
    // commandblocks/google
    nbt.setTag("display", display);
    nbt.setInteger("RepairCost", 1);
    nameTag.setTagCompound(nbt);// put the data into the item stack
    return nameTag;
  }

  public static ItemStack itemFromNBT(NBTTagCompound tag) {
    return new ItemStack(tag);
  }

  public static ItemStack buildNamedPlayerSkull(EntityPlayer player) {
    return buildNamedPlayerSkull(player.getDisplayNameString());
  }

  public static ItemStack buildNamedPlayerSkull(String displayNameString) {
    NBTTagCompound t = new NBTTagCompound();
    t.setString(Const.SkullOwner, displayNameString);
    return buildSkullFromTag(t);
  }

  public static ItemStack buildSkullFromTag(NBTTagCompound player) {
    ItemStack skull = new ItemStack(Items.SKULL, 1, Const.skull_player);
    skull.setTagCompound(player);
    return skull;
  }

  public static NBTTagCompound buildCustomSkullTag(String displayName, String textureData) {
    NBTTagCompound base = new NBTTagCompound();
    NBTTagCompound display = new NBTTagCompound();
    display.setString("Name", displayName);
    base.setTag("display", display);
    NBTTagCompound skullOwner = new NBTTagCompound();
    skullOwner.setString("Id", UUID.randomUUID().toString());
    NBTTagCompound props = new NBTTagCompound();
    NBTTagList list = new NBTTagList();
    NBTTagCompound textureValue = new NBTTagCompound();
    textureValue.setString("Value", textureData);
    list.appendTag(textureValue);
    props.setTag("textures", list);
    skullOwner.setTag("Properties", props);
    base.setTag(Const.SkullOwner, skullOwner);
    return base;
  }

  public static void setEntityBoolean(Entity entity, String string) {
    setEntityBoolean(entity, string, true);
  }

  public static void setEntityBoolean(Entity entityevokerfangs, String string, boolean bIn) {
    entityevokerfangs.getEntityData().setBoolean(string, bIn);
  }

  public static void setEntityInt(Entity entityevokerfangs, String string, int bIn) {
    entityevokerfangs.getEntityData().setInteger(string, bIn);
  }

  public static int getEntityInt(Entity entityevokerfangs, String string) {
    return entityevokerfangs.getEntityData().getInteger(string);
  }

  public static boolean getEntityBoolean(Entity entityevokerfangs, String string) {
    return entityevokerfangs.getEntityData().getBoolean(string);
  }

  public static boolean hasTagCompund(ItemStack held) {
    return held.hasTagCompound() &&
        held.getTagCompound().getSize() > 0;
  }

  public static boolean stacksTagsEqual(ItemStack main, ItemStack second) {
    if (!main.hasTagCompound() && !second.hasTagCompound()) {
      return true;//both no tag, so ==
    }
    if (main.hasTagCompound() && second.hasTagCompound()) {
      return main.getTagCompound().equals(second.getTagCompound());//both have tag
    }
    else {
      return false;//exactly 1 has tag 
    }
  }
}

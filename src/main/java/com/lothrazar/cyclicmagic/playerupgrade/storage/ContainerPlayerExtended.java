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
package com.lothrazar.cyclicmagic.playerupgrade.storage;

import com.lothrazar.cyclicmagic.gui.container.ContainerBase;
import com.lothrazar.cyclicmagic.util.Const;
import com.lothrazar.cyclicmagic.util.UtilPlayerInventoryFilestorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerPlayerExtended extends ContainerBase {

  public InventoryPlayerExtended inventory;
  public static final int SQ = Const.SQ;
  public static final int HOTBAR_SIZE = Const.HOTBAR_SIZE;
  private static final EntityEquipmentSlot[] ARMOR = new EntityEquipmentSlot[] { EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET };
  final int pad = Const.PAD;

  public ContainerPlayerExtended(InventoryPlayer playerInv, InventoryPlayerExtended eInvo, EntityPlayer player) {
    inventory = eInvo;
    inventory.setEventHandler(this);
    if (!player.getEntityWorld().isRemote) {
      UtilPlayerInventoryFilestorage.putDataIntoInventory(inventory, player);
    }
    int VROW = 3, VCOL = 9, armorX = -5 - 2 * Const.SQ, armorY;
    for (int k = 0; k < ARMOR.length; k++) {
      armorY = Const.PAD + k * Const.SQ;
      final EntityEquipmentSlot slot = ARMOR[k];
      this.addSlotToContainer(new Slot(playerInv, 4 * VCOL + (VROW - k), armorX, armorY) {

        @Override
        public int getSlotStackLimit() {
          return 1;
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
          return stack.getItem().isValidArmor(stack, slot, player);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public String getSlotTexture() {
          return ItemArmor.EMPTY_SLOT_NAMES[slot.getIndex()];
        }
      });
    }
    //  extended
    int xPos, yPos, sl = 0;
    for (int i = 0; i < InventoryPlayerExtended.IROW; ++i) {
      for (int j = 0; j < InventoryPlayerExtended.ICOL; ++j) {
        xPos = pad + j * SQ;
        yPos = pad + i * SQ;
        sl = j + (i + 1) * InventoryPlayerExtended.ICOL;
        this.addSlotToContainer(new Slot(inventory, sl, xPos, yPos));
      }
    }
    //extended armor 
    for (int k = 0; k < ARMOR.length; k++) {
      sl++;
      armorY = Const.PAD + (k) * Const.SQ;
      final EntityEquipmentSlot slot = ARMOR[k];
      this.addSlotToContainer(new Slot(inventory, sl, armorX + Const.SQ + 1, armorY) {

        @Override
        public int getSlotStackLimit() {
          return 1;
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
          return stack.getItem().isValidArmor(stack, slot, player);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public String getSlotTexture() {
          return ItemArmor.EMPTY_SLOT_NAMES[slot.getIndex()];
        }
      });
    }
    //  player inventory
    for (int i = 0; i < Const.ROWS_VANILLA; ++i) {
      for (int j = 0; j < Const.COLS_VANILLA; ++j) {
        xPos = pad + j * SQ;
        yPos = 84 + i * SQ;
        sl = j + (i + 1) * Const.COLS_VANILLA;
        this.addSlotToContainer(new Slot(playerInv, sl, xPos, yPos));
      }
    }
    //hotbar
    yPos = 142;
    for (int i = 0; i < Const.HOTBAR_SIZE; ++i) {
      xPos = pad + i * SQ;
      sl = i;
      this.addSlotToContainer(new Slot(playerInv, sl, xPos, yPos));
    }
  }

  /**
   * Called when the container is closed.
   */
  @Override
  public void onContainerClosed(EntityPlayer player) {
    super.onContainerClosed(player);
    if (!player.getEntityWorld().isRemote) {
      UtilPlayerInventoryFilestorage.setPlayerInventory(player, inventory);
    }
  }

  /**
   * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
   */
  @Override
  public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int iSlot) {
    ItemStack stack = ItemStack.EMPTY;
    Slot slot = this.inventorySlots.get(iSlot);
    /// the topEnd +4 is for right hand armor
    int playerStart = 44, playerEnd = 70, topStart = 4, topEnd = 39 + 4, hotbarStart = 71, hotbarEnd = hotbarStart + 8, armorStart = 0, armorEnd = 3;
    if (slot != null && slot.getHasStack()) {
      ItemStack stackInSlot = slot.getStack();
      stack = stackInSlot.copy();
      if (armorStart <= iSlot && iSlot <= armorEnd) {
        if (!this.mergeItemStack(stackInSlot, playerStart, hotbarEnd, false)) {
          return ItemStack.EMPTY;
        }
      }
      else if (playerStart <= iSlot && iSlot <= playerEnd) {
        if (!this.mergeItemStack(stackInSlot, topStart, topEnd + 1, false)) {
          return ItemStack.EMPTY;
        }
      }
      else if (topStart <= iSlot && iSlot <= topEnd) {
        if (!this.mergeItemStack(stackInSlot, playerStart, playerEnd, false)) {
          return ItemStack.EMPTY;
        }
      }
      else if (hotbarStart <= iSlot && iSlot <= hotbarEnd) {
        if (!this.mergeItemStack(stackInSlot, topStart, topEnd, false)) {
          return ItemStack.EMPTY;
        }
      }
      if (stackInSlot.getCount() == 0) {
        slot.putStack(ItemStack.EMPTY);
      }
      else {
        slot.onSlotChanged();
      }
      if (stackInSlot.getCount() == stack.getCount()) {
        return ItemStack.EMPTY;
      }
      slot.onTake(par1EntityPlayer, stackInSlot);
    }
    return stack;
  }

  protected boolean mergeItemStack(ItemStack par1ItemStack, int par2, int par3, boolean par4, Slot ss) {
    boolean flag1 = false;
    int k = par2;
    if (par4) {
      k = par3 - 1;
    }
    Slot slot;
    ItemStack itemstack1;
    if (par1ItemStack.isStackable()) {
      while (par1ItemStack.getCount() > 0 && (!par4 && k < par3 || par4 && k >= par2)) {
        slot = this.inventorySlots.get(k);
        itemstack1 = slot.getStack();
        if (!itemstack1.isEmpty() && itemstack1.getItem() == par1ItemStack.getItem() && (!par1ItemStack.getHasSubtypes() || par1ItemStack.getItemDamage() == itemstack1.getItemDamage()) && ItemStack.areItemStackTagsEqual(par1ItemStack, itemstack1)) {
          int l = itemstack1.getCount() + par1ItemStack.getCount();
          if (l <= par1ItemStack.getMaxStackSize()) {
            // if (ss instanceof SlotBauble) unequipBauble(par1ItemStack);
            par1ItemStack.setCount(0);
            itemstack1.setCount(l);
            slot.onSlotChanged();
            flag1 = true;
          }
          else if (itemstack1.getCount() < par1ItemStack.getMaxStackSize()) {
            // if (ss instanceof SlotBauble) unequipBauble(par1ItemStack);
            par1ItemStack.setCount(par1ItemStack.getCount() - par1ItemStack.getMaxStackSize() - itemstack1.getCount());
            itemstack1.setCount(par1ItemStack.getMaxStackSize());
            slot.onSlotChanged();
            flag1 = true;
          }
        }
        if (par4) {
          --k;
        }
        else {
          ++k;
        }
      }
    }
    if (par1ItemStack.getCount() > 0) {
      if (par4) {
        k = par3 - 1;
      }
      else {
        k = par2;
      }
      while (!par4 && k < par3 || par4 && k >= par2) {
        slot = this.inventorySlots.get(k);
        itemstack1 = slot.getStack();
        if (itemstack1 == null || itemstack1.isEmpty()) {
          // if (ss instanceof SlotBauble) unequipBauble(par1ItemStack);
          slot.putStack(par1ItemStack.copy());
          slot.onSlotChanged();
          par1ItemStack.setCount(0);
          flag1 = true;
          break;
        }
        if (par4) {
          --k;
        }
        else {
          ++k;
        }
      }
    }
    return flag1;
  }
}

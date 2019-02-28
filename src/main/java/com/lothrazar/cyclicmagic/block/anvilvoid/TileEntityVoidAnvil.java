package com.lothrazar.cyclicmagic.block.anvilvoid;

import java.util.Map;
import com.google.common.collect.Maps;
import com.lothrazar.cyclicmagic.block.core.TileEntityBaseMachineInvo;
import com.lothrazar.cyclicmagic.gui.ITileRedstoneToggle;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;

public class TileEntityVoidAnvil extends TileEntityBaseMachineInvo implements ITileRedstoneToggle, ITickable {

  public static enum Fields {
    REDSTONE, TIMER, FUEL;
  }

  public static final int SLOT_INPUT = 0;
  public static final int SLOT_OUT = 1;

  public TileEntityVoidAnvil() {
    super(2);
    this.initEnergy(BlockVoidAnvil.FUEL_COST);
    this.setSlotsForInsert(SLOT_INPUT);
    this.setSlotsForExtract(SLOT_OUT);
  }

  @Override
  public void toggleNeedsRedstone() {
    int val = (this.needsRedstone + 1) % 2;
    this.setField(Fields.REDSTONE.ordinal(), val);
  }

  @Override
  public boolean onlyRunIfPowered() {
    return this.needsRedstone == 1;
  }

  @Override
  public int[] getFieldOrdinals() {
    return super.getFieldArray(Fields.values().length);
  }

  @Override
  public int getField(int id) {
    switch (Fields.values()[id]) {
      case TIMER:
        return timer;
      case REDSTONE:
        return this.needsRedstone;
      case FUEL:
        return this.getEnergyCurrent();
    }
    return -1;
  }

  @Override
  public void setField(int id, int value) {
    switch (Fields.values()[id]) {
      case TIMER:
        this.timer = value;
      break;
      case REDSTONE:
        this.needsRedstone = value;
      break;
      case FUEL:
        this.setEnergyCurrent(value);
      break;
    }
  }

  @Override
  public void update() {
    if (this.isRunning() == false) {
      return;
    }
    ItemStack input = this.getStackInSlot(SLOT_INPUT);
    if (input.isEmpty()) {
      return;
    }
    Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(input);
    if (enchants.size() == 0) {
      //
      if (this.getStackInSlot(SLOT_OUT).isEmpty()) {
        this.setInventorySlotContents(SLOT_OUT, input.copy());
        this.setInventorySlotContents(SLOT_INPUT, ItemStack.EMPTY);
      }
    }
    else {
      if (this.updateEnergyIsBurning() == false) {
        return;
      }
      // non empty 
      EnchantmentHelper.setEnchantments(Maps.<Enchantment, Integer> newLinkedHashMap(), input);
      if (input.getTagCompound().getSize() == 0) {
        //only delete if its empty 
        input.setTagCompound(null);
      }
      if (this.getStackInSlot(SLOT_OUT).isEmpty()) {
        this.setInventorySlotContents(SLOT_OUT, input);
        this.setInventorySlotContents(SLOT_INPUT, ItemStack.EMPTY);
      }
      //consume resources
    }
  }
}

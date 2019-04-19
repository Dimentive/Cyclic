package com.lothrazar.cyclicmagic.block.battery;

import java.util.List;
import com.lothrazar.cyclicmagic.capability.EnergyCapabilityItemStack;
import com.lothrazar.cyclicmagic.util.UtilChat;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlockBattery extends ItemBlock {

  public static final String ENERGY = "energy";
  public static final String MAX = "maxenergy";

  public ItemBlockBattery(Block block) {
    super(block);
  }

  @Override
  public boolean showDurabilityBar(ItemStack stack) {
    return true;
  }

  @Override
  public int getRGBDurabilityForDisplay(ItemStack stack) {
    return 0xBC000C;
  }

  /**
   * Queries the percentage of the 'Durability' bar that should be drawn.
   *
   * @param stack
   *          The current ItemStack
   * @return 0.0 for 100% (no damage / full bar), 1.0 for 0% (fully damaged / empty bar)
   */
  @Override
  public double getDurabilityForDisplay(ItemStack stack) {
    IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
    double energy = storage.getEnergyStored();
    return 1 - energy / storage.getMaxEnergyStored();
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void addInformation(ItemStack stack, World player, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced) {
    IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
    tooltip.add(storage.getEnergyStored() + "/" + storage.getMaxEnergyStored());
    tooltip.add(UtilChat.lang("tile.battery.tooltip"));
  }

  @Override
  public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
    //use getItem to detect max! 
    return new EnergyCapabilityItemStack(stack, BlockBattery.MAX_SMALL);
  }
  //ICapabilityProvider doesnt exist/notprovidded
  //oh well http://www.minecraftforge.net/forum/topic/54711-1102-forge-energy-capability-in-item-class/
  //  @Override
  //  public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
  //
  //    ICapabilityProvider superProvider = super.initCapabilities(stack, nbt);
  //    return new ICapabilityProvider() {
  //
  //      @Override
  //      public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
  //        return capability == CapabilityEnergy.ENERGY ? true : superProvider.hasCapability(capability, facing);
  //      }
  //
  //      @Override
  //      public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
  //        //NULL POINTER here so idk
  //        return capability == CapabilityEnergy.ENERGY ? (T) stack.getCapability(CapabilityEnergy.ENERGY, facing) : superProvider.getCapability(capability, facing);
  //      }
  //    };
  //  }
  //
  //  
}

package com.lothrazar.cyclicmagic.block.battery;

import java.util.ArrayList;
import java.util.List;
import com.lothrazar.cyclicmagic.IContent;
import com.lothrazar.cyclicmagic.block.core.BlockBaseHasTile;
import com.lothrazar.cyclicmagic.capability.EnergyStore;
import com.lothrazar.cyclicmagic.data.IHasRecipe;
import com.lothrazar.cyclicmagic.gui.ForgeGuiHandler;
import com.lothrazar.cyclicmagic.guide.GuideCategory;
import com.lothrazar.cyclicmagic.registry.BlockRegistry;
import com.lothrazar.cyclicmagic.registry.RecipeRegistry;
import com.lothrazar.cyclicmagic.util.Const;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockBattery extends BlockBaseHasTile implements IHasRecipe, IContent {

  public static final int MAX_SMALL = 1000000;
  //  public static final int MAX_MED = 16 * MAX_SMALL;
  //  public static final int MAX_LRG = 64 * MAX_SMALL;
  public static final PropertyEnum<EnergyFlatMap> AMOUNT = PropertyEnum.create("amount", EnergyFlatMap.class);
  private static final PropertyBool U = PropertyBool.create("u");
  private static final PropertyBool D = PropertyBool.create("d");
  private static final PropertyBool N = PropertyBool.create("n");
  private static final PropertyBool E = PropertyBool.create("e");
  private static final PropertyBool S = PropertyBool.create("s");
  private static final PropertyBool W = PropertyBool.create("w");

  public BlockBattery() {
    super(Material.ROCK);
    this.setGuiId(ForgeGuiHandler.GUI_INDEX_BATTERY);
  }

  @Override
  public String getContentName() {
    return "battery";
  }

  @Override
  public void register() {
    BlockRegistry.registerBlock(this, new ItemBlockBattery(this), getContentName(), GuideCategory.BLOCKMACHINE);
    GameRegistry.registerTileEntity(TileEntityBattery.class, Const.MODID + getContentName() + "_te");
  }

  private boolean enabled;

  @Override
  public boolean enabled() {
    return enabled;
  }

  @Override
  public void syncConfig(Configuration config) {
    enabled = config.getBoolean(getContentName(), Const.ConfigCategory.content, true, Const.ConfigCategory.contentDefaultText);
  }

  @Override
  public IRecipe addRecipe() {
    return RecipeRegistry.addShapedOreRecipe(new ItemStack(this),
        "cbc",
        "bab",
        "cbc",
        'c', Items.CLAY_BALL,
        'b', Blocks.GLASS,
        'a', "blockRedstone");
  }

  @Override
  public TileEntity createTileEntity(World worldIn, IBlockState state) {
    return new TileEntityBattery();
  }

  //start of 'fixing getDrops to not have null tile entity', using pattern from forge BlockFlowerPot patch
  @Override
  public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
    if (willHarvest) {
      return true;
    } //If it will harvest, delay deletion of the block until after getDrops
    return super.removedByPlayer(state, world, pos, player, willHarvest);
  }

  @Override
  public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack tool) {
    super.harvestBlock(world, player, pos, state, te, tool);
    world.setBlockToAir(pos);
  }
  //end of fixing getdrops

  @Override
  public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
    //?? TE null? http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/modification-development/2677315-solved-tileentity-returning-null
    //http://www.minecraftforge.net/forum/index.php?/topic/38048-19-solved-blockgetdrops-and-tileentity/
    List<ItemStack> ret = new ArrayList<ItemStack>();
    Item item = Item.getItemFromBlock(this);//this.getItemDropped(state, rand, fortune);
    TileEntity ent = world.getTileEntity(pos);
    ItemStack stack = new ItemStack(item);
    if (ent != null && ent.hasCapability(CapabilityEnergy.ENERGY, null)) {
      IEnergyStorage handlerHere = ent.getCapability(CapabilityEnergy.ENERGY, null);
      if (stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
        EnergyStore storage = (EnergyStore) stack.getCapability(CapabilityEnergy.ENERGY, null);
        storage.setEnergyStored(handlerHere.getEnergyStored());
      }
    }
    ret.add(stack);
    return ret;
  }

  @SuppressWarnings("deprecation")
  @Override
  public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
    TileEntity tile = world.getTileEntity(pos);
    if (tile instanceof TileEntityBattery) {
      TileEntityBattery battery = (TileEntityBattery) tile;
      state = state.withProperty(U, battery.getSideHasPower(EnumFacing.UP));
      state = state.withProperty(D, battery.getSideHasPower(EnumFacing.DOWN));
      state = state.withProperty(N, battery.getSideHasPower(EnumFacing.NORTH));
      state = state.withProperty(E, battery.getSideHasPower(EnumFacing.EAST));
      state = state.withProperty(S, battery.getSideHasPower(EnumFacing.SOUTH));
      state = state.withProperty(W, battery.getSideHasPower(EnumFacing.WEST));
      IEnergyStorage handlerHere = tile.getCapability(CapabilityEnergy.ENERGY, null);
      double percent = (double) handlerHere.getEnergyStored() / (double) handlerHere.getMaxEnergyStored();
      EnergyFlatMap p = EnergyFlatMap.AMOUNT_G0;
      double max = 16.0;
      if (percent == 0.0) {
        p = EnergyFlatMap.AMOUNT_G0;
      }
      else if (percent < 1.0 / max) {
        p = EnergyFlatMap.AMOUNT_G1;
      }
      else if (percent < 2.0 / max) {
        p = EnergyFlatMap.AMOUNT_G2;
      }
      else if (percent < 3.0 / max) {
        p = EnergyFlatMap.AMOUNT_G3;
      }
      else if (percent < 4.0 / max) {
        p = EnergyFlatMap.AMOUNT_G4;
      }
      else if (percent < 5.0 / max) {
        p = EnergyFlatMap.AMOUNT_G5;
      }
      else if (percent < 6.0 / max) {
        p = EnergyFlatMap.AMOUNT_G6;
      }
      else if (percent < 7.0 / max) {
        p = EnergyFlatMap.AMOUNT_G7;
      }
      else if (percent < 8.0 / max) {
        p = EnergyFlatMap.AMOUNT_G8;
      }
      else if (percent < 9.0 / max) {
        p = EnergyFlatMap.AMOUNT_G9;
      }
      else if (percent < 10.0 / max) {
        p = EnergyFlatMap.AMOUNT_G10;
      }
      else if (percent < 11.0 / max) {
        p = EnergyFlatMap.AMOUNT_G11;
      }
      else if (percent < 12.0 / max) {
        p = EnergyFlatMap.AMOUNT_G12;
      }
      else if (percent < 13.0 / max) {
        p = EnergyFlatMap.AMOUNT_G13;
      }
      else if (percent < 14.0 / max) {
        p = EnergyFlatMap.AMOUNT_G14;
      }
      else if (percent < 15.0 / max) {
        p = EnergyFlatMap.AMOUNT_G15;
      }
      else { //close enough to full 
        p = EnergyFlatMap.AMOUNT_G16;
      }
      //map [0-100] into [0-8]  
      //TODO: measuure tile energy level, and map to the G0-8
      return state.withProperty(AMOUNT, p);
    }
    return super.getActualState(state, world, pos);
  }

  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { AMOUNT, U, D, N, E, S, W });
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState();
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return 0;
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
    if (stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
      IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
      TileEntityBattery container = (TileEntityBattery) world.getTileEntity(pos);
      container.setEnergyCurrent(storage.getEnergyStored());
    }
    else if (stack.getTagCompound() != null && world.getTileEntity(pos) instanceof TileEntityBattery) {
      NBTTagCompound tags = stack.getTagCompound();
      int energy = tags.getInteger(ItemBlockBattery.ENERGY);
      TileEntityBattery container = (TileEntityBattery) world.getTileEntity(pos);
      container.setEnergyCurrent(energy);
    }
  }
}

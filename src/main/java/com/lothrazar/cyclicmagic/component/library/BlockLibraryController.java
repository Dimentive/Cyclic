package com.lothrazar.cyclicmagic.component.library;
import java.util.List;
import com.lothrazar.cyclicmagic.ModCyclic;
import com.lothrazar.cyclicmagic.block.base.BlockBase;
import com.lothrazar.cyclicmagic.util.UtilWorld;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockLibraryController extends BlockBase {
  private static final int RANGE = 4;
  Block libraryInstance;
  public BlockLibraryController(Block lib) {
    super(Material.WOOD);
    libraryInstance = lib;
  }
  @Override
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
    List<BlockPos> connectors = UtilWorld.getMatchingInRange(world, pos, libraryInstance, RANGE);
    TileEntity te;
    TileEntityLibrary lib;
    ItemStack playerHeld = player.getHeldItem(hand);
    ModCyclic.logger.log("found lib   " + connectors.size());
    for (BlockPos p : connectors) {
      te = world.getTileEntity(p);
      ModCyclic.logger.log("found test   " + p);
      if (te instanceof TileEntityLibrary) {
        lib = (TileEntityLibrary) te;
        ModCyclic.logger.log("found lib at " + p);
        QuadrantEnum quad = lib.findMatchingQuadrant(playerHeld);
        if (quad == null) {
          quad = lib.findEmptyQuadrant();
        }
        if (quad != null) {
          //now try insert here 
          if (lib.addEnchantmentFromPlayer(player, hand, quad)) {
            return true;
          }
          //          lib.addEnchantment(quad, ench, level)
        }
        //        if(world.getBlockState(p).getBlock().onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ))
        //        {
        //          return true;
        //        }
      }
    }
    return false;
  }
}

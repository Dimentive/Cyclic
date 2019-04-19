package com.lothrazar.cyclicmagic.block.buttondoorbell;

import java.util.List;
import com.lothrazar.cyclicmagic.IContent;
import com.lothrazar.cyclicmagic.data.IHasRecipe;
import com.lothrazar.cyclicmagic.guide.GuideCategory;
import com.lothrazar.cyclicmagic.registry.BlockRegistry;
import com.lothrazar.cyclicmagic.registry.RecipeRegistry;
import com.lothrazar.cyclicmagic.registry.SoundRegistry;
import com.lothrazar.cyclicmagic.util.Const;
import com.lothrazar.cyclicmagic.util.UtilChat;
import com.lothrazar.cyclicmagic.util.UtilSound;
import net.minecraft.block.BlockButton;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockDoorbell extends BlockButton implements IHasRecipe, IContent {

  private static final double SIXLRG = 0.6875D;
  private static final double THREELRG = 0.255D;
  private static final double THREE_SMALL = 0.3125D;
  private static final double SIX = 0.745D;
  protected static final AxisAlignedBB AABB_DOWN_OFF = new AxisAlignedBB(THREE_SMALL, 0.875D, THREELRG, SIXLRG, 1.0D, SIX);
  protected static final AxisAlignedBB AABB_UP_OFF = new AxisAlignedBB(THREE_SMALL, 0.0D, THREELRG, SIXLRG, 0.125D, SIX);
  protected static final AxisAlignedBB AABB_NORTH_OFF = new AxisAlignedBB(THREE_SMALL, THREELRG, 0.875D, SIXLRG, SIX, 1.0D);
  protected static final AxisAlignedBB AABB_SOUTH_OFF = new AxisAlignedBB(THREE_SMALL, THREELRG, 0.0D, SIXLRG, SIX, 0.125D);
  protected static final AxisAlignedBB AABB_WEST_OFF = new AxisAlignedBB(0.875D, THREELRG, THREE_SMALL, 1.0D, SIX, SIXLRG);
  protected static final AxisAlignedBB AABB_EAST_OFF = new AxisAlignedBB(0.0D, THREELRG, THREE_SMALL, 0.125D, SIX, SIXLRG);
  protected static final AxisAlignedBB AABB_DOWN_ON = new AxisAlignedBB(THREE_SMALL, 0.9375D, THREELRG, SIXLRG, 1.0D, SIX);
  protected static final AxisAlignedBB AABB_UP_ON = new AxisAlignedBB(THREE_SMALL, 0.0D, THREELRG, SIXLRG, 0.0625D, SIX);
  protected static final AxisAlignedBB AABB_NORTH_ON = new AxisAlignedBB(THREE_SMALL, THREELRG, 0.9375D, SIXLRG, SIX, 1.0D);
  protected static final AxisAlignedBB AABB_SOUTH_ON = new AxisAlignedBB(THREE_SMALL, THREELRG, 0.0D, SIXLRG, SIX, 0.0625D);
  protected static final AxisAlignedBB AABB_WEST_ON = new AxisAlignedBB(0.9375D, THREELRG, THREE_SMALL, 1.0D, SIX, SIXLRG);
  protected static final AxisAlignedBB AABB_EAST_ON = new AxisAlignedBB(0.0D, THREELRG, THREE_SMALL, 0.0625D, SIX, SIXLRG);

  public BlockDoorbell() {
    super(false);
  }

  @Override
  public void register() {
    BlockRegistry.registerBlock(this, getContentName(), GuideCategory.BLOCK);
  }

  private boolean enabled;

  @Override
  public boolean enabled() {
    return enabled;
  }

  @Override
  public String getContentName() {
    return "doorbell_simple";
  }

  @Override
  public void syncConfig(Configuration config) {
    enabled = config.getBoolean("doorbell", Const.ConfigCategory.content, true, Const.ConfigCategory.contentDefaultText);
  }

  @Override
  public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
    return 0;
  }

  @Override
  public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
    return 0;
  }

  @Override
  public boolean canProvidePower(IBlockState state) {
    return false;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack stack, World playerIn, List<String> tooltip, ITooltipFlag advanced) {
    tooltip.add(UtilChat.lang(this.getTranslationKey() + ".tooltip"));
  }

  @Override
  protected void playClickSound(EntityPlayer player, World worldIn, BlockPos pos) {
    UtilSound.playSound(player, pos, SoundRegistry.doorbell_mikekoenig, SoundCategory.BLOCKS, 0.5F);
  }

  @Override
  protected void playReleaseSound(World worldIn, BlockPos pos) {}

  @Override
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
    EnumFacing enumfacing = state.getValue(FACING);
    boolean flag = state.getValue(POWERED).booleanValue();
    switch (enumfacing) {
      case EAST:
        return flag ? AABB_EAST_ON : AABB_EAST_OFF;
      case WEST:
        return flag ? AABB_WEST_ON : AABB_WEST_OFF;
      case SOUTH:
        return flag ? AABB_SOUTH_ON : AABB_SOUTH_OFF;
      case NORTH:
      default:
        return flag ? AABB_NORTH_ON : AABB_NORTH_OFF;
      case UP:
        return flag ? AABB_UP_ON : AABB_UP_OFF;
      case DOWN:
        return flag ? AABB_DOWN_ON : AABB_DOWN_OFF;
    }
  }

  @Override
  public IRecipe addRecipe() {
    return RecipeRegistry.addShapedOreRecipe(new ItemStack(this),
        "b ",
        " n",
        'b', Blocks.WOODEN_BUTTON, 'n', Blocks.NOTEBLOCK);
  }
}

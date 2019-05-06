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
package com.lothrazar.cyclicmagic.block.uncrafter;

import com.lothrazar.cyclicmagic.IContent;
import com.lothrazar.cyclicmagic.block.core.BlockBaseFacingInventory;
import com.lothrazar.cyclicmagic.block.core.IBlockHasTESR;
import com.lothrazar.cyclicmagic.block.core.MachineTESR;
import com.lothrazar.cyclicmagic.data.IHasRecipe;
import com.lothrazar.cyclicmagic.gui.ForgeGuiHandler;
import com.lothrazar.cyclicmagic.guide.GuideCategory;
import com.lothrazar.cyclicmagic.registry.BlockRegistry;
import com.lothrazar.cyclicmagic.registry.RecipeRegistry;
import com.lothrazar.cyclicmagic.util.Const;
import com.lothrazar.cyclicmagic.util.UtilUncraft;
import com.lothrazar.cyclicmagic.util.UtilUncraft.BlacklistType;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockUncrafting extends BlockBaseFacingInventory implements IHasRecipe, IContent, IBlockHasTESR {

  // http://www.minecraftforge.net/forum/index.php?topic=31953.0
  public static int FUEL_COST = 0;

  public BlockUncrafting() {
    super(Material.IRON, ForgeGuiHandler.GUI_INDEX_UNCRAFTING);
    this.setHardness(3.0F).setResistance(5.0F);
    this.setSoundType(SoundType.METAL);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void initModel() {
    ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    // Bind our TESR to our tile entity
    ClientRegistry.bindTileEntitySpecialRenderer(TileEntityUncrafter.class, new MachineTESR(this, 0));
  }

  @Override
  public TileEntity createTileEntity(World worldIn, IBlockState state) {
    return new TileEntityUncrafter();
  }

  @Override
  public IRecipe addRecipe() {
    return RecipeRegistry.addShapedRecipe(new ItemStack(this),
        "d d",
        "frf",
        "ooo",
        'r', Blocks.DROPPER,
        'o', "obsidian",
        'f', Blocks.FURNACE,
        'd', "gemDiamond");
  }

  @Override
  public String getContentName() {
    return "uncrafting_block";
  }

  @Override
  public void register() {
    BlockRegistry.registerBlock(this, getContentName(), GuideCategory.BLOCKMACHINE);
    BlockRegistry.registerTileEntity(TileEntityUncrafter.class, getContentName() + "_te");
  }

  private boolean enabled;

  @Override
  public boolean enabled() {
    return enabled;
  }

  @Override
  public void syncConfig(Configuration config) {
    enabled = config.getBoolean("UncraftingGrinder", Const.ConfigCategory.content, true, Const.ConfigCategory.contentDefaultText);
    TileEntityUncrafter.TIMER_FULL = config.getInt(getContentName(), Const.ConfigCategory.machineTimer,
        150, 1, 9000, Const.ConfigText.machineTimer);
    FUEL_COST = config.getInt(getContentName(), Const.ConfigCategory.fuelCost, 200, 0, 500000, Const.ConfigText.fuelCost);
    String category = Const.ConfigCategory.uncrafter;
    UtilUncraft.dictionaryFreedom = config.getBoolean("PickFirstMeta", category, true, "If you change this to true, then the uncrafting will just take the first of many options in any recipe that takes multiple input types.  For example, false means chests cannot be uncrafted, but true means chests will ALWAYS give oak wooden planks.");
    UtilUncraft.resetBlacklists();
    config.addCustomCategoryComment(category, "Blacklists and other tweaks for the Uncrafting Grinder.   (Use F3+H to see the details, it is always 'modid:item')");
    //INPUT
    String[] deflist = new String[] {
        "minecraft:end_crystal",
        "minecraft:magma",
        "minecraft:elytra",
        "forge:bucketfilled",
        "astralsorcery:itemwand",
        "progressiveautomation:WitherDiamond",
        "progressiveautomation:WitherGold",
        "progressiveautomation:WitherIron",
        "progressiveautomation:WitherStone",
        "progressiveautomation:WitherWood",
        "techreborn:uumatter",
        "spectrite:spectrite_arrow",
        "spectrite:spectrite_arrow_special"
    };
    String[] blacklist = config.getStringList("BlacklistInput", category, deflist, "Items that cannot be uncrafted.  ");
    UtilUncraft.setBlacklist(blacklist, BlacklistType.INPUT);
    //OUTPUT
    deflist = new String[] {
        "minecraft:milk_bucket",
        "minecraft:water_bucket",
        "minecraft:lava_bucket",
        "minecraft:nether_star",
        "minecraft:elytra",
        "forge:bucketfilled",
        "abyssalcraft:cloth",
        "abyssalcraft:transmutationgem",
        "botania:manatablet",
        "extrautils2:glasscutter",
        "harvestcraft:juiceritem",
        "harvestcraft:mixingbowlitem",
        "harvestcraft:mortarandpestleitem",
        "harvestcraft:bakewareitem",
        "harvestcraft:saucepanitem",
        "harvestcraft:skilletitem",
        "harvestcraft:potitem",
        "harvestcraft:cuttingboarditem",
        "ic2:forge_hammer",
        "ic2:cutter",
        "minecolonies:sceptergold",
        "mysticalagriculture:infusion_crystal",
        "mysticalagriculture:master_infusion_crystal",
        "techreborn:uumatter"
    };
    blacklist = config.getStringList("BlacklistOutput", category, deflist, "Items that cannot come out of crafting recipes.  For example, if milk is in here, then cake can be uncrafted, but you get all items except the milk buckets.  ");
    UtilUncraft.setBlacklist(blacklist, BlacklistType.OUTPUT);
    //MODNAME
    deflist = new String[] {
        "projecte",
        "flammpfeil.slashblade",
        "resourcefulcrops",
        "spectrite"
    };
    blacklist = config.getStringList("BlacklistMod", category, deflist, "If a mod id is in this list, then nothing from that mod will be uncrafted ");
    UtilUncraft.setBlacklist(blacklist, BlacklistType.MODNAME);
    //CONTAINS
    //    deflist = new String[] { "botania:manaTablet","projecte:pe_philosophers_stone" };//bot mana tablet
    //    blacklist = config.getStringList("BlacklistIfIngredient", category, deflist, "If something contains one of these items as output, uncrafting will be blocked.  For example, if you put 'minecraft:iron_ingot' here, you will not be able to uncraft pistons or iron swords or anything that uses iron at all.");
    //    UtilUncraft.setBlacklist(blacklist, BlacklistType.CONTAINS);
  }
}

package com.lothrazar.cyclicmagic;

import java.util.ArrayList;
import com.lothrazar.cyclicmagic.block.BlockDarknessGlass;
import com.lothrazar.cyclicmagic.block.BlockShears;
import com.lothrazar.cyclicmagic.block.BlockSoundSuppress;
import com.lothrazar.cyclicmagic.block.anvil.BlockAnvilAuto;
import com.lothrazar.cyclicmagic.block.anvilmagma.BlockAnvilMagma;
import com.lothrazar.cyclicmagic.block.anvilvoid.BlockVoidAnvil;
import com.lothrazar.cyclicmagic.block.applesprout.BlockAppleCrop;
import com.lothrazar.cyclicmagic.block.arrowtarget.BlockArrowTarget;
import com.lothrazar.cyclicmagic.block.autouser.BlockUser;
import com.lothrazar.cyclicmagic.block.battery.BlockBattery;
import com.lothrazar.cyclicmagic.block.batterycheat.BlockBatteryInfinite;
import com.lothrazar.cyclicmagic.block.beaconempty.BlockBeaconPowered;
import com.lothrazar.cyclicmagic.block.beaconpotion.BlockBeaconPotion;
import com.lothrazar.cyclicmagic.block.bean.BlockCropMagicBean;
import com.lothrazar.cyclicmagic.block.builderpattern.BlockPatternBuilder;
import com.lothrazar.cyclicmagic.block.buildershape.BlockStructureBuilder;
import com.lothrazar.cyclicmagic.block.buildplacer.BlockPlacer;
import com.lothrazar.cyclicmagic.block.buttondoorbell.BlockDoorbell;
import com.lothrazar.cyclicmagic.block.buttonflat.BlockButtonLarge;
import com.lothrazar.cyclicmagic.block.clockredstone.BlockRedstoneClock;
import com.lothrazar.cyclicmagic.block.collector.BlockVacuum;
import com.lothrazar.cyclicmagic.block.controlledminer.BlockMinerSmart;
import com.lothrazar.cyclicmagic.block.crafter.BlockCrafter;
import com.lothrazar.cyclicmagic.block.creativeduper.BlockCreativeItem;
import com.lothrazar.cyclicmagic.block.dehydrator.BlockDeHydrator;
import com.lothrazar.cyclicmagic.block.dice.BlockDice;
import com.lothrazar.cyclicmagic.block.disenchanter.BlockDisenchanter;
import com.lothrazar.cyclicmagic.block.dropper.BlockDropperExact;
import com.lothrazar.cyclicmagic.block.enchanter.BlockEnchanter;
import com.lothrazar.cyclicmagic.block.enchantlibrary.shelf.BlockLibrary;
import com.lothrazar.cyclicmagic.block.entitydetector.BlockDetector;
import com.lothrazar.cyclicmagic.block.exppylon.BlockXpPylon;
import com.lothrazar.cyclicmagic.block.fan.BlockFan;
import com.lothrazar.cyclicmagic.block.fishing.BlockFishing;
import com.lothrazar.cyclicmagic.block.fluiddrain.BlockFluidDrain;
import com.lothrazar.cyclicmagic.block.fluidplacer.BlockFluidPlacer;
import com.lothrazar.cyclicmagic.block.forester.BlockForester;
import com.lothrazar.cyclicmagic.block.harvester.BlockHarvester;
import com.lothrazar.cyclicmagic.block.hydrator.BlockHydrator;
import com.lothrazar.cyclicmagic.block.imbue.BlockImbue;
import com.lothrazar.cyclicmagic.block.interdiction.BlockMagnetAnti;
import com.lothrazar.cyclicmagic.block.laser.BlockLaser;
import com.lothrazar.cyclicmagic.block.magnetitem.BlockMagnet;
import com.lothrazar.cyclicmagic.block.melter.BlockMelter;
import com.lothrazar.cyclicmagic.block.miner.BlockMiner;
import com.lothrazar.cyclicmagic.block.moondetector.BlockMoonDetector;
import com.lothrazar.cyclicmagic.block.packager.BlockPackager;
import com.lothrazar.cyclicmagic.block.password.BlockPassword;
import com.lothrazar.cyclicmagic.block.screentarget.BlockScreenTarget;
import com.lothrazar.cyclicmagic.block.screentype.BlockScreen;
import com.lothrazar.cyclicmagic.block.solidifier.BlockSolidifier;
import com.lothrazar.cyclicmagic.block.sound.BlockSoundPlayer;
import com.lothrazar.cyclicmagic.block.sprinkler.BlockSprinkler;
import com.lothrazar.cyclicmagic.block.tank.BlockFluidTank;
import com.lothrazar.cyclicmagic.block.trash.BlockTrash;
import com.lothrazar.cyclicmagic.block.uncrafter.BlockUncrafting;
import com.lothrazar.cyclicmagic.block.vector.BlockVectorPlate;
import com.lothrazar.cyclicmagic.block.watercandle.BlockWaterCandle;
import com.lothrazar.cyclicmagic.block.workbench.BlockWorkbench;
import com.lothrazar.cyclicmagic.enchant.EnchantAutoSmelt;
import com.lothrazar.cyclicmagic.enchant.EnchantBeheading;
import com.lothrazar.cyclicmagic.enchant.EnchantExcavation;
import com.lothrazar.cyclicmagic.enchant.EnchantLaunch;
import com.lothrazar.cyclicmagic.enchant.EnchantLifeLeech;
import com.lothrazar.cyclicmagic.enchant.EnchantMagnet;
import com.lothrazar.cyclicmagic.enchant.EnchantMultishot;
import com.lothrazar.cyclicmagic.enchant.EnchantQuickdraw;
import com.lothrazar.cyclicmagic.enchant.EnchantReach;
import com.lothrazar.cyclicmagic.enchant.EnchantVenom;
import com.lothrazar.cyclicmagic.enchant.EnchantWaterwalking;
import com.lothrazar.cyclicmagic.enchant.EnchantXpBoost;
import com.lothrazar.cyclicmagic.item.DynamiteContent;
import com.lothrazar.cyclicmagic.item.ItemAppleStep;
import com.lothrazar.cyclicmagic.item.ItemCaveFinder;
import com.lothrazar.cyclicmagic.item.ItemCraftingUnlock;
import com.lothrazar.cyclicmagic.item.ItemEnderBag;
import com.lothrazar.cyclicmagic.item.ItemEnderWing;
import com.lothrazar.cyclicmagic.item.ItemEvokerFangs;
import com.lothrazar.cyclicmagic.item.ItemFireExtinguish;
import com.lothrazar.cyclicmagic.item.ItemFlight;
import com.lothrazar.cyclicmagic.item.ItemGemAmber;
import com.lothrazar.cyclicmagic.item.ItemGemObsidian;
import com.lothrazar.cyclicmagic.item.ItemIceWand;
import com.lothrazar.cyclicmagic.item.ItemInventoryUnlock;
import com.lothrazar.cyclicmagic.item.ItemLeverRemote;
import com.lothrazar.cyclicmagic.item.ItemMattock;
import com.lothrazar.cyclicmagic.item.ItemNoclipGhost;
import com.lothrazar.cyclicmagic.item.ItemObsShears;
import com.lothrazar.cyclicmagic.item.ItemPaperCarbon;
import com.lothrazar.cyclicmagic.item.ItemPistonWand;
import com.lothrazar.cyclicmagic.item.ItemPlayerLauncher;
import com.lothrazar.cyclicmagic.item.ItemPotionContent;
import com.lothrazar.cyclicmagic.item.ItemProspector;
import com.lothrazar.cyclicmagic.item.ItemRotateBlock;
import com.lothrazar.cyclicmagic.item.ItemSpawnInspect;
import com.lothrazar.cyclicmagic.item.ItemStirrups;
import com.lothrazar.cyclicmagic.item.ItemStirrupsReverse;
import com.lothrazar.cyclicmagic.item.ItemWandHypno;
import com.lothrazar.cyclicmagic.item.ItemWarpSurface;
import com.lothrazar.cyclicmagic.item.ItemWaterRemoval;
import com.lothrazar.cyclicmagic.item.ItemWaterSpreader;
import com.lothrazar.cyclicmagic.item.SoulstoneItem;
import com.lothrazar.cyclicmagic.item.boomerang.ItemBoomerang;
import com.lothrazar.cyclicmagic.item.cannon.ItemProjectileCannon;
import com.lothrazar.cyclicmagic.item.crashtestdummy.ItemCrashSpawner;
import com.lothrazar.cyclicmagic.item.cyclicwand.ItemCyclicWand;
import com.lothrazar.cyclicmagic.item.enderbook.ItemEnderBook;
import com.lothrazar.cyclicmagic.item.endereye.ItemEnderEyeReuse;
import com.lothrazar.cyclicmagic.item.equipbauble.ItemAutoTorch;
import com.lothrazar.cyclicmagic.item.equipbauble.ItemCharmAir;
import com.lothrazar.cyclicmagic.item.equipbauble.ItemCharmAntidote;
import com.lothrazar.cyclicmagic.item.equipbauble.ItemCharmBoat;
import com.lothrazar.cyclicmagic.item.equipbauble.ItemCharmFire;
import com.lothrazar.cyclicmagic.item.equipbauble.ItemCharmSlowfall;
import com.lothrazar.cyclicmagic.item.equipbauble.ItemCharmSpeed;
import com.lothrazar.cyclicmagic.item.equipbauble.ItemCharmVoid;
import com.lothrazar.cyclicmagic.item.equipbauble.ItemCharmWater;
import com.lothrazar.cyclicmagic.item.equipbauble.ItemGloveClimb;
import com.lothrazar.cyclicmagic.item.findspawner.ItemProjectileDungeon;
import com.lothrazar.cyclicmagic.item.homingmissile.ItemMagicMissile;
import com.lothrazar.cyclicmagic.item.lightningmagic.ItemProjectileLightning;
import com.lothrazar.cyclicmagic.item.locationgps.ItemLocationGps;
import com.lothrazar.cyclicmagic.item.merchant.ItemMerchantAlmanac;
import com.lothrazar.cyclicmagic.item.mobcapture.ItemProjectileMagicNet;
import com.lothrazar.cyclicmagic.item.mobs.ItemHorseTame;
import com.lothrazar.cyclicmagic.item.mobs.ItemVillagerMagic;
import com.lothrazar.cyclicmagic.item.random.ItemRandomizer;
import com.lothrazar.cyclicmagic.item.shears.ItemShearsRanged;
import com.lothrazar.cyclicmagic.item.signcolor.ItemSignEditor;
import com.lothrazar.cyclicmagic.item.sleep.ItemSleepingMat;
import com.lothrazar.cyclicmagic.item.slingshot.ItemProjectileSlingshot;
import com.lothrazar.cyclicmagic.item.snowmagic.ItemProjectileSnow;
import com.lothrazar.cyclicmagic.item.torchmagic.ItemProjectileTorch;
import com.lothrazar.cyclicmagic.item.torchmagic.ItemTorchThrower;
import com.lothrazar.cyclicmagic.liquid.amber.FluidAmber;
import com.lothrazar.cyclicmagic.liquid.biomass.FluidBiomass;
import com.lothrazar.cyclicmagic.liquid.crystal.FluidCrystal;
import com.lothrazar.cyclicmagic.liquid.milk.FluidMilk;
import com.lothrazar.cyclicmagic.liquid.poison.FluidPoison;
import com.lothrazar.cyclicmagic.module.MultiContent;
import com.lothrazar.cyclicmagic.registry.ConfigRegistry;
import net.minecraft.init.Blocks;

public class CyclicContent {

  private static ArrayList<IContent> content;
  public static BlockHydrator hydrator;
  public static BlockPackager packager;
  public static BlockDeHydrator dehydrator;
  public static BlockSolidifier solidifier;
  public static BlockMelter melter;

  public static void init() {
    content = new ArrayList<IContent>();
    content.add(new BlockFluidDrain());
    content.add(new ItemGemAmber());
    content.add(new ItemGemObsidian());
    content.add(new ItemPotionContent());
    content.add(new MultiContent());
    content.add(new ItemLocationGps());
    content.add(new BlockLibrary());
    content.add(new FluidPoison());
    content.add(new FluidAmber());
    content.add(new FluidBiomass());
    content.add(new FluidCrystal());
    content.add(new FluidMilk());
    content.add(new EnchantAutoSmelt());
    content.add(new EnchantBeheading());
    content.add(new EnchantExcavation());
    content.add(new EnchantLaunch());
    content.add(new EnchantLifeLeech());
    content.add(new EnchantMagnet());
    content.add(new EnchantMultishot());
    content.add(new EnchantQuickdraw());
    content.add(new EnchantReach());
    content.add(new EnchantVenom());
    content.add(new EnchantWaterwalking());
    content.add(new EnchantXpBoost());
    content.add(new ItemCrashSpawner());
    content.add(new ItemProjectileCannon());
    content.add(new ItemMerchantAlmanac());
    content.add(new SoulstoneItem());
    content.add(new ItemPlayerLauncher());
    content.add(new ItemLeverRemote());
    content.add(new ItemWarpSurface());
    content.add(new ItemRotateBlock());
    content.add(new ItemEvokerFangs());
    content.add(new ItemWaterSpreader());
    content.add(new ItemEnderEyeReuse());
    content.add(new ItemGloveClimb());
    content.add(new ItemTorchThrower());
    content.add(new ItemIceWand());
    content.add(new ItemFireExtinguish());
    content.add(new ItemEnderBag());
    content.add(new ItemStirrups());
    content.add(new ItemProspector());
    content.add(new ItemEnderBook());
    content.add(new ItemPaperCarbon());
    content.add(new ItemCaveFinder());
    content.add(new ItemProjectileDungeon());
    content.add(new ItemProjectileLightning());
    content.add(new ItemCyclicWand());
    content.add(new ItemObsShears());
    content.add(new ItemEnderWing(ItemEnderWing.WarpType.BED));
    content.add(new ItemEnderWing(ItemEnderWing.WarpType.SPAWN));
    content.add(new ItemPistonWand());
    content.add(new ItemSleepingMat());
    content.add(new ItemSpawnInspect());
    content.add(new ItemMattock());
    content.add(new ItemWandHypno());
    content.add(new ItemRandomizer());
    content.add(new DynamiteContent());
    content.add(new ItemShearsRanged());
    content.add(new ItemProjectileTorch());
    content.add(new ItemProjectileMagicNet());
    content.add(new ItemVillagerMagic());
    content.add(new ItemNoclipGhost());
    content.add(new ItemFlight());
    content.add(new ItemProjectileSnow());
    content.add(new ItemWaterRemoval());
    content.add(new ItemAutoTorch());
    content.add(new ItemInventoryUnlock());
    content.add(new ItemCraftingUnlock());
    content.add(new ItemCharmAntidote());
    content.add(new ItemMagicMissile());
    content.add(new ItemCharmWater());
    content.add(new ItemSignEditor());
    content.add(new ItemCharmAir());
    content.add(new ItemCharmVoid());
    content.add(new ItemCharmBoat());
    content.add(new ItemCharmFire());
    content.add(new ItemCharmSpeed());
    content.add(new ItemCharmSlowfall());
    content.add(new ItemAppleStep());
    content.add(new ItemStirrupsReverse());
    content.add(new ItemHorseTame());
    content.add(new ItemBoomerang());
    content.add(new ItemProjectileSlingshot());
    content.add(new BlockVectorPlate());
    content.add(new BlockPatternBuilder());
    content.add(new BlockMagnet());
    content.add(new BlockMagnetAnti());
    content.add(new BlockDoorbell());
    dehydrator = new BlockDeHydrator();
    content.add(dehydrator);
    content.add(new BlockVacuum());
    content.add(new BlockBeaconPotion());
    content.add(new BlockButtonLarge());
    content.add(new BlockMoonDetector());
    content.add(new BlockRedstoneClock());
    content.add(new BlockCropMagicBean());
    content.add(new BlockArrowTarget());
    content.add(new BlockAppleCrop());
    content.add(new BlockBeaconPowered());
    content.add(new BlockSprinkler());
    content.add(new BlockForester());
    packager = new BlockPackager();
    content.add(packager);
    content.add(new BlockMiner());
    hydrator = new BlockHydrator();
    content.add(hydrator);
    content.add(new BlockAnvilMagma());
    melter = new BlockMelter();
    content.add(melter);
    solidifier = new BlockSolidifier();
    content.add(solidifier);
    content.add(new BlockAnvilAuto(Blocks.ENCHANTING_TABLE));
    content.add(new BlockHarvester());
    content.add(new BlockEnchanter());
    content.add(new BlockXpPylon());
    content.add(new BlockStructureBuilder());
    content.add(new BlockUncrafting());
    content.add(new BlockMinerSmart());
    content.add(new BlockPlacer());
    content.add(new BlockPassword());
    content.add(new BlockUser());
    content.add(new BlockBatteryInfinite());
    content.add(new BlockBattery());
    //    content.add(new BlockBatteryCell(BlockBattery.MAX_LRG));
    content.add(new BlockTrash());
    content.add(new BlockWorkbench());
    content.add(new BlockScreen());
    content.add(new BlockScreenTarget());
    content.add(new BlockDarknessGlass());
    content.add(new BlockSoundSuppress());
    content.add(new BlockCrafter());
    content.add(new BlockDetector());
    content.add(new BlockFan());
    content.add(new BlockShears());
    content.add(new BlockFishing());
    content.add(new BlockDisenchanter());
    content.add(new BlockFluidTank());
    content.add(new BlockLaser());
    content.add(new BlockSoundPlayer());
    content.add(new BlockDice());
    content.add(new BlockImbue());
    content.add(new BlockVoidAnvil());
    content.add(new BlockDropperExact());
    content.add(new BlockFluidPlacer());
    content.add(new BlockWaterCandle());
    content.add(new BlockCreativeItem());
    for (IContent cont : content) {
      ConfigRegistry.register(cont);
    }
  }

  public static void register() {
    for (IContent cont : content) {
      if (cont.enabled()) {
        cont.register();
      }
    }
  }
}

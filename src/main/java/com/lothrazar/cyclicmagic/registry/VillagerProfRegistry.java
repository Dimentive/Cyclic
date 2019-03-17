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
package com.lothrazar.cyclicmagic.registry;

import java.util.ArrayList;
import com.lothrazar.cyclicmagic.util.Const;
import com.lothrazar.cyclicmagic.villager.druid.VillageCreationHandlerDruid;
import com.lothrazar.cyclicmagic.villager.druid.VillageStructureDruid;
import com.lothrazar.cyclicmagic.villager.druid.VillagerDruid;
import com.lothrazar.cyclicmagic.villager.sage.VillageCreationHandlerSage;
import com.lothrazar.cyclicmagic.villager.sage.VillageStructureSage;
import com.lothrazar.cyclicmagic.villager.sage.VillagerSage;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

public class VillagerProfRegistry {

  public static VillagerProfession DRUID;
  public static VillagerProfession SAGE;
  public static ArrayList<VillagerProfession> villagers = new ArrayList<VillagerProfession>();

  public static void register(VillagerProfession prof) {
    villagers.add(prof);
  }

  @SubscribeEvent
  public static void onRegistryEvent(RegistryEvent.Register<VillagerProfession> event) {
    for (VillagerProfession b : villagers) {
      event.getRegistry().register(b);
    }
    if (VillagerProfRegistry.DRUID != null) {
      VillagerRegistry.instance().registerVillageCreationHandler(new VillageCreationHandlerDruid());
      MapGenStructureIO.registerStructureComponent(VillageStructureDruid.class, Const.MODRES + VillagerDruid.NAME);
    }
    if (VillagerProfRegistry.SAGE != null) {
      VillagerRegistry.instance().registerVillageCreationHandler(new VillageCreationHandlerSage());
      MapGenStructureIO.registerStructureComponent(VillageStructureSage.class, Const.MODRES + VillagerSage.NAME);
    }
  }
}

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
package com.lothrazar.cyclicmagic.block.packager;

import com.lothrazar.cyclicmagic.gui.GuiBaseContainer;
import com.lothrazar.cyclicmagic.gui.component.EnergyBar;
import com.lothrazar.cyclicmagic.util.Const;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiPackager extends GuiBaseContainer {

  public GuiPackager(InventoryPlayer inventoryPlayer, TileEntityPackager tileEntity) {
    super(new ContainerPackager(inventoryPlayer, tileEntity), tileEntity);
    this.fieldRedstoneBtn = TileEntityPackager.Fields.REDSTONE.ordinal();
    this.energyBar = new EnergyBar(this);
    energyBar.setX(this.xSize / 2 - 8).setY(16).setWidth(14);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    int u = 0, v = 0;
    this.mc.getTextureManager().bindTexture(Const.Res.SLOT);
    int x = this.guiLeft + ContainerPackager.SLOTX_START - 1;
    int y = this.guiTop + ContainerPackager.SLOTY - 1;
    int ROWS = 3;
    for (int k = 0; k < TileEntityPackager.INPUT_SIZE; k++) {
      Gui.drawModalRectWithCustomSizedTexture(
          x + k % ROWS * Const.SQ,
          y + k / ROWS * Const.SQ,
          u, v, Const.SQ, Const.SQ, Const.SQ, Const.SQ);
    }
    x = this.guiLeft + ContainerPackager.MID_SPACING;
    y = this.guiTop + ContainerPackager.SLOTY - 1;
    for (int k = 0; k < TileEntityPackager.OUTPUT_SIZE; k++) {
      Gui.drawModalRectWithCustomSizedTexture(
          x + k % ROWS * Const.SQ,
          y + k / ROWS * Const.SQ,
          u, v, Const.SQ, Const.SQ, Const.SQ, Const.SQ);
    }
  }
}

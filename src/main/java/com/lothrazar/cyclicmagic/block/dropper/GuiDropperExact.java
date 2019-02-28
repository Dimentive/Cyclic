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
package com.lothrazar.cyclicmagic.block.dropper;

import java.io.IOException;
import org.lwjgl.input.Keyboard;
import com.lothrazar.cyclicmagic.gui.EnergyBar;
import com.lothrazar.cyclicmagic.gui.GuiSliderInteger;
import com.lothrazar.cyclicmagic.gui.core.GuiBaseContainer;
import com.lothrazar.cyclicmagic.util.Const;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiDropperExact extends GuiBaseContainer {

  private GuiSliderInteger sliderDelay;
  private GuiSliderInteger sliderOffset;
  private GuiSliderInteger sliderCount;

  public GuiDropperExact(InventoryPlayer inventoryPlayer, TileEntityDropperExact tileEntity) {
    super(new ContainerDropperExact(inventoryPlayer, tileEntity));
    tile = tileEntity;
    this.fieldRedstoneBtn = TileEntityDropperExact.Fields.REDSTONE.ordinal();
    this.energyBar = new EnergyBar(this);
    energyBar.setX(156);
  }

  @Override
  public void initGui() {
    super.initGui();
    Keyboard.enableRepeatEvents(true);
    // buttons!  
    int id = 1;
    int width = 90;
    int h = 10;
    int x = this.guiLeft + 6;
    int y = this.guiTop + 28;
    sliderDelay = new GuiSliderInteger(tile, id++, x, y, width, h, 0, 64,
        TileEntityDropperExact.Fields.DELAY.ordinal());
    sliderDelay.setTooltip("dropper.delay");
    this.addButton(sliderDelay);
    y += 18;
    //offset
    sliderOffset = new GuiSliderInteger(tile, id++, x, y, width, h, 0, 16,
        TileEntityDropperExact.Fields.OFFSET.ordinal());
    sliderOffset.setTooltip("dropper.offset");
    this.addButton(sliderOffset);
    y += 18;
    //stack size
    sliderCount = new GuiSliderInteger(tile, id++, x, y, width, h, 1, 64,
        TileEntityDropperExact.Fields.DROPCOUNT.ordinal());
    sliderCount.setTooltip("dropper.count");
    this.addButton(sliderCount);
  }

  @Override
  public void onGuiClosed() {
    Keyboard.enableRepeatEvents(false);
  }

  @Override
  protected void keyTyped(char typedChar, int keyCode) throws IOException {
    super.keyTyped(typedChar, keyCode);
    sliderCount.keyTyped(typedChar, keyCode);
    sliderDelay.keyTyped(typedChar, keyCode);
    sliderOffset.keyTyped(typedChar, keyCode);
  }

  @Override
  public void updateScreen() {
    super.updateScreen();
    sliderCount.updateScreen();
    sliderDelay.updateScreen();
    sliderOffset.updateScreen();
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    int u = 0, v = 0;
    this.mc.getTextureManager().bindTexture(Const.Res.SLOT);
    int xPrefix, yPrefix;
    int rows = 3, cols = 3;
    xPrefix = ContainerDropperExact.SLOTX_START;
    yPrefix = ContainerDropperExact.SLOTY;
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        Gui.drawModalRectWithCustomSizedTexture(this.guiLeft + xPrefix - 1 + j * Const.SQ,
            this.guiTop + yPrefix - 1 + i * Const.SQ, u, v, Const.SQ, Const.SQ, Const.SQ, Const.SQ);
      }
    }
  }
}

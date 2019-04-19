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
package com.lothrazar.cyclicmagic.block.forester;

import java.io.IOException;
import org.lwjgl.input.Keyboard;
import com.lothrazar.cyclicmagic.gui.button.ButtonTileEntityField;
import com.lothrazar.cyclicmagic.gui.component.EnergyBar;
import com.lothrazar.cyclicmagic.gui.component.GuiSliderInteger;
import com.lothrazar.cyclicmagic.gui.container.GuiBaseContainer;
import com.lothrazar.cyclicmagic.util.Const;
import com.lothrazar.cyclicmagic.util.Const.ScreenSize;
import com.lothrazar.cyclicmagic.util.UtilChat;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiForester extends GuiBaseContainer {

  private ButtonTileEntityField btnSize;
  private GuiSliderInteger slider;

  public GuiForester(InventoryPlayer inventoryPlayer, TileEntityForester tileEntity) {
    super(new ContainerForester(inventoryPlayer, tileEntity), tileEntity);
    setScreenSize(ScreenSize.LARGE);
    this.fieldRedstoneBtn = TileEntityForester.Fields.REDSTONE.ordinal();
    this.fieldPreviewBtn = TileEntityForester.Fields.RENDERPARTICLES.ordinal();
    this.energyBar = new EnergyBar(this);
    energyBar.setWidth(16).setX(150);
  }

  @Override
  public void initGui() {
    super.initGui();
    Keyboard.enableRepeatEvents(true);
    int id = 3, x, y;
    x = this.guiLeft + xSize / 4 + 22;
    y = this.guiTop + 34;
    btnSize = new ButtonTileEntityField(id++,
        x, y, this.tile.getPos(), TileEntityForester.Fields.SIZE.ordinal());
    btnSize.width = 44;
    btnSize.setTooltip("button.size.tooltip");
    this.addButton(btnSize);
    this.leftClickers.add(btnSize);
    x = this.guiLeft + xSize / 4 - 2;
    y = this.guiTop + 18;
    slider = new GuiSliderInteger(tile, id++, x, y, 100, 10, 1, TileEntityForester.MAX_HEIGHT,
        TileEntityForester.Fields.HEIGHT.ordinal());
    slider.setTooltip("button.miner.height");
    this.addButton(slider);
  }

  @Override
  public void onGuiClosed() {
    Keyboard.enableRepeatEvents(false);
  }

  @Override
  protected void keyTyped(char typedChar, int keyCode) throws IOException {
    super.keyTyped(typedChar, keyCode);
    slider.keyTyped(typedChar, keyCode);
  }

  @Override
  public void updateScreen() {
    super.updateScreen();
    slider.updateScreen();
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    int u = 0, v = 0;
    this.mc.getTextureManager().bindTexture(Const.Res.SLOT_SAPLING);
    int rowsize = 6;
    for (int k = 0; k < tile.getSizeInventory(); k++) {
      Gui.drawModalRectWithCustomSizedTexture(
          this.guiLeft + ContainerForester.SLOTX_START - 1 + (k % rowsize) * Const.SQ + Const.SQ,
          this.guiTop + ContainerForester.SLOTY - 1 + (k / rowsize) * Const.SQ,
          u, v, Const.SQ, Const.SQ, Const.SQ, Const.SQ);
    }
  }

  @SideOnly(Side.CLIENT)
  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    btnSize.displayString = UtilChat.lang("button.harvester.size" + tile.getField(TileEntityForester.Fields.SIZE.ordinal()));
  }
}

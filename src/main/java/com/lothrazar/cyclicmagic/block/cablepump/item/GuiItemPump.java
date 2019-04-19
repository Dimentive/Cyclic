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
package com.lothrazar.cyclicmagic.block.cablepump.item;

import com.lothrazar.cyclicmagic.data.ITileStackWrapper;
import com.lothrazar.cyclicmagic.gui.button.ButtonTileEntityField;
import com.lothrazar.cyclicmagic.gui.container.GuiBaseContainer;
import com.lothrazar.cyclicmagic.gui.container.StackWrapper;
import com.lothrazar.cyclicmagic.util.Const;
import com.lothrazar.cyclicmagic.util.UtilChat;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiItemPump extends GuiBaseContainer {

  ITileStackWrapper te;
  private ButtonTileEntityField filterBtn;

  public GuiItemPump(InventoryPlayer inventoryPlayer, TileEntityItemPump tileEntity) {
    super(new ContainerItemPump(inventoryPlayer, tileEntity), tileEntity);
    te = tileEntity;
    this.fieldRedstoneBtn = TileEntityItemPump.Fields.REDSTONE.ordinal();
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    int x, y, slotNum = 0;
    this.mc.getTextureManager().bindTexture(Const.Res.SLOT);
    for (int j = 1; j < 10; j++) {
      x = this.guiLeft + ContainerItemPump.SLOTX_START + (j - 1) * Const.SQ - 1;
      y = this.guiTop + ContainerItemPump.SLOTY - 1;
      StackWrapper wrap = te.getStackWrapper(slotNum);
      wrap.setX(x);
      wrap.setY(y);
      slotNum++;
    }
    this.renderStackWrappers(te);
  }

  @SideOnly(Side.CLIENT)
  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    int filterType = tile.getField(TileEntityItemPump.Fields.FILTERTYPE.ordinal());
    //    filterBtn.displayString = UtilChat.lang("button.itemfilter.type" + filterType);
    filterBtn.setTooltip(UtilChat.lang("button.itemfilter.tooltip.type" + filterType));
    filterBtn.setTextureIndex(11 + filterType);
  }

  /**
   * mouseover render for fake slots
   */
  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    super.drawScreen(mouseX, mouseY, partialTicks);
    //if (isPointInRegion(wrap.getX() - guiLeft, wrap.getY() - guiTop, Const.SQ - 2, Const.SQ - 2, mouseX, mouseY)) {
    //    {}
  }

  @Override
  public void initGui() {
    super.initGui();
    int id = 2;
    filterBtn = new ButtonTileEntityField(
        id++,
        this.guiLeft + 150,
        this.guiTop + Const.PAD / 2,
        tile.getPos(), TileEntityItemPump.Fields.FILTERTYPE.ordinal(), 1,
        20, 20);
    this.addButton(filterBtn);
  }
}

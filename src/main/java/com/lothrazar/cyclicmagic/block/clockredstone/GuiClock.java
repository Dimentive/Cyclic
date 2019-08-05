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
package com.lothrazar.cyclicmagic.block.clockredstone;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.lothrazar.cyclicmagic.ModCyclic;
import com.lothrazar.cyclicmagic.block.clockredstone.TileEntityClock.Fields;
import com.lothrazar.cyclicmagic.gui.GuiBaseContainer;
import com.lothrazar.cyclicmagic.gui.button.ButtonTileEntityField;
import com.lothrazar.cyclicmagic.gui.button.ButtonTriggerWrapper.ButtonTriggerType;
import com.lothrazar.cyclicmagic.gui.component.CheckboxFacingComponent;
import com.lothrazar.cyclicmagic.gui.component.GuiTextFieldInteger;
import com.lothrazar.cyclicmagic.net.PacketTileSetField;
import com.lothrazar.cyclicmagic.util.Const;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiClock extends GuiBaseContainer {

  int w = 18, h = 15;
  int rowOffset = Const.PAD / 2;
  int colOffset = Const.PAD / 4;
  int xCol1 = 4;
  int xCol2 = xCol1 + w + colOffset;
  int xColText = xCol2 + 34;
  int xCol3 = xColText + 24;
  int xCol4 = xCol3 + w + colOffset;
  int yRow1 = Const.PAD * 3 + rowOffset;
  int yRow2 = yRow1 + h + colOffset;
  int yRow3 = yRow2 + h + colOffset;
  int xColFacing = xCol4 + w + Const.PAD;
  CheckboxFacingComponent checkboxes;
  TileEntityClock tileClock;

  public GuiClock(InventoryPlayer inventoryPlayer, TileEntityClock tileEntity) {
    super(new ContainerClock(inventoryPlayer, tileEntity), tileEntity);
    tileClock = (TileEntityClock) this.tile;
    this.fieldRedstoneBtn = Fields.REDSTONE.ordinal();
    checkboxes = new CheckboxFacingComponent(this);
    checkboxes.setX(140);
    checkboxes.setY(Const.PAD * 4);
    Map<EnumFacing, Integer> facingFields = new HashMap<EnumFacing, Integer>();
    for (EnumFacing side : EnumFacing.values()) {
      switch (side) {
        case DOWN:
          facingFields.put(side, Fields.D.ordinal());
        break;
        case EAST:
          facingFields.put(side, Fields.E.ordinal());
        break;
        case NORTH:
          facingFields.put(side, Fields.N.ordinal());
        break;
        case SOUTH:
          facingFields.put(side, Fields.S.ordinal());
        break;
        case UP:
          facingFields.put(side, Fields.U.ordinal());
        break;
        case WEST:
          facingFields.put(side, Fields.W.ordinal());
        break;
      }
    }
    checkboxes.setFacingFields(facingFields);
  }

  @Override
  public void initGui() {
    super.initGui();
    int id = 30;
    int xColTextbox = xCol2 + 22;
    addButton(xCol1, yRow1, Fields.TON.ordinal(), -5, "duration");
    addButton(xCol2, yRow1, Fields.TON.ordinal(), -1, "duration");
    // here
    GuiTextFieldInteger txtPower = addTextbox(id++, xColTextbox, yRow1, tile.getField(Fields.TON.ordinal()) + "", 4);
    txtPower.setMaxVal(9999);
    txtPower.setMinVal(1);
    txtPower.height = 16;
    txtPower.width = 32;
    txtPower.setTileFieldId(TileEntityClock.Fields.TON.ordinal());
    //
    addButton(xCol3, yRow1, Fields.TON.ordinal(), 1, "duration");
    addButton(xCol4, yRow1, Fields.TON.ordinal(), 5, "duration");
    addButton(xCol1, yRow2, Fields.TOFF.ordinal(), -5, "delay");
    addButton(xCol2, yRow2, Fields.TOFF.ordinal(), -1, "delay");
    //
    GuiTextFieldInteger txtTOFF = addTextbox(id++, xColTextbox, yRow2, tile.getField(Fields.TOFF.ordinal()) + "", 4);
    txtTOFF.setMaxVal(9999);
    txtTOFF.setMinVal(1);
    txtTOFF.height = 16;
    txtTOFF.width = 32;
    txtTOFF.setTileFieldId(TileEntityClock.Fields.TOFF.ordinal());
    //
    addButton(xCol3, yRow2, Fields.TOFF.ordinal(), 1, "delay");
    addButton(xCol4, yRow2, Fields.TOFF.ordinal(), 5, "delay");
    addButton(xCol2, yRow3, Fields.POWER.ordinal(), -1, "power");
    addButton(xCol3, yRow3, Fields.POWER.ordinal(), 1, "power");
    //checkboxes
    checkboxes.initGui();
  }

  private void addButton(int x, int y, int field, int value, String tooltip) {
    ButtonTileEntityField btn = new ButtonTileEntityField(field + 50,
        this.guiLeft + x,
        this.guiTop + y, this.tile.getPos(), field, value,
        w, h);
    if (value > 0) {
      btn.displayString = "+" + value;
      if (field == Fields.POWER.ordinal()) {
        //TODO: setup/find magic numbers for redstone, 15 is max
        this.registerButtonDisableTrigger(btn, ButtonTriggerType.EQUAL, field, 15);
      }
    }
    else {
      btn.displayString = "" + value;
      int min = (field == Fields.POWER.ordinal()) ? 0 : 1;
      this.registerButtonDisableTrigger(btn, ButtonTriggerType.EQUAL, field, min);
    }
    btn.setTooltip("tile.clock." + tooltip);
    this.addButton(btn);
  }

  @SideOnly(Side.CLIENT)
  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    this.drawString("" + this.tile.getField(Fields.POWER.ordinal()), xColText, yRow3 + rowOffset);
  }

  @Override
  protected void keyTyped(char pchar, int keyCode) throws IOException {
    super.keyTyped(pchar, keyCode);
    for (GuiTextField t : txtBoxes) {
      GuiTextFieldInteger txt = (GuiTextFieldInteger) t;
      String oldval = txt.getText();
      txt.textboxKeyTyped(pchar, keyCode);
      String newval = txt.getText();
      boolean yes = false;
      try {
        int val = Integer.parseInt(newval);
        if (val <= txt.getMaxVal() && val >= txt.getMinVal()) {
          yes = true;
          //also set it clientisde to hopefully prevent desycn
          tile.setField(txt.getTileFieldId(), val);
          ModCyclic.network.sendToServer(new PacketTileSetField(tile.getPos(), txt.getTileFieldId(), val));
        }
      }
      catch (NumberFormatException e) {}
      if (!yes && !newval.isEmpty()) {//allow empty string in case user is in middle of deleting all and retyping
        txt.setText(oldval);//rollback to the last valid value. ex if they type 'abc' revert to valid 
      }
    }
  }
}

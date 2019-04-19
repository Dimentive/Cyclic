package com.lothrazar.cyclicmagic.block.firestarter;

import java.io.IOException;
import org.lwjgl.input.Keyboard;
import com.lothrazar.cyclicmagic.gui.button.ButtonTileEntityField;
import com.lothrazar.cyclicmagic.gui.component.EnergyBar;
import com.lothrazar.cyclicmagic.gui.component.GuiSliderInteger;
import com.lothrazar.cyclicmagic.gui.container.GuiBaseContainer;
import com.lothrazar.cyclicmagic.util.Const;
import com.lothrazar.cyclicmagic.util.UtilChat;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiFireStarter extends GuiBaseContainer {

  private ButtonTileEntityField fireTypeButton;
  private ButtonTileEntityField yOffset;
  private GuiSliderInteger slider;

  public GuiFireStarter(InventoryPlayer inventoryPlayer, TileEntityFireStarter tile) {
    super(new ContainerFireStarter(inventoryPlayer, tile), tile);
    this.fieldRedstoneBtn = TileEntityFireStarter.Fields.REDSTONE.ordinal();
    this.energyBar = new EnergyBar(this);
    energyBar.setWidth(16).setX(this.xSize - 26);
  }

  @Override
  public void initGui() {
    super.initGui();
    Keyboard.enableRepeatEvents(true);
    int id = 1;
    fireTypeButton = new ButtonTileEntityField(id++,
        guiLeft + Const.PAD / 2,
        guiTop + 46, this.tile.getPos(), TileEntityFireStarter.Fields.FIRETYPE.ordinal());
    fireTypeButton.width = 60;
    fireTypeButton.setTooltip("fire_starter.firetype.button");
    //    fireTypeButton.setTooltip("fire_starter.action.tooltip");
    this.addButton(fireTypeButton);
    yOffset = new ButtonTileEntityField(id++,
        this.guiLeft + Const.PAD / 2,
        guiTop + 26, this.tile.getPos(), TileEntityFireStarter.Fields.Y_OFFSET.ordinal());
    yOffset.width = yOffset.height = Const.SQ;
    yOffset.setTooltip("fire_starter.yoffset.tooltip");
    this.addButton(yOffset);
    slider = new GuiSliderInteger(tile, id++,
        this.guiLeft + Const.PAD * 3 + 4,
        this.guiTop + Const.PAD * 4 - 2, 112, 12, 0, 16,
        TileEntityFireStarter.Fields.OFFSET.ordinal());
    slider.setTooltip("fire_starter.offset.tooltip");
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

  @SideOnly(Side.CLIENT)
  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    yOffset.displayString = tile.getField(TileEntityFireStarter.Fields.Y_OFFSET.ordinal()) + "";
    fireTypeButton.displayString = UtilChat.lang("fire_starter.fire" + tile.getField(TileEntityFireStarter.Fields.FIRETYPE.ordinal()));
  }
}

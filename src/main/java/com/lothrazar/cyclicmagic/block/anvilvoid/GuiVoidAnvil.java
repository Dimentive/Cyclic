package com.lothrazar.cyclicmagic.block.anvilvoid;

import com.lothrazar.cyclicmagic.block.anvil.ContainerAnvilAuto;
import com.lothrazar.cyclicmagic.gui.GuiBaseContainer;
import com.lothrazar.cyclicmagic.gui.component.EnergyBar;
import com.lothrazar.cyclicmagic.util.Const;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiVoidAnvil extends GuiBaseContainer {

  public GuiVoidAnvil(InventoryPlayer inventoryPlayer, TileEntityVoidAnvil tile) {
    super(new ContainerVoidAnvil(inventoryPlayer, tile), tile);
    this.energyBar = new EnergyBar(this);
    energyBar.setWidth(16).setX(this.xSize / 2 - 8 - 1);
    this.fieldRedstoneBtn = TileEntityVoidAnvil.Fields.REDSTONE.ordinal();
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    int u = 0, v = 0;
    this.mc.getTextureManager().bindTexture(Const.Res.SLOT);
    Gui.drawModalRectWithCustomSizedTexture(
        this.guiLeft + 50 - 1,
        this.guiTop + ContainerAnvilAuto.SLOTY - 1,
        u, v, Const.SQ, Const.SQ, Const.SQ, Const.SQ);
    Gui.drawModalRectWithCustomSizedTexture(
        this.guiLeft + 110 - 1,
        this.guiTop + ContainerAnvilAuto.SLOTY - 1,
        u, v, Const.SQ, Const.SQ, Const.SQ, Const.SQ);
  }
}

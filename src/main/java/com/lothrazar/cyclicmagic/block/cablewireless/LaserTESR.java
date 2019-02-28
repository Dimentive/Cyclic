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
package com.lothrazar.cyclicmagic.block.cablewireless;

import com.lothrazar.cyclicmagic.util.RenderUtil;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LaserTESR extends TileEntitySpecialRenderer<TileEntity> {

  @Override
  public void render(TileEntity tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
    if (tile instanceof ILaserTarget == false) {
      return;
    }
    ILaserTarget te = (ILaserTarget) tile;
    if (te.isVisible() == false) {
      return;
    }
    RenderUtil.renderLaser(te.getTarget());
    //    float[] color = te.getColor();
    //    double rotationTime = 0;
    //    double beamWidth = 0.09;
    //    //find laser endpoints and go
    //    BlockPosDim first = te.getPos();
    //    BlockPosDim second = te.getTarget();
    //
    //    if (second != null && first != null && second.getDimension() == first.getDimension()) {
    //      LaserConfig laserCnf = new LaserConfig(first.toBlockPos(), second.toBlockPos(),
    //            rotationTime, te.getAlpha(), beamWidth, color);
    //
    //        RenderUtil.renderLaser(laserCnf);
    //      }
  }

  @Override
  public boolean isGlobalRenderer(TileEntity te) {
    return true;//should link to tile entity setSetRenderGlobally
  }
}

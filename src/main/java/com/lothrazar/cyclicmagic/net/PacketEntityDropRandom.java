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
package com.lothrazar.cyclicmagic.net;

import com.lothrazar.cyclicmagic.ModCyclic;
import com.lothrazar.cyclicmagic.util.UtilChat;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketEntityDropRandom implements IMessage, IMessageHandler<PacketEntityDropRandom, IMessage> {

  private int entityId;
  private int slot;

  public PacketEntityDropRandom() {}

  public PacketEntityDropRandom(int entityid, int level) {
    entityId = entityid;
    this.slot = level;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    NBTTagCompound tags = ByteBufUtils.readTag(buf);
    entityId = tags.getInteger("entityId");
    slot = tags.getInteger("level");
  }

  @Override
  public void toBytes(ByteBuf buf) {
    NBTTagCompound tags = new NBTTagCompound();
    tags.setInteger("entityId", entityId);
    tags.setInteger("level", slot);
    ByteBufUtils.writeTag(buf, tags);
  }

  @Override
  public IMessage onMessage(PacketEntityDropRandom message, MessageContext ctx) {
    if (ctx.side.isServer()) {
      MinecraftServer s = FMLCommonHandler.instance().getMinecraftServerInstance();
      s.addScheduledTask(new Runnable() {

        @Override
        public void run() {
          handle(message, ctx);
        }
      });
    }
    return null;
  }

  private void handle(PacketEntityDropRandom message, MessageContext ctx) {
    EntityPlayer player = ctx.getServerHandler().player;
    World world = player.getEntityWorld();
    Entity entityTarget = world.getEntityByID(message.entityId);
    if (entityTarget != null && entityTarget instanceof EntityLivingBase) {
      EntityLivingBase entity = (EntityLivingBase) entityTarget;
      EntityEquipmentSlot slot = EntityEquipmentSlot.values()[message.slot];
      ItemStack stackCurrent = player.getItemStackFromSlot(slot);
      if (stackCurrent.isEmpty()) {
        return;
      }
      //      EntityItem ei = UtilItemStack.dropItemStackInWorld(world, entity.getPosition(), message.stack);
      BlockPos pos = entity.getPosition();
      EntityItem entityItem = new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stackCurrent);
      // entityItem.addVelocity(0, PotionDropItems.VELOCITY, 0);
      world.spawnEntity(entityItem);
      entity.setItemStackToSlot(slot, ItemStack.EMPTY);
      if (entity instanceof EntityPlayer) {
        UtilChat.sendStatusMessage((EntityPlayer) entity, "potion.butter.oops");
      }
    }
    else {
      ModCyclic.logger.error("NOT FOUND packet entityid" + message.entityId);
    }
  }
}

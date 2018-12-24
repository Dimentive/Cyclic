package com.lothrazar.cyclicmagic.block.sound;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import com.lothrazar.cyclicmagic.block.core.TileEntityBaseMachineInvo;
import com.lothrazar.cyclicmagic.block.password.IPlayerClaimed;
import com.lothrazar.cyclicmagic.gui.ITileRedstoneToggle;
import com.lothrazar.cyclicmagic.util.UtilSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class TileEntitySoundPlayer extends TileEntityBaseMachineInvo implements ITileRedstoneToggle, ITickable, IPlayerClaimed {

  private static final int TIMER_MAX = 100;
  private int needsRedstone = 1;
  private int soundIndex = -1;
  private String userHash = "";
  private String userName = "";

  public static enum Fields {
    REDSTONE, TIMER, SOUNDINDEX;
  }

  public TileEntitySoundPlayer() {
    super(0);
  }

  public static List<ResourceLocation> getSoundList() {
    // blacklisted:    minecraft:record
    List<ResourceLocation> allSounds = new ArrayList<>();
    for (ResourceLocation r : SoundEvent.REGISTRY.getKeys()) {
      if (!r.toString().contains("minecraft:record"))
        allSounds.add(r);
    }
    allSounds.sort(Comparator.comparing(ResourceLocation::toString));
    return allSounds;
  }

  @Override
  public void update() {
    if (isPowered() == false && this.onlyRunIfPowered()) {
      //i need signal to run. i dont have signal. set timer zero so pulse triggers right away
      timer = 0;
    }
    if (this.isRunning() == false) {
      return;
    }
    if (this.updateTimerIsZero()) {
      if (soundIndex >= 0 && soundIndex < SoundEvent.REGISTRY.getKeys().size()) {
        List<ResourceLocation> allSounds = getSoundList();
        ResourceLocation sound = allSounds.get(soundIndex);
        if (sound != null && SoundEvent.REGISTRY.getObject(sound) != null) {
          playSound(sound);
        }
      }
    }
  }

  private void playSound(ResourceLocation sound) {
    timer = TIMER_MAX;
    if (BlockSoundPlayer.playToEverybody) {
      //      ModCyclic.logger.info("Play sound for everybody ");
      UtilSound.playSound(world, pos, SoundEvent.REGISTRY.getObject(sound), SoundCategory.BLOCKS);
      return;
    }
    //get player by hash
    try {
      if (this.getClaimedHash() != null) {
        //        ModCyclic.logger.info("sound play only owner owner :" + this.getClaimedName());
        EntityPlayer playerTarget = world.getPlayerEntityByUUID(UUID.fromString(this.getClaimedHash()));
        UtilSound.playSound(playerTarget, pos, SoundEvent.REGISTRY.getObject(sound));
        return;
      }
    }
    catch (Exception e) {
      //no sound, no probl
    }
  }

  @Override
  public int[] getFieldOrdinals() {
    return super.getFieldArray(Fields.values().length);
  }

  @Override
  public void toggleNeedsRedstone() {
    int val = (this.needsRedstone + 1) % 2;
    this.setField(Fields.REDSTONE.ordinal(), val);
  }

  @Override
  public boolean onlyRunIfPowered() {
    return this.needsRedstone == 1;
  }

  @Override
  public int getField(int id) {
    switch (Fields.values()[id]) {
      case TIMER:
        return timer;
      case REDSTONE:
        return this.needsRedstone;
      case SOUNDINDEX:
        return this.soundIndex;
      default:
      break;
    }
    return -1;
  }

  @Override
  public void setField(int id, int value) {
    switch (Fields.values()[id]) {
      case TIMER:
        this.timer = value;
      break;
      case REDSTONE:
        this.needsRedstone = value;
      break;
      case SOUNDINDEX:
        this.soundIndex = value;
      break;
    }
  }

  @Override
  public void readFromNBT(NBTTagCompound tags) {
    super.readFromNBT(tags);
    this.needsRedstone = tags.getInteger(NBT_REDST);
    soundIndex = tags.getInteger("soundIndex");
    userHash = tags.getString(NBT_UHASH);
    userName = tags.getString(NBT_UNAME);
  }

  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound tags) {
    tags.setInteger(NBT_REDST, this.needsRedstone);
    tags.setInteger("soundIndex", soundIndex);
    tags.setString(NBT_UHASH, userHash);
    tags.setString(NBT_UNAME, userName);
    return super.writeToNBT(tags);
  }

  @Override
  public boolean isClaimedBy(EntityPlayer p) {
    return p.getUniqueID().toString().equals(this.userHash);
  }

  @Override
  public boolean isClaimedBySomeone() {
    return this.userHash != null && !this.userHash.isEmpty();
  }

  @Override
  public String getClaimedHash() {
    return userHash;
  }

  @Override
  public void toggleClaimedHash(EntityPlayer player) {
    if (isClaimedBySomeone()) {
      this.userHash = "";
      this.userName = "";
    }
    else {
      this.userHash = player.getUniqueID().toString();
      this.userName = player.getDisplayNameString();
    }
  }

  @Override
  public String getClaimedName() {
    return userName;
  }
}

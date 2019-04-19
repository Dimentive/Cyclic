package com.lothrazar.cyclicmagic;

import com.lothrazar.cyclicmagic.config.IHasConfig;

public interface IContent extends IHasConfig {

  public String getContentName();

  public void register();

  public boolean enabled();
}

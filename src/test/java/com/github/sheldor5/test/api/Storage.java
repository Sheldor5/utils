package com.github.sheldor5.test.api;

public abstract class Storage {

  public Storage() {
  }

  public abstract String getName();

  public abstract boolean save(String var1, Object var2, boolean var3);

  public abstract Object load(String var1);
}

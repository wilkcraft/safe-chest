package com.safeChest;

import org.bukkit.plugin.java.JavaPlugin;

public class SafeChest extends JavaPlugin {

  private static SafeChest instance;
  private ChestSystem chestSystem;

  @Override
  public void onEnable() {
    instance = this;

    getLogger().info("SafeChest enabled!");

    chestSystem = new ChestSystem(this);
    chestSystem.loadChests();

    getServer().getPluginManager().registerEvents(new ChestSystem.ChestListener(chestSystem), this);

    CommandManager manager = new CommandManager(chestSystem);

    getCommand("sc").setExecutor(manager);
    getCommand("sc").setTabCompleter(manager);
  }

  @Override
  public void onDisable() {
    getLogger().info("SafeChest disabled!");
    chestSystem.saveChests();
  }

  public static SafeChest getInstance() {
    return instance;
  }
}

package com.safeChest;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ChestSystem {

  private final Map<String, ProtectedChest> chests = new HashMap<>();
  private File file;
  private FileConfiguration config;

  public ChestSystem(SafeChest plugin) {
    plugin.getLogger().info("ChestSystem initialized");

    file = new File(plugin.getDataFolder(), "chests.yml");

    if (!file.exists()) {
      plugin.getDataFolder().mkdirs();
      plugin.saveResource("chests.yml", false);
    }

    config = YamlConfiguration.loadConfiguration(file);
  }

  // =========================
  // DATA
  // =========================
  public void addChest(String name, UUID owner, Location loc) {
    chests.put(name, new ProtectedChest(name, owner, loc));
    saveChests();
  }

  public ProtectedChest getChestByName(String name) {
    return chests.get(name);
  }

  public List<ProtectedChest> getChestsByOwner(UUID owner) {
    List<ProtectedChest> list = new ArrayList<>();
    for (ProtectedChest c : chests.values()) {
      if (c.owner.equals(owner))
        list.add(c);
    }
    return list;
  }

  public ProtectedChest getChestByLocation(Location loc) {
    for (ProtectedChest chest : chests.values()) {
      for (Location c : chest.locations) {
        if (c.getWorld().equals(loc.getWorld()) &&
            c.getBlockX() == loc.getBlockX() &&
            c.getBlockY() == loc.getBlockY() &&
            c.getBlockZ() == loc.getBlockZ()) {
          return chest;
        }
      }
    }
    return null;
  }

  public Map<String, ProtectedChest> getChests() {
    return chests;
  }

  // =========================
  // SAVE / LOAD
  // =========================
  public void saveChests() {
    config.set("chests", null);

    for (String name : chests.keySet()) {
      ProtectedChest c = chests.get(name);

      String path = "chests." + name;
      config.set(path + ".owner", c.owner.toString());

      // Guardar todas las localizaciones
      List<String> locs = new ArrayList<>();
      for (Location l : c.locations) {
        locs.add(l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ());
      }
      config.set(path + ".locations", locs);

      // Guardar amigos
      List<String> friends = new ArrayList<>();
      for (UUID u : c.friends)
        friends.add(u.toString());

      config.set(path + ".friends", friends);
    }

    try {
      config.save(file);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void removeChest(Location loc) {
    String toRemove = null;

    for (String name : chests.keySet()) {
      ProtectedChest c = chests.get(name);

      if (c.locations.contains(loc)) {
        toRemove = name;
        break;
      }
    }

    if (toRemove != null)
      chests.remove(toRemove);
  }

  public void loadChests() {
    chests.clear();

    if (!config.contains("chests"))
      return;

    for (String name : config.getConfigurationSection("chests").getKeys(false)) {

      String path = "chests." + name;

      UUID owner = UUID.fromString(config.getString(path + ".owner"));
      World world = Bukkit.getWorld(config.getString(path + ".world"));

      if (world == null)
        continue;

      ProtectedChest chest = new ProtectedChest(name, owner, new Location(world, 0, 0, 0));
      chest.locations.clear();

      // Cargar todas las localizaciones
      for (String s : config.getStringList(path + ".locations")) {
        String[] split = s.split(",");
        World w = Bukkit.getWorld(split[0]);
        int x = Integer.parseInt(split[1]);
        int y = Integer.parseInt(split[2]);
        int z = Integer.parseInt(split[3]);
        chest.locations.add(new Location(w, x, y, z));
      }

      // Cargar amigos
      for (String s : config.getStringList(path + ".friends")) {
        chest.friends.add(UUID.fromString(s));
      }

      chests.put(name, chest);
    }
  }

  // =========================
  // CHEST CLASS
  // =========================
  public static class ProtectedChest {
    public String name;
    public UUID owner;
    public Set<Location> locations = new HashSet<>();
    public Set<UUID> friends = new HashSet<>();

    public ProtectedChest(String name, UUID owner, Location loc) {
      this.name = name;
      this.owner = owner;
      this.locations.add(loc);
    }
  }

  // =========================
  // LISTENER
  // =========================
  public static class ChestListener implements Listener {

    private final ChestSystem data;

    public ChestListener(ChestSystem data) {
      this.data = data;
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {

      if (!(event.getPlayer() instanceof Player player))
        return;
      if (event.getInventory().getLocation() == null)
        return;

      ProtectedChest chest = data.getChestByLocation(event.getInventory().getLocation());

      if (chest == null)
        return;

      if (player.getUniqueId().equals(chest.owner))
        return;
      if (chest.friends.contains(player.getUniqueId()))
        return;

      event.setCancelled(true);

      player.sendMessage("§cYou cannot open this chest");

      Player owner = Bukkit.getPlayer(chest.owner);
      if (owner != null) {
        owner.sendMessage("§eSomeone tried to open your chest!");
      }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {

      Player player = event.getPlayer();
      Location loc = event.getBlock().getLocation();

      ProtectedChest chest = data.getChestByLocation(loc);

      if (chest == null)
        return;

      if (!player.getUniqueId().equals(chest.owner)) {
        event.setCancelled(true);
        player.sendMessage("§cYou cannot break this chest");
        return;
      }

      // 🔥 SOLO quitar ese bloque
      chest.locations.removeIf(c -> c.getWorld().equals(loc.getWorld()) &&
          c.getBlockX() == loc.getBlockX() &&
          c.getBlockY() == loc.getBlockY() &&
          c.getBlockZ() == loc.getBlockZ());

      // 🔥 Si ya no quedan bloques → borrar cofre
      if (chest.locations.isEmpty()) {
        data.getChests().remove(chest.name);
      }

      data.saveChests();

      player.sendMessage("§eChest part unprotected");
    }

    @EventHandler
    public void onClick(org.bukkit.event.inventory.InventoryClickEvent event) {

      if (!(event.getWhoClicked() instanceof Player))
        return;

      if (event.getView().title().equals(Component.text("Your Chests"))) {
        event.setCancelled(true);
      }
    }

    @EventHandler
    public void onPlace(org.bukkit.event.block.BlockPlaceEvent event) {

      Block block = event.getBlockPlaced();
      Player player = event.getPlayer();

      if (!block.getType().toString().contains("CHEST"))
        return;

      for (BlockFace face : new BlockFace[] { BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST }) {

        Block adjacent = block.getRelative(face);
        ProtectedChest chest = data.getChestByLocation(adjacent.getLocation());

        if (chest == null)
          continue;

        // 🔥 SI YA ES DOBLE → permitir (no hacer nada)
        if (chest.locations.size() >= 2) {
          return;
        }

        // ❌ NO ES OWNER → cancelar SOLO si es single
        if (!chest.owner.equals(player.getUniqueId())) {
          event.setCancelled(true);
          player.sendMessage("§cYou cannot connect to this protected chest");
          return;
        }

        // ✅ ES OWNER → convertir en doble
        chest.locations.add(block.getLocation());
        data.saveChests();
        player.sendMessage("§aChest merged into protected double chest");
      }
    }

    @EventHandler
    public void onHopperTake(org.bukkit.event.inventory.InventoryMoveItemEvent event) {

      if (event.getSource().getLocation() == null)
        return;

      ProtectedChest chest = data.getChestByLocation(event.getSource().getLocation());

      if (chest != null) {
        event.setCancelled(true); // ❌ no sacar items
      }
    }

    @EventHandler
    public void onHopperPut(org.bukkit.event.inventory.InventoryMoveItemEvent event) {

      if (event.getDestination().getLocation() == null)
        return;

      ProtectedChest chest = data.getChestByLocation(event.getDestination().getLocation());

      if (chest != null) {
        // ✅ NO cancelar → permite meter items
      }
    }

    @EventHandler
    public void onExplode(org.bukkit.event.entity.EntityExplodeEvent event) {

      event.blockList().removeIf(block -> data.getChestByLocation(block.getLocation()) != null);
    }
  }

  // =========================
  // GUI
  // =========================
  public void openGUI(Player player) {

    List<ProtectedChest> list = getChestsByOwner(player.getUniqueId());

    Inventory inv = Bukkit.createInventory(null, 27, Component.text("Your Chests"));

    int slot = 0;

    for (ProtectedChest c : list) {

      ItemStack item = new ItemStack(Material.CHEST);
      ItemMeta meta = item.getItemMeta();

      meta.displayName(Component.text("§a" + c.name));

      List<Component> lore = new ArrayList<>();

      for (Location loc : c.locations) {
        lore.add(Component.text("§7X: " + loc.getBlockX()
            + " Y: " + loc.getBlockY()
            + " Z: " + loc.getBlockZ()));
      }

      meta.lore(lore);

      item.setItemMeta(meta);

      inv.setItem(slot++, item);
    }

    player.openInventory(inv);
  }
}

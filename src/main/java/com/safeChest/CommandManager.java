package com.safeChest;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandManager implements CommandExecutor, TabCompleter {

  private final ChestSystem data;

  public CommandManager(ChestSystem data) {
    this.data = data;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

    if (!(sender instanceof Player player))
      return true;

    if (args.length == 0) {
      player.sendMessage("§cYou need more arguments. Use /sc help");
      return true;
    }

    switch (args[0].toLowerCase()) {
      case "help" -> {
        player.sendMessage("§6==== Safe-Chest Help ====");
        player.sendMessage("§e/sc protect <name> §7- Protect a chest");
        player.sendMessage("§e/sc unprotect [name] §7- Unprotect chest");
        player.sendMessage("§e/sc addfriend <player> [chest] §7- Add access for another user");
        player.sendMessage("§e/sc unfriend <player> [chest] §7- Remove a friend's access");
        player.sendMessage("§e/sc chests §7- Open a GUI with your chests");
        player.sendMessage("§e/sc reload §7- Reload chests.yml");
      }

      case "protect" -> {
        if (args.length != 2) {
          player.sendMessage("§cUse: /sc protect <name>");
          return true;
        }

        Block block = player.getTargetBlockExact(5);
        if (block == null || !block.getType().toString().contains("CHEST")) {
          player.sendMessage("§cYou must look at a chest");
          return true;
        }

        // evitar duplicados
        if (data.getChestByLocation(block.getLocation()) != null) {
          player.sendMessage("§cThis chest is already protected");
          return true;
        }

        ChestSystem.ProtectedChest chest = new ChestSystem.ProtectedChest(args[1], player.getUniqueId(),
            block.getLocation());

        // detectar doble cofre
        for (BlockFace face : new BlockFace[] { BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST }) {
          Block relative = block.getRelative(face);
          if (relative.getType().toString().contains("CHEST")) {
            chest.locations.add(relative.getLocation());
          }
        }

        data.getChests().put(args[1], chest);
        data.saveChests();

        player.sendMessage("§aChest protected: " + args[1]);
      }

      case "unprotect" -> {

        ChestSystem.ProtectedChest chest = null;

        // 🔹 CASO 1: por nombre
        if (args.length == 2) {
          chest = data.getChestByName(args[1]);
        }

        // 🔹 CASO 2: mirando
        else {

          Block block = player.getTargetBlockExact(5);

          if (block == null || !block.getType().toString().contains("CHEST")) {
            player.sendMessage("§cLook at a chest");
            return true;
          }

          chest = data.getChestByLocation(block.getLocation());
        }

        if (chest == null) {
          player.sendMessage("§cChest not found");
          return true;
        }

        if (!chest.owner.equals(player.getUniqueId())) {
          player.sendMessage("§cThis is not your chest");
          return true;
        }

        data.getChests().remove(chest.name);
        data.saveChests();

        player.sendMessage("§aChest unprotected");
      }

      case "addfriend" -> {

        if (args.length < 2) {
          player.sendMessage("§cUse: /sc addfriend <player> [chest]");
          return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
          player.sendMessage("§cPlayer not found");
          return true;
        }

        ChestSystem.ProtectedChest chest = null;

        // 🔹 CASO 1: mirando al cofre
        if (args.length == 2) {

          Block block = player.getTargetBlockExact(5);

          if (block == null || !block.getType().toString().contains("CHEST")) {
            player.sendMessage("§cLook at a chest");
            return true;
          }

          chest = data.getChestByLocation(block.getLocation());
        }

        // 🔹 CASO 2: por nombre
        else {
          chest = data.getChestByName(args[2]);
        }

        if (chest == null) {
          player.sendMessage("§cChest not found");
          return true;
        }

        if (!chest.owner.equals(player.getUniqueId())) {
          player.sendMessage("§cThis is not your chest");
          return true;
        }

        chest.friends.add(target.getUniqueId());
        data.saveChests();

        player.sendMessage("§aFriend added!");
      }

      case "unfriend" -> {

        if (args.length < 2) {
          player.sendMessage("§cUse: /sc unfriend <player> [chest]");
          return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
          player.sendMessage("§cPlayer not found");
          return true;
        }

        // 🔹 TODOS LOS COFRES
        if (args.length == 2) {

          for (ChestSystem.ProtectedChest chest : data.getChestsByOwner(player.getUniqueId())) {
            chest.friends.remove(target.getUniqueId());
          }

          data.saveChests();
          player.sendMessage("§aFriend removed from all chests");
          return true;
        }

        // 🔹 SOLO UN COFRE
        ChestSystem.ProtectedChest chest = data.getChestByName(args[2]);

        if (chest == null) {
          player.sendMessage("§cChest not found");
          return true;
        }

        if (!chest.owner.equals(player.getUniqueId())) {
          player.sendMessage("§cThis is not your chest");
          return true;
        }

        chest.friends.remove(target.getUniqueId());
        data.saveChests();

        player.sendMessage("§aFriend removed");
      }

      case "chests" -> data.openGUI(player);

      case "reload" -> {
        data.loadChests();
        player.sendMessage("§aReloaded");
      }
    }

    return true;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

    if (!(sender instanceof Player player))
      return Collections.emptyList();

    List<String> completions = new ArrayList<>();

    // BASE
    List<String> base = List.of("protect", "unprotect", "addfriend", "unfriend", "chests", "reload", "help");

    // 🔹 /sc
    if (args.length == 1) {
      for (String s : base) {
        if (s.toLowerCase().startsWith(args[0].toLowerCase())) {
          completions.add(s);
        }
      }
      return completions;
    }

    // 🔹 protect
    if (args[0].equalsIgnoreCase("protect") && args.length == 2) {
      completions.add("<name>");
      return completions;
    }

    // 🔹 unprotect
    if (args[0].equalsIgnoreCase("unprotect") && args.length == 2) {
      for (var c : data.getChestsByOwner(player.getUniqueId())) {
        if (c.name.toLowerCase().startsWith(args[1].toLowerCase())) {
          completions.add(c.name);
        }
      }
      return completions;
    }

    // 🔹 addfriend / unfriend
    if (args[0].equalsIgnoreCase("addfriend") || args[0].equalsIgnoreCase("unfriend")) {

      // jugadores
      if (args.length == 2) {
        for (Player p : Bukkit.getOnlinePlayers()) {
          if (p.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
            completions.add(p.getName());
          }
        }
        return completions;
      }

      // cofres
      if (args.length == 3) {
        for (var c : data.getChestsByOwner(player.getUniqueId())) {
          if (c.name.toLowerCase().startsWith(args[2].toLowerCase())) {
            completions.add(c.name);
          }
        }
        return completions;
      }
    }

    return completions;
  }
}

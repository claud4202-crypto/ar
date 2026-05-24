package me.ariscrates.commands;

import me.ariscrates.ArisCratesPlugin;
import me.ariscrates.managers.Msg;
import me.ariscrates.models.Crate;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CrateCommand implements CommandExecutor, TabCompleter {

    private final ArisCratesPlugin plugin;

    public CrateCommand(ArisCratesPlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Msg.parse("&6[ArisCrates] &7Использование:"));
            sender.sendMessage(Msg.parse("  &e/crate list &7— список крейтов"));
            sender.sendMessage(Msg.parse("  &e/crate preview <крейт> &7— посмотреть награды"));
            sender.sendMessage(Msg.parse("  &e/crate place <крейт> &7— поставить крейт (смотри на блок)"));
            sender.sendMessage(Msg.parse("  &e/crate remove &7— удалить крейт (смотри на блок)"));
            sender.sendMessage(Msg.parse("  &e/key <ник> <крейт> [кол-во] &7— выдать ключ"));
            sender.sendMessage(Msg.parse("  &e/crate reload &7— перечитать конфиг"));
            return true;
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "list" -> {
                sender.sendMessage(Msg.parse("&6[ArisCrates] &7Доступные крейты:"));
                for (Crate c : plugin.getCrateManager().all()) {
                    sender.sendMessage(Msg.parse("  &f• " + c.displayName() + " &7(id: &f" + c.id() + "&7, наград: &f" + c.rewards().size() + "&7)"));
                }
            }
            case "preview" -> {
                if (!(sender instanceof Player p)) { sender.sendMessage(Msg.parse("&cТолько в игре.")); return true; }
                if (args.length < 2) { sender.sendMessage(Msg.parse("&7/crate preview <крейт>")); return true; }
                Crate c = plugin.getCrateManager().getCrate(args[1]);
                if (c == null) { sender.sendMessage(Msg.parse("&cКрейт не найден: &e" + args[1])); return true; }
                plugin.getPreviewGui().open(p, c);
            }
            case "place" -> {
                if (!sender.hasPermission("ariscrates.admin")) { sender.sendMessage(Msg.parse("&cНет доступа.")); return true; }
                if (!(sender instanceof Player p)) { sender.sendMessage(Msg.parse("&cТолько в игре.")); return true; }
                if (args.length < 2) { sender.sendMessage(Msg.parse("&7/crate place <крейт>")); return true; }
                Crate c = plugin.getCrateManager().getCrate(args[1]);
                if (c == null) { sender.sendMessage(Msg.parse("&cКрейт не найден: &e" + args[1])); return true; }
                Block block = p.getTargetBlockExact(5);
                if (block == null) { sender.sendMessage(Msg.parse("&cПосмотрите на блок.")); return true; }
                plugin.getLocationManager().setCrate(block.getLocation(), c.id());
                String locKey = plugin.getLocationManager().locKeyOf(block.getLocation());
                plugin.getHologramManager().spawnForLocation(locKey, c.id());
                sender.sendMessage(Msg.parse("&aКрейт " + c.displayName() + " &aустановлен на " + block.getX() + ", " + block.getY() + ", " + block.getZ()));
            }
            case "remove" -> {
                if (!sender.hasPermission("ariscrates.admin")) { sender.sendMessage(Msg.parse("&cНет доступа.")); return true; }
                if (!(sender instanceof Player p)) { sender.sendMessage(Msg.parse("&cТолько в игре.")); return true; }
                Block block = p.getTargetBlockExact(5);
                if (block == null) { sender.sendMessage(Msg.parse("&cПосмотрите на блок.")); return true; }
                String locKey = plugin.getLocationManager().locKeyOf(block.getLocation());
                plugin.getLocationManager().removeCrate(block.getLocation());
                plugin.getHologramManager().removeForLocation(locKey);
                sender.sendMessage(Msg.parse("&aКрейт удалён."));
            }
            case "reload" -> {
                if (!sender.hasPermission("ariscrates.admin")) { sender.sendMessage(Msg.parse("&cНет доступа.")); return true; }
                plugin.reloadConfig();
                plugin.getCrateManager().reload();
                plugin.getHologramManager().spawnAll();
                sender.sendMessage(Msg.parse("&aКонфиг перечитан, голограммы обновлены."));
            }
            default -> sender.sendMessage(Msg.parse("&cНеизвестная подкоманда: &e" + sub));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) return List.of("list", "preview", "place", "remove", "reload");
        if (args.length == 2 && (args[0].equalsIgnoreCase("preview") || args[0].equalsIgnoreCase("place"))) {
            List<String> ids = new ArrayList<>();
            for (Crate c : plugin.getCrateManager().all()) ids.add(c.id());
            return ids;
        }
        return List.of();
    }
}

package me.ariscrates.commands;

import me.ariscrates.ArisCratesPlugin;
import me.ariscrates.managers.Msg;
import me.ariscrates.models.Crate;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class KeyCommand implements CommandExecutor, TabCompleter {

    private final ArisCratesPlugin plugin;

    public KeyCommand(ArisCratesPlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("ariscrates.admin")) { sender.sendMessage(Msg.parse("&cНет доступа.")); return true; }
        if (args.length < 2) {
            sender.sendMessage(Msg.parse("&7Использование: &e/key <ник> <крейт> [количество]"));
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) { sender.sendMessage(Msg.parse("&cИгрок &e" + args[0] + " &cне в сети.")); return true; }
        Crate crate = plugin.getCrateManager().getCrate(args[1]);
        if (crate == null) { sender.sendMessage(Msg.parse("&cКрейт не найден: &e" + args[1])); return true; }
        int amount = 1;
        if (args.length >= 3) {
            try { amount = Integer.parseInt(args[2]); } catch (NumberFormatException e) { amount = 1; }
        }
        if (amount < 1) amount = 1;

        ItemStack key = plugin.getCrateManager().createKey(crate, amount);
        var leftover = target.getInventory().addItem(key);
        for (ItemStack drop : leftover.values()) {
            target.getWorld().dropItemNaturally(target.getLocation(), drop);
        }
        sender.sendMessage(Msg.parse("&aВыдано &e" + amount + " &aключ(ей) от " + crate.displayName() + " &aигроку &e" + target.getName()));
        target.sendMessage(Msg.parse("&aВам выдано &e" + amount + " &aключ(ей) от крейта " + crate.displayName()));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            List<String> names = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) names.add(p.getName());
            return names;
        }
        if (args.length == 2) {
            List<String> ids = new ArrayList<>();
            for (Crate c : plugin.getCrateManager().all()) ids.add(c.id());
            return ids;
        }
        if (args.length == 3) return List.of("1", "5", "10", "32", "64");
        return List.of();
    }
}

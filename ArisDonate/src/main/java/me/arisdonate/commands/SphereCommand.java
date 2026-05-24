package me.arisdonate.commands;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.models.Sphere;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * /sphere shop                       — открыть магазин
 * /sphere list                       — список всех сфер
 * /sphere give <ник> <sphere-id>     — выдать сферу (админ)
 * /sphere coins <ник> add/take/set N — управлять валютой (админ)
 */
public class SphereCommand extends BaseCommand {

    public SphereCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            Player p = requirePlayer(sender);
            if (p == null) return;
            plugin.getSphereShopGui().open(p);
            return;
        }
        switch (args[0].toLowerCase()) {
            case "shop" -> {
                Player p = requirePlayer(sender);
                if (p == null) return;
                plugin.getSphereShopGui().open(p);
            }
            case "list" -> {
                sender.sendMessage(Msg.parse("&6Доступные сферы:"));
                for (Sphere s : plugin.getSphereManager().all()) {
                    sender.sendMessage(Msg.parse(" &f• " + s.gradientName()
                            + " &7id=&e" + s.id() + " &7цена=&e" + s.price()));
                }
            }
            case "give" -> {
                if (!check(sender, "arisdonate.sphere.admin")) return;
                if (args.length < 3) { sender.sendMessage(Msg.parse("&7/sphere give <ник> <id>")); return; }
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) { sender.sendMessage(Msg.parse("&cИгрок не найден.")); return; }
                Sphere s = plugin.getSphereManager().getSphere(args[2]);
                if (s == null) { sender.sendMessage(Msg.parse("&cСфера не найдена.")); return; }
                target.getInventory().addItem(s.toItem(plugin));
                sender.sendMessage(Msg.parse("&aВыдана сфера " + s.gradientName() + " &7игроку &e" + target.getName()));
                target.sendMessage(Msg.parse("&aПолучена сфера " + s.gradientName()));
            }
            case "coins" -> {
                if (!check(sender, "arisdonate.sphere.admin")) return;
                if (args.length < 4) { sender.sendMessage(Msg.parse("&7/sphere coins <ник> <add|take|set> <N>")); return; }
                Player target = Bukkit.getPlayerExact(args[1]);
                if (target == null) { sender.sendMessage(Msg.parse("&cИгрок не найден.")); return; }
                long n;
                try { n = Long.parseLong(args[3]); } catch (NumberFormatException e) {
                    sender.sendMessage(Msg.parse("&cЧисло.")); return;
                }
                switch (args[2].toLowerCase()) {
                    case "add"  -> plugin.getEconomyManager().add(target.getUniqueId(), n);
                    case "take" -> plugin.getEconomyManager().take(target.getUniqueId(), n);
                    case "set"  -> plugin.getEconomyManager().set(target.getUniqueId(), n);
                    default     -> { sender.sendMessage(Msg.parse("&cadd/take/set")); return; }
                }
                long bal = plugin.getEconomyManager().get(target.getUniqueId());
                sender.sendMessage(Msg.parse("&aБаланс &e" + target.getName() + "&a: &6" + bal));
            }
            case "balance", "bal" -> {
                Player p = requirePlayer(sender);
                if (p == null) return;
                long bal = plugin.getEconomyManager().get(p.getUniqueId());
                p.sendMessage(Msg.parse("&6Твой баланс: &e" + bal + " Aris-coins"));
            }
            default -> sender.sendMessage(Msg.parse("&7/sphere shop|list|give|coins|bal"));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) return List.of("shop", "list", "give", "coins", "bal");
        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            List<String> ids = new ArrayList<>();
            for (Sphere s : plugin.getSphereManager().all()) ids.add(s.id());
            return ids;
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("coins")) {
            return List.of("add", "take", "set");
        }
        return List.of();
    }
}

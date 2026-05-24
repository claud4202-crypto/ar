package me.arisdonate.commands.teleport;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import me.arisdonate.util.Players;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TeleportCommand extends BaseCommand {
    public TeleportCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.tp")) return;
        if (args.length == 1) {
            Player p = requirePlayer(sender);
            if (p == null) return;
            Player t = Players.online(args[0]);
            if (t == null) { p.sendMessage(Msg.parse("&cИгрок не в сети.")); return; }
            plugin.getBackManager().store(p);
            p.teleportAsync(t.getLocation());
            p.sendMessage(Msg.parse("&aТелепорт к &e" + t.getName()));
        } else if (args.length == 2) {
            Player who = Players.online(args[0]);
            Player to = Players.online(args[1]);
            if (who == null || to == null) { sender.sendMessage(Msg.parse("&cИгрок не в сети.")); return; }
            plugin.getBackManager().store(who);
            who.teleportAsync(to.getLocation());
            sender.sendMessage(Msg.parse("&a" + who.getName() + " &7→ &a" + to.getName()));
        } else if (args.length >= 3) {
            Player p = requirePlayer(sender);
            if (p == null) return;
            try {
                double x = Double.parseDouble(args[0]);
                double y = Double.parseDouble(args[1]);
                double z = Double.parseDouble(args[2]);
                plugin.getBackManager().store(p);
                p.teleportAsync(new org.bukkit.Location(p.getWorld(), x, y, z));
                p.sendMessage(Msg.parse("&aТелепорт к &e" + x + ", " + y + ", " + z));
            } catch (NumberFormatException e) {
                p.sendMessage(Msg.parse("&cНеверные координаты."));
            }
        } else {
            sender.sendMessage(Msg.parse("&7Использование: &e/tp <ник> [ник2|x y z]"));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 || args.length == 2) return Players.onlineNames(args[args.length - 1]);
        return List.of();
    }
}

package me.arisdonate.commands.teleport;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import me.arisdonate.util.Players;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TpHereCommand extends BaseCommand {
    public TpHereCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.tphere")) return;
        Player p = requirePlayer(sender);
        if (p == null) return;
        if (args.length == 0) { p.sendMessage(Msg.parse("&7Использование: &e/tphere <ник>")); return; }
        Player t = Players.online(args[0]);
        if (t == null) { p.sendMessage(Msg.parse("&cИгрок не в сети.")); return; }
        plugin.getBackManager().store(t);
        t.teleportAsync(p.getLocation());
        sender.sendMessage(Msg.parse("&a" + t.getName() + " телепортирован к вам."));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length == 1 ? Players.onlineNames(args[0]) : List.of();
    }
}

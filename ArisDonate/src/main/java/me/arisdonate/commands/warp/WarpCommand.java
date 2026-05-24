package me.arisdonate.commands.warp;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WarpCommand extends BaseCommand {
    public WarpCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        Player p = requirePlayer(sender);
        if (p == null) return;
        if (args.length == 0) {
            p.sendMessage(Msg.parse("&7Доступные варпы: &e" + String.join(", ", plugin.getWarpManager().listWarps())));
            return;
        }
        Location loc = plugin.getWarpManager().getWarp(args[0]);
        if (loc == null) { p.sendMessage(Msg.parse("&cВарп не найден.")); return; }
        plugin.getBackManager().store(p);
        p.teleportAsync(loc);
        p.sendMessage(Msg.parse("&aТелепорт к варпу &e" + args[0]));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) return new ArrayList<>(plugin.getWarpManager().listWarps());
        return List.of();
    }
}

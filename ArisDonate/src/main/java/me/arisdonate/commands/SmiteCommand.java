package me.arisdonate.commands;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import me.arisdonate.util.Players;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SmiteCommand extends BaseCommand {
    public SmiteCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.smite")) return;
        if (args.length == 0) {
            Player p = requirePlayer(sender);
            if (p == null) return;
            org.bukkit.block.Block b = p.getTargetBlockExact(150);
            org.bukkit.Location l = b != null ? b.getLocation() : p.getLocation();
            p.getWorld().strikeLightning(l);
            p.sendMessage(Msg.parse("&aМолния!"));
            return;
        }
        Player t = Players.online(args[0]);
        if (t == null) { sender.sendMessage(Msg.parse("&cИгрок не в сети.")); return; }
        t.getWorld().strikeLightning(t.getLocation());
        sender.sendMessage(Msg.parse("&aМолния по &e" + t.getName()));
    }
}

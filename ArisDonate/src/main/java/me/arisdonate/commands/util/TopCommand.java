package me.arisdonate.commands.util;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TopCommand extends BaseCommand {
    public TopCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.top")) return;
        Player p = requirePlayer(sender);
        if (p == null) return;
        Location l = p.getLocation();
        int y = p.getWorld().getHighestBlockYAt(l) + 1;
        plugin.getBackManager().store(p);
        p.teleportAsync(new Location(p.getWorld(), l.getX(), y, l.getZ(), l.getYaw(), l.getPitch()));
        p.sendMessage(Msg.parse("&aТелепорт наверх."));
    }
}

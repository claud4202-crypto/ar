package me.arisdonate.commands.teleport;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BackCommand extends BaseCommand {
    public BackCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.back")) return;
        Player p = requirePlayer(sender);
        if (p == null) return;
        Location loc = plugin.getBackManager().get(p.getUniqueId());
        if (loc == null) { p.sendMessage(Msg.parse("&cНет предыдущей локации.")); return; }
        Location current = p.getLocation();
        plugin.getBackManager().store(p, current);
        p.teleportAsync(loc);
        p.sendMessage(Msg.parse("&aВозврат к предыдущей точке."));
    }
}

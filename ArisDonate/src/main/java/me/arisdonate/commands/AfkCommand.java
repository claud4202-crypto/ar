package me.arisdonate.commands;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AfkCommand extends BaseCommand {
    public AfkCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        Player p = requirePlayer(sender);
        if (p == null) return;
        boolean on = plugin.getAfkManager().toggle(p);
        Bukkit.broadcast(Msg.parse("&7* &e" + p.getName() + " &7теперь " + (on ? "AFK." : "не AFK.")));
    }
}

package me.arisdonate.commands.donate;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DonateCommand extends BaseCommand {
    public DonateCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        Player p = requirePlayer(sender);
        if (p == null) return;
        plugin.getDonateGui().open(p);
    }
}

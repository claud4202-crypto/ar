package me.arisdonate.commands.util;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class TpsCommand extends BaseCommand {
    public TpsCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        double[] tps = Bukkit.getTPS();
        sender.sendMessage(Msg.parse(String.format("&6TPS: &a%.2f &7/ &a%.2f &7/ &a%.2f", tps[0], tps[1], tps[2])));
    }
}

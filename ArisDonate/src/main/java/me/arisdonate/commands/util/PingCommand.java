package me.arisdonate.commands.util;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import me.arisdonate.util.Players;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PingCommand extends BaseCommand {
    public PingCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        Player target = args.length >= 1 ? Players.online(args[0]) : (sender instanceof Player ? (Player) sender : null);
        if (target == null) { sender.sendMessage(Msg.parse("&cУкажите игрока.")); return; }
        sender.sendMessage(Msg.parse("&6Пинг &e" + target.getName() + "&6: &a" + target.getPing() + "&7мс"));
    }
}

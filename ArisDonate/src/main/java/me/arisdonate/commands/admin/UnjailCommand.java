package me.arisdonate.commands.admin;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import me.arisdonate.util.Players;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnjailCommand extends BaseCommand {
    public UnjailCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.jail")) return;
        if (args.length == 0) { sender.sendMessage(Msg.parse("&7Использование: &e/unjail <ник>")); return; }
        OfflinePlayer t = Players.offline(args[0]);
        plugin.getJailManager().unjail(t.getUniqueId());
        Player online = Players.online(args[0]);
        if (online != null) {
            online.sendMessage(Msg.parse("&aВас выпустили из джейла."));
        }
        sender.sendMessage(Msg.parse("&aСнят джейл: &e" + args[0]));
    }
}

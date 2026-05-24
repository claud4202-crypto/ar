package me.arisdonate.commands.util;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import me.arisdonate.util.Players;
import me.arisdonate.util.TimeUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class SeenCommand extends BaseCommand {
    public SeenCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) { sender.sendMessage(Msg.parse("&7Использование: &e/seen <ник>")); return; }
        OfflinePlayer p = Players.offline(args[0]);
        if (p == null || !p.hasPlayedBefore() && !p.isOnline()) { sender.sendMessage(Msg.parse("&cИгрок не найден.")); return; }
        if (p.isOnline()) { sender.sendMessage(Msg.parse("&a" + args[0] + " &7сейчас в сети.")); return; }
        long ago = System.currentTimeMillis() - p.getLastSeen();
        sender.sendMessage(Msg.parse("&e" + args[0] + " &7был в сети &f" + TimeUtil.fmt(ago) + " &7назад."));
    }
}

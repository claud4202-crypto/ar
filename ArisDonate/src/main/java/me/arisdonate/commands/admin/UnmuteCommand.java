package me.arisdonate.commands.admin;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import me.arisdonate.util.Players;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class UnmuteCommand extends BaseCommand {
    public UnmuteCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.mute")) return;
        if (args.length == 0) { sender.sendMessage(Msg.parse("&7Использование: &e/unmute <ник>")); return; }
        OfflinePlayer t = Players.offline(args[0]);
        if (t == null) { sender.sendMessage(Msg.parse("&cИгрок не найден.")); return; }
        plugin.getMuteManager().unmute(t.getUniqueId());
        sender.sendMessage(Msg.parse("&aСнят мьют: &e" + args[0]));
    }
}

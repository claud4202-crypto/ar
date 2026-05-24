package me.arisdonate.commands.admin;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import me.arisdonate.util.Players;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarnCommand extends BaseCommand {
    public WarnCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.warn")) return;
        if (args.length < 2) { sender.sendMessage(Msg.parse("&7Использование: &e/warn <ник> <причина>")); return; }
        Player t = Players.online(args[0]);
        if (t == null) { sender.sendMessage(Msg.parse("&cИгрок не в сети.")); return; }
        String reason = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
        t.sendMessage(Msg.parse("&c⚠ Вам выдано предупреждение: &f" + reason));
        sender.sendMessage(Msg.parse("&aWarn: &e" + t.getName()));
    }
}

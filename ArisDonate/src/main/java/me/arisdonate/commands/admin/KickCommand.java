package me.arisdonate.commands.admin;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import me.arisdonate.util.Players;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickCommand extends BaseCommand {
    public KickCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.kick")) return;
        if (args.length == 0) { sender.sendMessage(Msg.parse("&7Использование: &e/kick <ник> [причина]")); return; }
        Player t = Players.online(args[0]);
        if (t == null) { sender.sendMessage(Msg.parse("&cИгрок не в сети.")); return; }
        String reason = args.length > 1 ? String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length)) : "Кикнут";
        t.kick(Msg.parse("&cВы кикнуты&7. &fПричина: &e" + reason));
        sender.sendMessage(Msg.parse("&aКик: &e" + args[0]));
    }
}

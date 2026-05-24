package me.arisdonate.commands.admin;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import me.arisdonate.util.Players;
import me.arisdonate.util.TimeUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MuteCommand extends BaseCommand {
    public MuteCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.mute")) return;
        if (args.length == 0) { sender.sendMessage(Msg.parse("&7Использование: &e/" + label + " <ник> [время]")); return; }
        OfflinePlayer t = Players.offline(args[0]);
        if (t == null) { sender.sendMessage(Msg.parse("&cИгрок не найден.")); return; }
        long until = 0L;
        if (args.length >= 2) {
            long ms = TimeUtil.parseDuration(args[1]);
            if (ms > 0) until = System.currentTimeMillis() + ms;
        }
        plugin.getMuteManager().mute(t.getUniqueId(), until);
        Player online = Players.online(args[0]);
        if (online != null) online.sendMessage(Msg.parse("&cВы заглушены."));
        sender.sendMessage(Msg.parse("&aМьют: &e" + args[0]));
    }
}

package me.arisdonate.commands.admin;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import me.arisdonate.util.Players;
import me.arisdonate.util.TimeUtil;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;

public class BanCommand extends BaseCommand {
    public BanCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.ban")) return;
        if (args.length == 0) { sender.sendMessage(Msg.parse("&7Использование: &e/" + label + " <ник> [время] [причина]")); return; }
        OfflinePlayer t = Players.offline(args[0]);
        if (t == null) { sender.sendMessage(Msg.parse("&cИгрок не найден.")); return; }

        Date expires = null;
        String reason = "Нарушение правил";
        int idx = 1;

        if (label.equalsIgnoreCase("tempban") && args.length >= 2) {
            long ms = TimeUtil.parseDuration(args[1]);
            if (ms > 0) {
                expires = new Date(System.currentTimeMillis() + ms);
                idx = 2;
            }
        }
        if (args.length > idx) {
            reason = String.join(" ", java.util.Arrays.copyOfRange(args, idx, args.length));
        }

        Bukkit.getBanList(BanList.Type.NAME).addBan(t.getName(), reason, expires, sender.getName());
        Player online = Players.online(args[0]);
        if (online != null) online.kick(Msg.parse("&cВы забанены&7. &fПричина: &e" + reason));
        sender.sendMessage(Msg.parse("&a" + args[0] + " &7забанен" + (expires == null ? " &cперманентно" : " до &e" + expires) + "&7. Причина: &f" + reason));
    }
}

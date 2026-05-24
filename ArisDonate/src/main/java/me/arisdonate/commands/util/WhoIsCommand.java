package me.arisdonate.commands.util;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.models.DonateRank;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import me.arisdonate.util.Players;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WhoIsCommand extends BaseCommand {
    public WhoIsCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) { sender.sendMessage(Msg.parse("&7Использование: &e/whois <ник>")); return; }
        Player t = Players.online(args[0]);
        if (t == null) { sender.sendMessage(Msg.parse("&cИгрок не в сети.")); return; }
        DonateRank rank = plugin.getDonateManager().getPlayerRank(t.getName());
        sender.sendMessage(Msg.parse("&6── &e" + t.getName() + " &6──"));
        sender.sendMessage(Msg.parse("&7Донат: " + (rank == null ? "&8нет" : rank.gradientName())));
        sender.sendMessage(Msg.parse("&7Здоровье: &c" + (int) t.getHealth()));
        sender.sendMessage(Msg.parse("&7Голод: &6" + t.getFoodLevel()));
        sender.sendMessage(Msg.parse("&7Мир: &e" + t.getWorld().getName()));
        sender.sendMessage(Msg.parse(String.format("&7Координаты: &e%.0f %.0f %.0f", t.getX(), t.getY(), t.getZ())));
        sender.sendMessage(Msg.parse("&7GameMode: &e" + t.getGameMode().name().toLowerCase()));
        sender.sendMessage(Msg.parse("&7Пинг: &a" + t.getPing() + "мс"));
    }
}

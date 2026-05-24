package me.arisdonate.commands;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import me.arisdonate.util.Players;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class MsgCommand extends BaseCommand {
    public MsgCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Msg.parse("&7Использование: &e/" + label + " <ник> <сообщение>"));
            return;
        }
        Player target = Players.online(args[0]);
        if (target == null) { sender.sendMessage(Msg.parse("&cИгрок не в сети.")); return; }
        String text = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
        String fromName = sender instanceof Player ? ((Player) sender).getName() : "[Console]";

        target.sendMessage(Msg.parse("&8[&dот &f" + fromName + "&8] &7" + text));
        sender.sendMessage(Msg.parse("&8[&dк &f" + target.getName() + "&8] &7" + text));

        // socialspy
        for (UUID id : plugin.getMessageManager().socialSpyMembers()) {
            Player spy = Bukkit.getPlayer(id);
            if (spy == null || spy.equals(target) || spy.getName().equals(fromName)) continue;
            spy.sendMessage(Msg.parse("&8[spy] &f" + fromName + " &7→ &f" + target.getName() + "&7: " + text));
        }

        plugin.getMessageManager().setLast(sender, target);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return args.length == 1 ? Players.onlineNames(args[0]) : List.of();
    }
}

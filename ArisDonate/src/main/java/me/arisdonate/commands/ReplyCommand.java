package me.arisdonate.commands;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ReplyCommand extends BaseCommand {
    public ReplyCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        Player p = requirePlayer(sender);
        if (p == null) return;
        if (args.length == 0) { p.sendMessage(Msg.parse("&7Использование: &e/r <сообщение>")); return; }
        UUID last = plugin.getMessageManager().getLast(p.getUniqueId());
        if (last == null) { p.sendMessage(Msg.parse("&cНекому отвечать.")); return; }
        Player target = Bukkit.getPlayer(last);
        if (target == null) { p.sendMessage(Msg.parse("&cСобеседник оффлайн.")); return; }
        String text = String.join(" ", args);
        target.sendMessage(Msg.parse("&8[&dот &f" + p.getName() + "&8] &7" + text));
        p.sendMessage(Msg.parse("&8[&dк &f" + target.getName() + "&8] &7" + text));
        plugin.getMessageManager().setLast(p, target);
    }
}

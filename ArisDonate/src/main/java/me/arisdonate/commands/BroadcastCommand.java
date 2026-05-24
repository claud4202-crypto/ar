package me.arisdonate.commands;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class BroadcastCommand extends BaseCommand {
    public BroadcastCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.broadcast")) return;
        if (args.length == 0) { sender.sendMessage(Msg.parse("&7Использование: &e/bc <текст>")); return; }
        String text = String.join(" ", args);
        Bukkit.broadcast(Msg.parse("&7[&6ОБЪЯВЛЕНИЕ&7] &f" + text));
    }
}

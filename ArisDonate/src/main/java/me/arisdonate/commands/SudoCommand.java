package me.arisdonate.commands;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import me.arisdonate.util.Players;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SudoCommand extends BaseCommand {
    public SudoCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.sudo")) return;
        if (args.length < 2) { sender.sendMessage(Msg.parse("&7Использование: &e/sudo <ник> <текст_или_/команда>")); return; }
        Player t = Players.online(args[0]);
        if (t == null) { sender.sendMessage(Msg.parse("&cИгрок не в сети.")); return; }
        String rest = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
        if (rest.startsWith("/")) {
            t.performCommand(rest.substring(1));
        } else {
            t.chat(rest);
        }
        sender.sendMessage(Msg.parse("&aSudo выполнен."));
    }
}

package me.arisdonate.commands;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearChatCommand extends BaseCommand {
    public ClearChatCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.clearchat")) return;
        for (Player pl : Bukkit.getOnlinePlayers()) {
            for (int i = 0; i < 100; i++) pl.sendMessage(" ");
        }
        Bukkit.broadcast(Msg.parse("&7Чат очищен администратором &e" + sender.getName()));
    }
}

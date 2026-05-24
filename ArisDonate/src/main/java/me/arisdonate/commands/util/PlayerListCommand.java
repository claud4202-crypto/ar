package me.arisdonate.commands.util;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class PlayerListCommand extends BaseCommand {
    public PlayerListCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        String list = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.joining(", "));
        sender.sendMessage(Msg.parse("&6Онлайн (&e" + Bukkit.getOnlinePlayers().size() + "&6): &7" + list));
    }
}

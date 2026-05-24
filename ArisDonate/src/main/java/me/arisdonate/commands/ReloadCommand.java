package me.arisdonate.commands;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends BaseCommand {
    public ReloadCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.admin")) return;
        plugin.reloadConfig();
        if (plugin.getPermissionService() != null) plugin.getPermissionService().refreshAll();
        sender.sendMessage(Msg.parse("&aКонфигурация перечитана и пермы донатов перевыданы."));
    }
}

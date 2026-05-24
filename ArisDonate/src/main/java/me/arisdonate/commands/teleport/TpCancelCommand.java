package me.arisdonate.commands.teleport;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class TpCancelCommand extends BaseCommand {
    public TpCancelCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(Msg.parse("&7Активные запросы автоматически очищаются через 2 минуты."));
    }
}

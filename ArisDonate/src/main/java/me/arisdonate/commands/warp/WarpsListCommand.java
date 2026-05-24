package me.arisdonate.commands.warp;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class WarpsListCommand extends BaseCommand {
    public WarpsListCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        var list = plugin.getWarpManager().listWarps();
        if (list.isEmpty()) {
            sender.sendMessage(Msg.parse("&7Варпы не созданы."));
            return;
        }
        sender.sendMessage(Msg.parse("&6Варпы (&f" + list.size() + "&6):"));
        sender.sendMessage(Msg.parse("&7" + String.join(", ", list)));
    }
}

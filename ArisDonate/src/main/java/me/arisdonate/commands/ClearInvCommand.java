package me.arisdonate.commands;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import me.arisdonate.util.Players;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearInvCommand extends BaseCommand {
    public ClearInvCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.clearinv")) return;
        Player target = args.length >= 1 ? Players.online(args[0]) : (sender instanceof Player ? (Player) sender : null);
        if (target == null) { sender.sendMessage(Msg.parse("&cУкажите игрока.")); return; }
        target.getInventory().clear();
        target.sendMessage(Msg.parse("&aИнвентарь очищен."));
        if (!target.equals(sender)) sender.sendMessage(Msg.parse("&aОчищен инвентарь &e" + target.getName()));
    }
}

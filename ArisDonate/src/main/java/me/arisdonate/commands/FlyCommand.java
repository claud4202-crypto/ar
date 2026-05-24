package me.arisdonate.commands;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import me.arisdonate.util.Players;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand extends BaseCommand {
    public FlyCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.fly")) return;
        Player target = args.length >= 1 ? Players.online(args[0]) : (sender instanceof Player ? (Player) sender : null);
        if (target == null) { sender.sendMessage(Msg.parse("&cУкажите игрока.")); return; }
        boolean newState = !target.getAllowFlight();
        target.setAllowFlight(newState);
        target.setFlying(newState);
        target.sendMessage(Msg.parse(newState ? "&aРежим полёта включён." : "&7Режим полёта выключен."));
        if (!target.equals(sender)) sender.sendMessage(Msg.parse("&a" + target.getName() + ": fly=" + newState));
    }
}

package me.arisdonate.commands;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import me.arisdonate.util.Players;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HealCommand extends BaseCommand {
    public HealCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.heal")) return;
        Player target = args.length >= 1 ? Players.online(args[0]) : (sender instanceof Player ? (Player) sender : null);
        if (target == null) { sender.sendMessage(Msg.parse("&cУкажите игрока.")); return; }
        double max = target.getAttribute(Attribute.MAX_HEALTH).getValue();
        target.setHealth(max);
        target.setFireTicks(0);
        target.sendMessage(Msg.parse("&aВы исцелены."));
        if (!target.equals(sender)) sender.sendMessage(Msg.parse("&aHeal: &e" + target.getName()));
    }
}

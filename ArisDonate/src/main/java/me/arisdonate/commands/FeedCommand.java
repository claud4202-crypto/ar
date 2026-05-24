package me.arisdonate.commands;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import me.arisdonate.util.Players;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FeedCommand extends BaseCommand {
    public FeedCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.feed")) return;
        Player target = args.length >= 1 ? Players.online(args[0]) : (sender instanceof Player ? (Player) sender : null);
        if (target == null) { sender.sendMessage(Msg.parse("&cУкажите игрока.")); return; }
        target.setFoodLevel(20);
        target.setSaturation(20);
        target.sendMessage(Msg.parse("&aВы наелись."));
    }
}

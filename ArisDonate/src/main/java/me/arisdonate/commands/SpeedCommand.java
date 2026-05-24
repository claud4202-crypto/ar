package me.arisdonate.commands;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpeedCommand extends BaseCommand {
    public SpeedCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.speed")) return;
        Player p = requirePlayer(sender);
        if (p == null) return;
        if (args.length == 0) { p.sendMessage(Msg.parse("&7Использование: &e/" + label + " <1-10>")); return; }
        try {
            float v = Math.max(1, Math.min(10, Float.parseFloat(args[0])));
            float normalized = v / 10f;
            if (label.equalsIgnoreCase("walkspeed")) p.setWalkSpeed(normalized);
            else if (label.equalsIgnoreCase("flyspeed")) p.setFlySpeed(normalized);
            else {
                p.setFlySpeed(normalized);
                p.setWalkSpeed(normalized);
            }
            p.sendMessage(Msg.parse("&aСкорость установлена в &e" + v));
        } catch (NumberFormatException e) {
            p.sendMessage(Msg.parse("&cНеверное число."));
        }
    }
}

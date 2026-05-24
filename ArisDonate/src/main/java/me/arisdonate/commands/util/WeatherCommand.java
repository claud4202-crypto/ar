package me.arisdonate.commands.util;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WeatherCommand extends BaseCommand {
    public WeatherCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.weather")) return;
        if (args.length == 0) { sender.sendMessage(Msg.parse("&7Использование: &e/weather <sun|rain|thunder>")); return; }
        World w = sender instanceof Player ? ((Player) sender).getWorld() : org.bukkit.Bukkit.getWorlds().get(0);
        switch (args[0].toLowerCase()) {
            case "sun": case "clear":
                w.setStorm(false); w.setThundering(false); break;
            case "rain":
                w.setStorm(true); w.setThundering(false); break;
            case "thunder": case "storm":
                w.setStorm(true); w.setThundering(true); break;
            default: sender.sendMessage(Msg.parse("&cНеизвестная погода.")); return;
        }
        sender.sendMessage(Msg.parse("&aПогода: &e" + args[0]));
    }
}

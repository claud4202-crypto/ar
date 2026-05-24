package me.arisdonate.commands.util;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WeatherShortcutCommand extends BaseCommand {
    private final String mode;
    public WeatherShortcutCommand(ArisDonatePlugin plugin, String mode) { super(plugin); this.mode = mode; }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.weather")) return;
        World w = sender instanceof Player ? ((Player) sender).getWorld() : org.bukkit.Bukkit.getWorlds().get(0);
        switch (mode) {
            case "sun": w.setStorm(false); w.setThundering(false); break;
            case "rain": w.setStorm(true); w.setThundering(false); break;
            case "thunder": w.setStorm(true); w.setThundering(true); break;
        }
        sender.sendMessage(Msg.parse("&aПогода: &e" + mode));
    }
}

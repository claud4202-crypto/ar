package me.arisdonate.commands.util;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TimeShortcutCommand extends BaseCommand {
    private final String mode;
    public TimeShortcutCommand(ArisDonatePlugin plugin, String mode) { super(plugin); this.mode = mode; }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.time")) return;
        World w = sender instanceof Player ? ((Player) sender).getWorld() : org.bukkit.Bukkit.getWorlds().get(0);
        long t = mode.equals("day") ? 1000 : 13000;
        w.setTime(t);
        sender.sendMessage(Msg.parse("&aВремя: &e" + mode));
    }
}

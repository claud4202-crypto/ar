package me.arisdonate.commands.util;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TimeCommand extends BaseCommand {
    public TimeCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.time")) return;
        if (args.length == 0) { sender.sendMessage(Msg.parse("&7Использование: &e/time <day|night|noon|midnight|число>")); return; }
        World w = sender instanceof Player ? ((Player) sender).getWorld() : org.bukkit.Bukkit.getWorlds().get(0);
        long t;
        switch (args[0].toLowerCase()) {
            case "day": t = 1000; break;
            case "night": t = 13000; break;
            case "noon": t = 6000; break;
            case "midnight": t = 18000; break;
            default:
                try { t = Long.parseLong(args[0]); }
                catch (NumberFormatException e) { sender.sendMessage(Msg.parse("&cНеверное время.")); return; }
        }
        w.setTime(t);
        sender.sendMessage(Msg.parse("&aВремя установлено: &e" + t));
    }
}

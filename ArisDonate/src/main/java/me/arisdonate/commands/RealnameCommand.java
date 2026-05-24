package me.arisdonate.commands;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class RealnameCommand extends BaseCommand {
    public RealnameCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) { sender.sendMessage(Msg.parse("&7Использование: &e/realname <отображаемое_имя>")); return; }
        String q = args[0];
        for (Player p : Bukkit.getOnlinePlayers()) {
            String disp = PlainTextComponentSerializer.plainText().serialize(p.displayName());
            if (disp.equalsIgnoreCase(q) || p.getName().equalsIgnoreCase(q)) {
                sender.sendMessage(Msg.parse("&a" + disp + " &7= &e" + p.getName()));
                return;
            }
        }
        sender.sendMessage(Msg.parse("&cНе найдено."));
    }
}

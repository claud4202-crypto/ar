package me.arisdonate.commands;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class SimpleTextCommand extends BaseCommand {
    private final String key;
    public SimpleTextCommand(ArisDonatePlugin plugin, String key) { super(plugin); this.key = key; }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        List<String> lines = plugin.getConfig().getStringList("texts." + key);
        if (lines.isEmpty()) {
            sender.sendMessage(Msg.parse("&7Текст &e" + key + " &7не задан в config.yml"));
            return;
        }
        for (String line : lines) sender.sendMessage(Msg.parse(line));
    }
}

package me.arisdonate.commands;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CommandSpyCommand extends BaseCommand {
    public static final Set<UUID> SPIES = new HashSet<>();

    public CommandSpyCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.commandspy")) return;
        Player p = requirePlayer(sender);
        if (p == null) return;
        if (!SPIES.add(p.getUniqueId())) {
            SPIES.remove(p.getUniqueId());
            p.sendMessage(Msg.parse("&7CommandSpy выключен."));
        } else {
            p.sendMessage(Msg.parse("&aCommandSpy включён."));
        }
    }
}

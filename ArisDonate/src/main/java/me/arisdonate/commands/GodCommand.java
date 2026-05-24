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

public class GodCommand extends BaseCommand {

    private static final Set<UUID> gods = new HashSet<>();

    public GodCommand(ArisDonatePlugin plugin) { super(plugin); }

    public static boolean isGod(UUID id) { return gods.contains(id); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.god")) return;
        Player p = requirePlayer(sender);
        if (p == null) return;
        if (gods.add(p.getUniqueId())) {
            p.setInvulnerable(true);
            p.sendMessage(Msg.parse("&aРежим бога включён."));
        } else {
            gods.remove(p.getUniqueId());
            p.setInvulnerable(false);
            p.sendMessage(Msg.parse("&7Режим бога выключен."));
        }
    }
}

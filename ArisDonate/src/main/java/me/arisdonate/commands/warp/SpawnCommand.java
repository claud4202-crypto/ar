package me.arisdonate.commands.warp;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand extends BaseCommand {
    public SpawnCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        Player p = requirePlayer(sender);
        if (p == null) return;
        Location loc = plugin.getSpawnManager().getSpawn();
        if (loc == null) { p.sendMessage(Msg.parse("&cСпавн не установлен.")); return; }
        plugin.getBackManager().store(p);
        p.teleportAsync(loc);
        p.sendMessage(Msg.parse("&aТелепорт на спавн."));
    }
}

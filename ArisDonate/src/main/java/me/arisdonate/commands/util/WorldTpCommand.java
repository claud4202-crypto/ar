package me.arisdonate.commands.util;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldTpCommand extends BaseCommand {
    public WorldTpCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.worldtp")) return;
        Player p = requirePlayer(sender);
        if (p == null) return;
        if (args.length == 0) {
            StringBuilder sb = new StringBuilder();
            for (World w : Bukkit.getWorlds()) sb.append(w.getName()).append(", ");
            p.sendMessage(Msg.parse("&7Миры: &e" + sb));
            return;
        }
        World w = Bukkit.getWorld(args[0]);
        if (w == null) { p.sendMessage(Msg.parse("&cМир не найден.")); return; }
        plugin.getBackManager().store(p);
        p.teleportAsync(w.getSpawnLocation());
        p.sendMessage(Msg.parse("&aТелепорт в мир &e" + w.getName()));
    }
}

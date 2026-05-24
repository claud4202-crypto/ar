package me.arisdonate.commands.warp;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnCommand extends BaseCommand {
    public SetSpawnCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.setspawn")) return; // только админ
        Player p = requirePlayer(sender);
        if (p == null) return;
        plugin.getSpawnManager().setSpawn(p.getLocation());
        p.sendMessage(Msg.parse("&aСпавн установлен."));
    }
}

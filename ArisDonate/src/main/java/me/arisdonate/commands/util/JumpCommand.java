package me.arisdonate.commands.util;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JumpCommand extends BaseCommand {
    public JumpCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.jump")) return;
        Player p = requirePlayer(sender);
        if (p == null) return;
        Block b = p.getTargetBlockExact(150);
        if (b == null) { p.sendMessage(Msg.parse("&cНет блока в зоне видимости.")); return; }
        Location loc = b.getLocation().add(0.5, 1, 0.5);
        loc.setYaw(p.getLocation().getYaw());
        loc.setPitch(p.getLocation().getPitch());
        plugin.getBackManager().store(p);
        p.teleportAsync(loc);
        p.sendMessage(Msg.parse("&aJump!"));
    }
}

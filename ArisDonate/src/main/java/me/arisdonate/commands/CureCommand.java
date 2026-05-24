package me.arisdonate.commands;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class CureCommand extends BaseCommand {
    public CureCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.cure")) return;
        Player p = requirePlayer(sender);
        if (p == null) return;
        for (PotionEffect e : p.getActivePotionEffects()) p.removePotionEffect(e.getType());
        p.sendMessage(Msg.parse("&aЭффекты очищены."));
    }
}

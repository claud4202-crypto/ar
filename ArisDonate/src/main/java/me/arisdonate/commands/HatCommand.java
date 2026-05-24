package me.arisdonate.commands;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HatCommand extends BaseCommand {
    public HatCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.hat")) return;
        Player p = requirePlayer(sender);
        if (p == null) return;
        ItemStack hand = p.getInventory().getItemInMainHand();
        if (hand == null || hand.getType().isAir()) { p.sendMessage(Msg.parse("&cПусто в руке.")); return; }
        ItemStack helmet = p.getInventory().getHelmet();
        p.getInventory().setHelmet(hand.clone());
        p.getInventory().setItemInMainHand(helmet);
        p.sendMessage(Msg.parse("&aШляпа надета."));
    }
}

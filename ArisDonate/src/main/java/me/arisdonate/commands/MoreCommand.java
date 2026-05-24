package me.arisdonate.commands;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MoreCommand extends BaseCommand {
    public MoreCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.more")) return;
        Player p = requirePlayer(sender);
        if (p == null) return;
        ItemStack it = p.getInventory().getItemInMainHand();
        if (it == null || it.getType().isAir()) { p.sendMessage(Msg.parse("&cПусто в руке.")); return; }
        it.setAmount(it.getType().getMaxStackSize());
        p.sendMessage(Msg.parse("&aСтак увеличен."));
    }
}

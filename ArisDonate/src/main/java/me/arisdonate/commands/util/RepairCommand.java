package me.arisdonate.commands.util;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class RepairCommand extends BaseCommand {
    public RepairCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.repair")) return;
        Player p = requirePlayer(sender);
        if (p == null) return;
        boolean all = args.length > 0 && args[0].equalsIgnoreCase("all");
        int n = 0;
        if (all) {
            for (ItemStack it : p.getInventory().getContents()) n += repair(it);
            for (ItemStack it : p.getInventory().getArmorContents()) n += repair(it);
        } else {
            n += repair(p.getInventory().getItemInMainHand());
        }
        p.sendMessage(Msg.parse("&aПочинено предметов: &e" + n));
    }

    private int repair(ItemStack it) {
        if (it == null) return 0;
        ItemMeta im = it.getItemMeta();
        if (!(im instanceof Damageable d)) return 0;
        if (d.getDamage() == 0) return 0;
        d.setDamage(0);
        it.setItemMeta(im);
        return 1;
    }
}

package me.arisdonate.commands;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemMeCommand extends BaseCommand {
    public ItemMeCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.item")) return;
        Player p = requirePlayer(sender);
        if (p == null) return;
        if (args.length == 0) { p.sendMessage(Msg.parse("&7Использование: &e/i <предмет> [кол]")); return; }
        Material m = Material.matchMaterial(args[0]);
        if (m == null) { p.sendMessage(Msg.parse("&cПредмет не найден.")); return; }
        int n = args.length > 1 ? safe(args[1], 1) : 1;
        p.getInventory().addItem(new ItemStack(m, n));
        p.sendMessage(Msg.parse("&aПолучено &e" + n + " " + m.name()));
    }

    private int safe(String s, int d) { try { return Integer.parseInt(s); } catch (Exception e) { return d; } }
}

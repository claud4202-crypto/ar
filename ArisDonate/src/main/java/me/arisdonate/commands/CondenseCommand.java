package me.arisdonate.commands;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Map;

public class CondenseCommand extends BaseCommand {
    public CondenseCommand(ArisDonatePlugin plugin) { super(plugin); }

    private static final Map<Material, Material> R = Map.ofEntries(
            Map.entry(Material.IRON_INGOT, Material.IRON_BLOCK),
            Map.entry(Material.GOLD_INGOT, Material.GOLD_BLOCK),
            Map.entry(Material.DIAMOND, Material.DIAMOND_BLOCK),
            Map.entry(Material.EMERALD, Material.EMERALD_BLOCK),
            Map.entry(Material.NETHERITE_INGOT, Material.NETHERITE_BLOCK),
            Map.entry(Material.LAPIS_LAZULI, Material.LAPIS_BLOCK),
            Map.entry(Material.REDSTONE, Material.REDSTONE_BLOCK),
            Map.entry(Material.COAL, Material.COAL_BLOCK),
            Map.entry(Material.RAW_IRON, Material.RAW_IRON_BLOCK),
            Map.entry(Material.RAW_GOLD, Material.RAW_GOLD_BLOCK),
            Map.entry(Material.RAW_COPPER, Material.RAW_COPPER_BLOCK),
            Map.entry(Material.COPPER_INGOT, Material.COPPER_BLOCK),
            Map.entry(Material.AMETHYST_SHARD, Material.AMETHYST_BLOCK)
    );

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        Player p = requirePlayer(sender);
        if (p == null) return;
        PlayerInventory inv = p.getInventory();
        int count = 0;
        for (Map.Entry<Material, Material> e : R.entrySet()) {
            int qty = 0;
            for (ItemStack it : inv.getContents()) {
                if (it != null && it.getType() == e.getKey()) qty += it.getAmount();
            }
            int blocks = qty / 9;
            if (blocks == 0) continue;
            inv.removeItem(new ItemStack(e.getKey(), blocks * 9));
            inv.addItem(new ItemStack(e.getValue(), blocks));
            count += blocks;
        }
        p.sendMessage(Msg.parse("&aСжато блоков: &e" + count));
    }
}

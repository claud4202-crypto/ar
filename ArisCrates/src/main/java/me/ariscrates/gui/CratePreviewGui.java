package me.ariscrates.gui;

import me.ariscrates.ArisCratesPlugin;
import me.ariscrates.managers.Msg;
import me.ariscrates.models.Crate;
import me.ariscrates.models.CrateReward;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CratePreviewGui {

    public static final String TITLE_PREFIX = "Награды: ";

    private final ArisCratesPlugin plugin;

    public CratePreviewGui(ArisCratesPlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player player, Crate crate) {
        int size = Math.min(54, ((crate.rewards().size() / 9) + 1) * 9);
        if (size < 9) size = 9;

        Inventory inv = Bukkit.createInventory(null, size,
                Msg.parse("&6" + TITLE_PREFIX + crate.displayName()));

        double totalChance = crate.rewards().stream().mapToDouble(CrateReward::chance).sum();

        int slot = 0;
        for (CrateReward r : crate.rewards()) {
            if (slot >= size - 1) break;
            ItemStack display = r.item().clone();
            ItemMeta im = display.getItemMeta();
            im.displayName(Msg.parse(r.display()));
            double pct = (r.chance() / totalChance) * 100;
            List<Component> lore = new ArrayList<>();
            lore.add(Msg.parse("&7Шанс: &e" + String.format("%.1f", pct) + "%"));
            lore.add(Msg.parse("&7Количество: &f" + r.item().getAmount()));
            im.lore(lore);
            display.setItemMeta(im);
            inv.setItem(slot++, display);
        }

        // Close button
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta cm = close.getItemMeta();
        cm.displayName(Msg.parse("&c&lЗакрыть"));
        close.setItemMeta(cm);
        inv.setItem(size - 1, close);

        player.openInventory(inv);
    }
}

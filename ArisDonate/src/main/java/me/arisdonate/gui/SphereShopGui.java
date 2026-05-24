package me.arisdonate.gui;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.models.Sphere;
import me.arisdonate.util.Msg;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Магазин сфер: показывает все доступные сферы как купляемые предметы.
 * Клик по сфере покупает её за Aris-coins.
 */
public class SphereShopGui {

    private final ArisDonatePlugin plugin;

    public SphereShopGui(ArisDonatePlugin plugin) { this.plugin = plugin; }

    public void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 54,
                Msg.parse("<grad:#FFD700:#FF1493>✦ МАГАЗИН СФЕР ✦</grad>"));

        ItemStack pane = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
        ItemMeta pm = pane.getItemMeta();
        pm.displayName(Component.text(" "));
        pane.setItemMeta(pm);
        for (int slot = 0; slot < 54; slot++) inv.setItem(slot, pane);

        int[] slots = {
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34
        };
        int i = 0;
        for (Sphere s : plugin.getSphereManager().all()) {
            if (i >= slots.length) break;
            inv.setItem(slots[i++], buildIcon(p, s));
        }

        // баланс
        ItemStack balance = new ItemStack(Material.SUNFLOWER);
        ItemMeta bm = balance.getItemMeta();
        bm.displayName(Msg.parse("<grad:#FFD700:#FFA500>Твой баланс</grad>"));
        long bal = plugin.getEconomyManager().get(p.getUniqueId());
        List<Component> blore = new ArrayList<>();
        blore.add(Msg.parse("&7Aris-coins: &e" + bal));
        blore.add(Component.empty());
        blore.add(Msg.parse("&8Заработать coins можно у админа,"));
        blore.add(Msg.parse("&8или получив их при покупке доната."));
        bm.lore(blore);
        balance.setItemMeta(bm);
        inv.setItem(4, balance);

        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta cm = close.getItemMeta();
        cm.displayName(Msg.parse("&c&lЗакрыть"));
        close.setItemMeta(cm);
        inv.setItem(49, close);

        p.openInventory(inv);
    }

    private ItemStack buildIcon(Player p, Sphere s) {
        ItemStack it = s.toItem(plugin);
        ItemMeta im = it.getItemMeta();
        List<Component> lore = im.lore();
        if (lore == null) lore = new ArrayList<>();
        else lore = new ArrayList<>(lore);
        lore.add(Component.empty());
        long bal = plugin.getEconomyManager().get(p.getUniqueId());
        lore.add(Msg.parse("&6Цена: &e" + s.price() + " Aris-coins"));
        if (s.price() <= 0) {
            lore.add(Msg.parse("&8(доступна только из кита)"));
        } else if (bal >= s.price()) {
            lore.add(Msg.parse("&aКликни, чтобы купить."));
        } else {
            lore.add(Msg.parse("&cНе хватает coins: &7" + (s.price() - bal)));
        }
        im.lore(lore);
        it.setItemMeta(im);
        return it;
    }
}

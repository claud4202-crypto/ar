package me.arisdonate.gui;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.models.DonateRank;
import me.arisdonate.util.Msg;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

/**
 * GUI магазина донатов: показывает шалкеры по слотам с описанием/командами/лимитом регионов в lore.
 */
public class DonateGui {

    public static final String TITLE = "Донаты сервера";

    private final ArisDonatePlugin plugin;

    public DonateGui(ArisDonatePlugin plugin) {
        this.plugin = plugin;
    }

    public void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 54, Msg.parse("<grad:#FFD700:#FF8C00>✦ ДОНАТЫ СЕРВЕРА ✦</grad>"));

        // декоративные стёкла
        ItemStack pane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta pm = pane.getItemMeta();
        pm.displayName(Component.text(" "));
        pane.setItemMeta(pm);
        for (int slot = 0; slot < 54; slot++) inv.setItem(slot, pane);

        for (DonateRank r : plugin.getDonateManager().getOrderedRanks()) {
            ItemStack icon = new ItemStack(r.shulkerMaterial());
            ItemMeta im = icon.getItemMeta();
            im.displayName(Msg.parse(r.gradientName() + " &7— донат"));
            List<Component> lore = new ArrayList<>();
            lore.add(Msg.parse("&8&m                            "));
            for (String line : r.description()) {
                lore.add(Msg.parse(line));
            }
            lore.add(Component.empty());
            lore.add(Msg.parse("&eРегионов (/sethome): &f" + r.regionLimit()));
            lore.add(Msg.parse("&eДомов (/sethome max): &f" + r.homeLimit()));
            lore.add(Component.empty());
            lore.add(Msg.parse("&6Доступные команды:"));
            for (String cmd : r.commands()) lore.add(Msg.parse(" &f• &7" + cmd));
            lore.add(Component.empty());
            lore.add(Msg.parse("&8&m                            "));
            lore.add(Msg.parse("&7Покупка: &eDiscord администрации"));
            im.lore(lore);
            im.getPersistentDataContainer().set(plugin.keyDonateRank(), PersistentDataType.STRING, r.id());
            icon.setItemMeta(im);
            int slot = Math.max(0, Math.min(53, r.guiSlot()));
            inv.setItem(slot, icon);
        }

        // close button
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta cm = close.getItemMeta();
        cm.displayName(Msg.parse("&c&lЗакрыть"));
        close.setItemMeta(cm);
        inv.setItem(49, close);

        // info
        ItemStack info = new ItemStack(Material.NETHER_STAR);
        ItemMeta in = info.getItemMeta();
        in.displayName(Msg.parse("<grad:#FFD700:#FF8C00>Информация</grad>"));
        List<Component> infoLore = new ArrayList<>();
        infoLore.add(Msg.parse("&7Наведитесь на шалкер с донатом,"));
        infoLore.add(Msg.parse("&7чтобы увидеть описание, доступные"));
        infoLore.add(Msg.parse("&7команды и лимит регионов."));
        infoLore.add(Component.empty());
        infoLore.add(Msg.parse("&8Самый мощный — <grad:#FFD700:#FF8C00>Aris</grad>&8."));
        in.lore(infoLore);
        info.setItemMeta(in);
        inv.setItem(4, info);

        p.openInventory(inv);
    }
}

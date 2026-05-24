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

/**
 * GUI, который открывается при нажатии на сундук-крейт.
 * Показывает: инфо крейта, кол-во ключей, награды с редкостями, кнопку "Открыть".
 */
public class CrateOpenGui {

    public static final String TITLE_PREFIX = "Крейт: ";

    private final ArisCratesPlugin plugin;

    public CrateOpenGui(ArisCratesPlugin plugin) { this.plugin = plugin; }

    public void open(Player player, Crate crate) {
        Inventory inv = Bukkit.createInventory(null, 54,
                Msg.parse("&6" + TITLE_PREFIX + crate.displayName()));

        // Border
        ItemStack border = makeItem(Material.BLACK_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 54; i++) inv.setItem(i, border);

        // Crate info (slot 4)
        int keyCount = plugin.getCrateManager().countKeys(player, crate.id());
        ItemStack info = new ItemStack(crate.blockMaterial());
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.displayName(Msg.parse(crate.displayName()));
        List<Component> infoLore = new ArrayList<>();
        infoLore.add(Component.empty());
        infoLore.add(Msg.parse("&7Ваших ключей: " + (keyCount > 0 ? "&a" : "&c") + keyCount));
        infoLore.add(Msg.parse("&7Наград: &e" + crate.rewards().size()));
        infoLore.add(Component.empty());
        infoLore.add(Msg.parse("&7Нажмите &a[Открыть]&7 внизу"));
        infoMeta.lore(infoLore);
        info.setItemMeta(infoMeta);
        inv.setItem(4, info);

        // Rewards display (slots 10-16, 19-25, 28-34) — up to 21 rewards
        double totalChance = crate.rewards().stream().mapToDouble(CrateReward::chance).sum();
        int[] rewardSlots = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
        for (int i = 0; i < Math.min(crate.rewards().size(), rewardSlots.length); i++) {
            CrateReward r = crate.rewards().get(i);
            ItemStack display = r.item().clone();
            ItemMeta dm = display.getItemMeta();
            dm.displayName(Msg.parse(r.display()));
            List<Component> lore = new ArrayList<>();
            double pct = (r.chance() / totalChance) * 100;
            lore.add(Msg.parse("&7Шанс: &e" + String.format("%.1f", pct) + "%"));
            if (r.isDonateReward()) {
                lore.add(Msg.parse("&7Тип: &cДонат-ранг"));
            } else {
                lore.add(Msg.parse("&7Кол-во: &f" + r.item().getAmount()));
            }
            lore.add(Msg.parse("&7Редкость: " + getRarityColor(r.rarity()) + getRarityName(r.rarity())));
            lore.add(Component.empty());
            lore.add(Msg.parse(getRarityColor(r.rarity()) + getRaritySymbols(r.rarity())));
            dm.lore(lore);
            display.setItemMeta(dm);
            inv.setItem(rewardSlots[i], display);
        }

        // Key display (slot 48)
        ItemStack keyDisplay = new ItemStack(crate.keyMaterial());
        ItemMeta km = keyDisplay.getItemMeta();
        km.displayName(Msg.parse("&eКлючи"));
        List<Component> keyLore = new ArrayList<>();
        keyLore.add(Msg.parse("&7У вас: " + (keyCount > 0 ? "&a" + keyCount + " ключ(ей)" : "&cНет ключей")));
        keyLore.add(Component.empty());
        keyLore.add(Msg.parse("&7Ключи можно получить от администрации"));
        km.lore(keyLore);
        km.setEnchantmentGlintOverride(true);
        keyDisplay.setItemMeta(km);
        inv.setItem(48, keyDisplay);

        // Preview button (slot 46)
        inv.setItem(46, makeItem(Material.BOOK, "&eПросмотр наград"));

        // Open button (slot 49)
        if (keyCount > 0) {
            ItemStack open = new ItemStack(Material.LIME_DYE);
            ItemMeta om = open.getItemMeta();
            om.displayName(Msg.parse("&a&lОТКРЫТЬ КРЕЙТ"));
            List<Component> openLore = new ArrayList<>();
            openLore.add(Msg.parse("&7Используется &a1 &7ключ"));
            openLore.add(Msg.parse("&7У вас: &a" + keyCount));
            openLore.add(Component.empty());
            openLore.add(Msg.parse("&eНажмите, чтобы открыть!"));
            om.lore(openLore);
            open.setItemMeta(om);
            inv.setItem(49, open);
        } else {
            ItemStack noKey = new ItemStack(Material.GRAY_DYE);
            ItemMeta nm = noKey.getItemMeta();
            nm.displayName(Msg.parse("&c&lНЕТ КЛЮЧЕЙ"));
            List<Component> noLore = new ArrayList<>();
            noLore.add(Msg.parse("&7Нужен ключ от этого крейта"));
            nm.lore(noLore);
            noKey.setItemMeta(nm);
            inv.setItem(49, noKey);
        }

        // Close button (slot 50)
        inv.setItem(50, makeItem(Material.BARRIER, "&c&lЗакрыть"));

        player.openInventory(inv);
    }

    private ItemStack makeItem(Material mat, String name) {
        ItemStack it = new ItemStack(mat);
        ItemMeta im = it.getItemMeta();
        im.displayName(Msg.parse(name));
        it.setItemMeta(im);
        return it;
    }

    public static String getRarityColor(String rarity) {
        if (rarity == null) return "&7";
        return switch (rarity.toLowerCase()) {
            case "common" -> "&f";
            case "uncommon" -> "&a";
            case "rare" -> "&9";
            case "epic" -> "&5";
            case "legendary" -> "&6";
            case "mythic" -> "&d";
            case "donate", "донат" -> "&c";
            default -> "&7";
        };
    }

    public static String getRarityName(String rarity) {
        if (rarity == null) return "Обычная";
        return switch (rarity.toLowerCase()) {
            case "common" -> "Обычная";
            case "uncommon" -> "Необычная";
            case "rare" -> "Редкая";
            case "epic" -> "Эпическая";
            case "legendary" -> "Легендарная";
            case "mythic" -> "Мифическая";
            case "donate", "донат" -> "Донатная";
            default -> rarity;
        };
    }

    public static String getRaritySymbols(String rarity) {
        if (rarity == null) return "☆";
        return switch (rarity.toLowerCase()) {
            case "common" -> "☆";
            case "uncommon" -> "★";
            case "rare" -> "★★";
            case "epic" -> "★★★";
            case "legendary" -> "★★★★";
            case "mythic" -> "★★★★★";
            case "donate", "донат" -> "💎 ДОНАТ";
            default -> "☆";
        };
    }
}

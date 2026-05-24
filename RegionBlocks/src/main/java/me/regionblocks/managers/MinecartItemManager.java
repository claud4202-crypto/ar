package me.regionblocks.managers;

import me.regionblocks.models.MinecartType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class MinecartItemManager {

    private static NamespacedKey MINECART_KEY;

    public static void init(Plugin plugin) {
        MINECART_KEY = new NamespacedKey(plugin, "custom_minecart_type");
    }

    public static NamespacedKey getKey() { return MINECART_KEY; }

    public static ItemStack createMinecart(MinecartType type) {
        ItemStack item = new ItemStack(Material.TNT_MINECART, 1);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(
            Component.text(type.getDisplayName())
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true)
        );

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(""));

        for (String line : type.getDescription().split("\n")) {
            lore.add(
                Component.text("  " + line)
                    .color(TextColor.color(0xAAAAAA))
                    .decoration(TextDecoration.ITALIC, false)
            );
        }

        lore.add(Component.text(""));
        lore.add(
            Component.text("  Радиус взрыва: ")
                .color(TextColor.color(0x888888))
                .decoration(TextDecoration.ITALIC, false)
                .append(Component.text(type.getRadius() + " блоков")
                    .color(TextColor.color(0xFFCC55))
                    .decoration(TextDecoration.ITALIC, false))
        );
        if (type.isBreakObsidian()) {
            lore.add(
                Component.text("  ✓ Ломает обсидиан")
                    .color(TextColor.color(0xFF44FF))
                    .decoration(TextDecoration.ITALIC, false)
            );
        }
        if (type.isBreakAris()) {
            lore.add(
                Component.text("  ✓ УНИЧТОЖАЕТ АРИС-БЛОК!")
                    .color(TextColor.color(0xFF2222))
                    .decoration(TextDecoration.BOLD, true)
                    .decoration(TextDecoration.ITALIC, false)
            );
        }
        lore.add(Component.text(""));
        lore.add(
            Component.text("  Цена: ")
                .color(TextColor.color(0x888888))
                .decoration(TextDecoration.ITALIC, false)
                .append(Component.text(type.getPrice() + " ✦")
                    .color(TextColor.color(0xFF8000))
                    .decoration(TextDecoration.BOLD, true)
                    .decoration(TextDecoration.ITALIC, false))
        );
        lore.add(Component.text(""));

        meta.lore(lore);
        meta.getPersistentDataContainer().set(MINECART_KEY, PersistentDataType.STRING, type.name());
        item.setItemMeta(meta);
        return item;
    }

    public static MinecartType getMinecartType(ItemStack item) {
        if (item == null || item.getType() != Material.TNT_MINECART) return null;
        if (!item.hasItemMeta()) return null;
        String val = item.getItemMeta()
            .getPersistentDataContainer()
            .get(MINECART_KEY, PersistentDataType.STRING);
        if (val == null) return null;
        try { return MinecartType.valueOf(val); }
        catch (IllegalArgumentException e) { return null; }
    }
}

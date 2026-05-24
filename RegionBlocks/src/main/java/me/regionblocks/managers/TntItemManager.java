package me.regionblocks.managers;

import me.regionblocks.models.TntType;
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

public class TntItemManager {

    private static NamespacedKey TNT_KEY;

    public static void init(Plugin plugin) {
        TNT_KEY = new NamespacedKey(plugin, "custom_tnt_type");
    }

    public static NamespacedKey getKey() { return TNT_KEY; }

    /** Создать предмет кастомного ТНТ. */
    public static ItemStack createTnt(TntType type) {
        ItemStack item = new ItemStack(Material.TNT, 1);
        ItemMeta meta = item.getItemMeta();

        // Название
        meta.displayName(
            Component.text(type.getDisplayName())
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true)
        );

        // Лор
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(""));

        // Описание (многострочное)
        for (String line : type.getDescription().split("\n")) {
            lore.add(
                Component.text("  " + line)
                    .color(TextColor.color(0xAAAAAA))
                    .decoration(TextDecoration.ITALIC, false)
            );
        }

        lore.add(Component.text(""));

        // Характеристики
        lore.add(
            Component.text("  Сила взрыва: ")
                .color(TextColor.color(0x888888))
                .decoration(TextDecoration.ITALIC, false)
                .append(Component.text(String.format("%.0f", type.getPower()))
                    .color(TextColor.color(0xFF6644))
                    .decoration(TextDecoration.BOLD, true)
                    .decoration(TextDecoration.ITALIC, false))
        );
        lore.add(
            Component.text("  Радиус: ")
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
        if (type.isBreakPrivate()) {
            lore.add(
                Component.text("  ✓ УНИЧТОЖАЕТ ПРИВАТИ!")
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

        // PDC-метка — храним имя enum
        meta.getPersistentDataContainer().set(TNT_KEY, PersistentDataType.STRING, type.name());
        item.setItemMeta(meta);
        return item;
    }

    /** Получить тип ТНТ из предмета, или null если не кастомный. */
    public static TntType getTntType(ItemStack item) {
        if (item == null || item.getType() != Material.TNT) return null;
        if (!item.hasItemMeta()) return null;
        String val = item.getItemMeta()
            .getPersistentDataContainer()
            .get(TNT_KEY, PersistentDataType.STRING);
        if (val == null) return null;
        try { return TntType.valueOf(val); }
        catch (IllegalArgumentException e) { return null; }
    }
}

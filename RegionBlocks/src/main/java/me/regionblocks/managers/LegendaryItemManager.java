package me.regionblocks.managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class LegendaryItemManager {

    private static NamespacedKey LEGENDARY_KEY;

    public static void init(Plugin plugin) {
        LEGENDARY_KEY = new NamespacedKey(plugin, "legendary_block");
    }

    public static ItemStack createLegendaryBlock() {
        ItemStack item = new ItemStack(Material.ANCIENT_DEBRIS, 1);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(
            Component.text("✦ Легендарный приват ✦")
                .color(TextColor.color(0xFF4400))
                .decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, true)
        );

        meta.lore(List.of(
            Component.text(""),
            Component.text("  Древний обломок из Нижнего мира.")
                .color(TextColor.color(0xFF6633))
                .decoration(TextDecoration.ITALIC, false),
            Component.text("  Выдержит любой взрыв.")
                .color(TextColor.color(0xFF4400))
                .decoration(TextDecoration.ITALIC, false),
            Component.text(""),
            Component.text("  ► Тир: ")
                .color(TextColor.color(0xAAAAAA))
                .decoration(TextDecoration.ITALIC, false)
                .append(Component.text("Легендарный")
                    .color(TextColor.color(0xFF4400))
                    .decoration(TextDecoration.BOLD, true)),
            Component.text("  ► Регион: ")
                .color(TextColor.color(0xAAAAAA))
                .decoration(TextDecoration.ITALIC, false)
                .append(Component.text("24×24×24 блоков")
                    .color(TextColor.color(0xFFCC66))),
            Component.text(""),
            Component.text("  Поставьте блок, чтобы")
                .color(TextColor.color(0x888888))
                .decoration(TextDecoration.ITALIC, true),
            Component.text("  создать легендарный регион.")
                .color(TextColor.color(0x888888))
                .decoration(TextDecoration.ITALIC, true),
            Component.text("")
        ));

        // PDC-метка: только этот блок создаёт регион
        meta.getPersistentDataContainer().set(LEGENDARY_KEY, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isLegendaryBlock(ItemStack item) {
        if (item == null || item.getType() != Material.ANCIENT_DEBRIS) return false;
        if (!item.hasItemMeta()) return false;
        return item.getItemMeta()
            .getPersistentDataContainer()
            .has(LEGENDARY_KEY, PersistentDataType.BYTE);
    }

    public static NamespacedKey getKey() { return LEGENDARY_KEY; }
}

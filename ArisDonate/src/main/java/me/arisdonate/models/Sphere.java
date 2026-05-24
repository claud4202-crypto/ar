package me.arisdonate.models;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.Msg;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

/**
 * Сфера / шарик. Носится в шлеме или off-hand.
 * Даёт активные баф/дебаф-эффекты пока экипирована.
 *
 *   id            — уникальный id (lowercase)
 *   displayName   — название с MiniMessage градиентом
 *   material      — иконка (любой "круглый" предмет)
 *   startHex/endHex — цвета градиента для имени
 *   buffs         — список баф-эффектов "type:level"
 *   debuffs       — список деба-эффектов "type:level"
 *   price         — цена в Aris-coins
 *   slot          — head / offhand / any
 *   lore          — дополнительные строки описания
 */
public final class Sphere {

    public enum Slot { HEAD, OFFHAND, ANY }

    private final String id;
    private final String displayName;
    private final Material material;
    private final String startHex;
    private final String endHex;
    private final List<String> buffs;
    private final List<String> debuffs;
    private final int price;
    private final Slot slot;
    private final List<String> lore;

    public Sphere(String id, String displayName, Material material, String startHex, String endHex,
                  List<String> buffs, List<String> debuffs, int price, Slot slot, List<String> lore) {
        this.id = id;
        this.displayName = displayName;
        this.material = material;
        this.startHex = startHex;
        this.endHex = endHex;
        this.buffs = buffs;
        this.debuffs = debuffs;
        this.price = price;
        this.slot = slot;
        this.lore = lore;
    }

    public String id()                  { return id; }
    public String displayName()         { return displayName; }
    public Material material()          { return material; }
    public String startHex()            { return startHex; }
    public String endHex()              { return endHex; }
    public List<String> buffs()         { return buffs; }
    public List<String> debuffs()       { return debuffs; }
    public int price()                  { return price; }
    public Slot slot()                  { return slot; }
    public List<String> lore()          { return lore; }

    public String gradientName() {
        return "<grad:#" + startHex + ":#" + endHex + ">" + displayName + "</grad>";
    }

    /** Создаёт ItemStack-предмет сферы с pdc-меткой. */
    public ItemStack toItem(ArisDonatePlugin plugin) {
        ItemStack it = new ItemStack(material);
        ItemMeta im = it.getItemMeta();
        if (im == null) return it;
        im.displayName(Msg.parse(gradientName()));

        List<Component> lines = new ArrayList<>();
        lines.add(Msg.parse("&8&m                            "));
        lines.add(Msg.parse("&7Слот: &f" + switch (slot) {
            case HEAD    -> "Голова";
            case OFFHAND -> "Вторая рука";
            case ANY     -> "Голова или вторая рука";
        }));
        lines.add(Component.empty());
        if (!buffs.isEmpty()) {
            lines.add(Msg.parse("&a▲ Бафы:"));
            for (String b : buffs) lines.add(Msg.parse(" &f• &a" + describeEffect(b)));
        }
        if (!debuffs.isEmpty()) {
            lines.add(Msg.parse("&c▼ Дебафы:"));
            for (String d : debuffs) lines.add(Msg.parse(" &f• &c" + describeEffect(d)));
        }
        if (lore != null && !lore.isEmpty()) {
            lines.add(Component.empty());
            for (String l : lore) lines.add(Msg.parse(l));
        }
        lines.add(Component.empty());
        lines.add(Msg.parse("&7Носи в шлеме или второй руке."));
        lines.add(Msg.parse("&8&m                            "));
        im.lore(lines);
        im.getPersistentDataContainer().set(plugin.keySphereId(), PersistentDataType.STRING, id);
        // Лёгкий visual glow
        im.addEnchant(org.bukkit.enchantments.Enchantment.UNBREAKING, 1, true);
        im.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS,
                org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES,
                org.bukkit.inventory.ItemFlag.HIDE_UNBREAKABLE);
        it.setItemMeta(im);
        return it;
    }

    /** Собирает живые PotionEffect'ы (durations длинные, обновляются каждый тик SphereManager'а). */
    public List<PotionEffect> potionEffects() {
        List<PotionEffect> out = new ArrayList<>();
        for (String s : buffs)   { PotionEffect pe = build(s); if (pe != null) out.add(pe); }
        for (String s : debuffs) { PotionEffect pe = build(s); if (pe != null) out.add(pe); }
        return out;
    }

    private static PotionEffect build(String raw) {
        if (raw == null) return null;
        String[] parts = raw.split(":");
        if (parts.length < 1) return null;
        PotionEffectType type = effectByKey(parts[0]);
        if (type == null) return null;
        int lvl = parts.length >= 2 ? Integer.parseInt(parts[1]) : 1;
        return new PotionEffect(type, 80, Math.max(0, lvl - 1), true, false, false);
    }

    private static PotionEffectType effectByKey(String key) {
        String low = key.toLowerCase();
        org.bukkit.NamespacedKey nk = org.bukkit.NamespacedKey.minecraft(low);
        return Registry.EFFECT.get(nk);
    }

    private static String describeEffect(String raw) {
        if (raw == null) return "?";
        String[] parts = raw.split(":");
        String name = parts.length > 0 ? humanize(parts[0]) : raw;
        int lvl = parts.length >= 2 ? Integer.parseInt(parts[1]) : 1;
        return name + " " + toRoman(lvl);
    }

    private static String humanize(String s) {
        s = s.toLowerCase().replace('_', ' ');
        StringBuilder out = new StringBuilder();
        boolean upper = true;
        for (char c : s.toCharArray()) {
            if (c == ' ') { out.append(' '); upper = true; continue; }
            out.append(upper ? Character.toUpperCase(c) : c);
            upper = false;
        }
        return out.toString();
    }

    private static String toRoman(int n) {
        return switch (n) {
            case 1 -> "I"; case 2 -> "II"; case 3 -> "III"; case 4 -> "IV"; case 5 -> "V";
            default -> String.valueOf(n);
        };
    }
}

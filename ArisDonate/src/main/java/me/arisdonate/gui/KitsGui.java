package me.arisdonate.gui;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.managers.KitManager;
import me.arisdonate.util.Msg;
import me.arisdonate.util.TimeUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * GUI /kits — показывает все киты в виде шалкер-предметов.
 * В lore — список предметов и зачарований, клик выдаёт кит.
 */
public class KitsGui {

    public static final String TITLE_RAW = "★ КИТЫ СЕРВЕРА ★";

    private final ArisDonatePlugin plugin;

    public KitsGui(ArisDonatePlugin plugin) { this.plugin = plugin; }

    public void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 54,
                Msg.parse("<grad:#FFD700:#FF8C00>★ КИТЫ СЕРВЕРА ★</grad>"));

        ItemStack pane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta pm = pane.getItemMeta();
        pm.displayName(Component.text(" "));
        pane.setItemMeta(pm);
        for (int slot = 0; slot < 54; slot++) inv.setItem(slot, pane);

        // 3 ряда по 7 слотов в центре
        int[] slots = {
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34
        };
        int i = 0;
        for (KitManager.Kit kit : plugin.getKitManager().all()) {
            if (i >= slots.length) break;
            inv.setItem(slots[i++], buildIcon(p, kit));
        }

        // info
        ItemStack info = new ItemStack(Material.NETHER_STAR);
        ItemMeta in = info.getItemMeta();
        in.displayName(Msg.parse("<grad:#FFD700:#FF8C00>★ Киты ★</grad>"));
        List<Component> infoLore = new ArrayList<>();
        infoLore.add(Msg.parse("&7Кликни по шалкеру, чтобы получить кит."));
        infoLore.add(Msg.parse("&7Наведись для просмотра содержимого."));
        infoLore.add(Component.empty());
        infoLore.add(Msg.parse("&8Самый мощный: <grad:#FFD700:#FF8C00>Aris</grad> / <grad:#FF8C00:#FF1493>ArisPlus</grad>"));
        in.lore(infoLore);
        info.setItemMeta(in);
        inv.setItem(4, info);

        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta cm = close.getItemMeta();
        cm.displayName(Msg.parse("&c&lЗакрыть"));
        close.setItemMeta(cm);
        inv.setItem(49, close);

        p.openInventory(inv);
    }

    private ItemStack buildIcon(Player p, KitManager.Kit kit) {
        Material icon = pickIconForKit(kit.id);
        ItemStack it = new ItemStack(icon);
        ItemMeta im = it.getItemMeta();
        im.displayName(Msg.parse(kit.displayName));

        List<Component> lore = new ArrayList<>();
        lore.add(Msg.parse("&8&m                            "));
        lore.add(Msg.parse("&6Содержимое кита:"));
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (ItemStack item : kit.items) {
            String label = describeItem(item);
            counts.merge(label, item.getAmount(), Integer::sum);
        }
        int limit = 0;
        for (Map.Entry<String, Integer> e : counts.entrySet()) {
            if (limit++ >= 18) { lore.add(Msg.parse(" &8…")); break; }
            lore.add(Msg.parse(" &f• &7" + e.getKey() + (e.getValue() > 1 ? " &8×&f" + e.getValue() : "")));
        }
        lore.add(Component.empty());
        long left = plugin.getKitManager().cooldownLeft(p, kit.id);
        if (left > 0) {
            lore.add(Msg.parse("&cНа кулдауне ещё: &e" + TimeUtil.fmt(left)));
        } else if (!plugin.getKitManager().canTakeKit(p, kit)) {
            lore.add(Msg.parse("&cНет доступа &7(нужен донат)"));
        } else {
            lore.add(Msg.parse("&aКликни, чтобы получить!"));
        }
        if (plugin.getKitManager().canBypassCooldown(p)) {
            lore.add(Msg.parse("&8(OP: без кулдауна)"));
        }
        lore.add(Msg.parse("&8&m                            "));
        im.lore(lore);
        im.getPersistentDataContainer().set(plugin.keyKitId(),
                PersistentDataType.STRING, kit.id);
        it.setItemMeta(im);
        return it;
    }

    private Material pickIconForKit(String id) {
        return switch (id) {
            case "spark"    -> Material.LIME_SHULKER_BOX;
            case "luna"     -> Material.LIGHT_BLUE_SHULKER_BOX;
            case "stellar"  -> Material.CYAN_SHULKER_BOX;
            case "nova"     -> Material.MAGENTA_SHULKER_BOX;
            case "comet"    -> Material.WHITE_SHULKER_BOX;
            case "galaxy"   -> Material.PURPLE_SHULKER_BOX;
            case "nebula"   -> Material.PINK_SHULKER_BOX;
            case "cosmos"   -> Material.BLUE_SHULKER_BOX;
            case "phoenix"  -> Material.RED_SHULKER_BOX;
            case "aris"     -> Material.ORANGE_SHULKER_BOX;
            case "arisplus" -> Material.YELLOW_SHULKER_BOX;
            default          -> Material.SHULKER_BOX;
        };
    }

    private String describeItem(ItemStack it) {
        ItemMeta im = it.getItemMeta();
        String name;
        if (im != null && im.hasDisplayName() && im.displayName() != null) {
            name = PlainTextComponentSerializer.plainText().serialize(im.displayName());
        } else if (it.getType() == Material.SHULKER_BOX
                || it.getType().name().endsWith("SHULKER_BOX")) {
            name = "Шалкер-ящик с расходниками";
        } else {
            name = humanize(it.getType().name());
        }
        if (im != null && !im.getEnchants().isEmpty()) {
            StringBuilder sb = new StringBuilder(name).append(" §8[");
            int j = 0;
            for (Map.Entry<Enchantment, Integer> e : im.getEnchants().entrySet()) {
                if (j++ > 0) sb.append(", ");
                String key = e.getKey().getKey().getKey();
                sb.append(humanize(key)).append(" ").append(toRoman(e.getValue()));
            }
            sb.append("§8]");
            return sb.toString();
        }
        // если шалкер — добавим сводку содержимого
        if (im instanceof BlockStateMeta bsm && bsm.getBlockState() instanceof org.bukkit.block.ShulkerBox box) {
            int total = 0;
            for (ItemStack inner : box.getInventory().getContents()) {
                if (inner != null && !inner.getType().isAir()) total += inner.getAmount();
            }
            if (total > 0) return name + " §8(внутри §f" + total + " §8предметов)";
        }
        return name;
    }

    private String humanize(String s) {
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

    private String toRoman(int n) {
        return switch (n) {
            case 1 -> "I"; case 2 -> "II"; case 3 -> "III"; case 4 -> "IV"; case 5 -> "V";
            case 6 -> "VI"; case 7 -> "VII"; case 8 -> "VIII"; case 9 -> "IX"; case 10 -> "X";
            default -> String.valueOf(n);
        };
    }
}

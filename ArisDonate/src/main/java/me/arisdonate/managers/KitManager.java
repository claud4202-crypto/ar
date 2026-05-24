package me.arisdonate.managers;

import me.arisdonate.ArisDonatePlugin;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Простая система китов с поддержкой:
 *   - название материала
 *   - количество
 *   - зачарования key:level (sharpness:6, protection:5, fire_aspect:3 и т.д.)
 *   - cooldown в секундах
 */
public class KitManager {

    private final ArisDonatePlugin plugin;
    private final Map<String, Kit> kits = new LinkedHashMap<>();
    private final Map<UUID, Map<String, Long>> lastUsed = new HashMap<>();
    private File cooldownFile;

    public KitManager(ArisDonatePlugin plugin) {
        this.plugin = plugin;
        load();
        loadCooldowns();
    }

    public static final class Kit {
        public final String id;
        public final String displayName;
        public final long cooldownSeconds;
        public final List<ItemStack> items;
        public Kit(String id, String displayName, long cooldownSeconds, List<ItemStack> items) {
            this.id = id; this.displayName = displayName;
            this.cooldownSeconds = cooldownSeconds; this.items = items;
        }
    }

    public Kit getKit(String id) { return id == null ? null : kits.get(id.toLowerCase()); }
    public Collection<Kit> all()  { return kits.values(); }

    public long cooldownLeft(Player p, String kitId) {
        if (canBypassCooldown(p)) return 0;
        Map<String, Long> map = lastUsed.get(p.getUniqueId());
        Kit k = getKit(kitId);
        if (map == null || k == null) return 0;
        Long t = map.get(kitId.toLowerCase());
        if (t == null) return 0;
        long left = (t + k.cooldownSeconds * 1000L) - System.currentTimeMillis();
        return Math.max(0, left);
    }

    /** Только OP могут получать любой кит без кулдауна. Staff и донатеры тоже ждут. */
    public boolean canBypassCooldown(Player p) {
        return p.isOp();
    }

    public boolean canTakeKit(Player p, Kit kit) {
        if (canBypassCooldown(p)) return true;
        if (p.hasPermission("arisdonate.kit." + kit.id)) return true;
        // Запасной путь: PermissionAttachment может ещё не доехать в первом тике
        // после join — поэтому смотрим напрямую в DonateManager.
        me.arisdonate.models.DonateRank rank = plugin.getDonateManager().getPlayerRank(p.getName());
        if (rank == null) return false;
        if (kit.id.equalsIgnoreCase(rank.kitId())) return true;
        // владелец более высокого ранга может брать киты ниже своего веса
        for (me.arisdonate.models.DonateRank r : plugin.getDonateManager().getRanks().values()) {
            if (r.weight() <= rank.weight() && kit.id.equalsIgnoreCase(r.kitId())) return true;
        }
        return false;
    }

    public void giveKit(Player p, Kit k) {
        for (ItemStack it : k.items) {
            Map<Integer, ItemStack> leftover = p.getInventory().addItem(it.clone());
            for (ItemStack drop : leftover.values()) {
                p.getWorld().dropItemNaturally(p.getLocation(), drop);
            }
        }
        if (!canBypassCooldown(p)) {
            lastUsed.computeIfAbsent(p.getUniqueId(), k2 -> new HashMap<>())
                    .put(k.id.toLowerCase(), System.currentTimeMillis());
            saveCooldowns();
        }
    }

    /** Принудительно поставить кулдаун на кит для игрока (напр., при /kit give). */
    public void setCooldown(Player p, String kitId) {
        if (canBypassCooldown(p)) return;
        lastUsed.computeIfAbsent(p.getUniqueId(), k -> new HashMap<>())
                .put(kitId.toLowerCase(), System.currentTimeMillis());
        saveCooldowns();
    }

    private void load() {
        FileConfiguration cfg = plugin.getConfig();
        ConfigurationSection root = cfg.getConfigurationSection("kits");
        if (root == null) return;
        kits.clear();
        for (String id : root.getKeys(false)) {
            ConfigurationSection s = root.getConfigurationSection(id);
            String displayName = s.getString("display-name", id);
            long cd = s.getLong("cooldown", 3600);
            List<ItemStack> items = new ArrayList<>();
            for (Map<?, ?> raw : s.getMapList("items")) {
                ItemStack it = parseItem(raw);
                if (it != null) items.add(it);
            }
            kits.put(id.toLowerCase(), new Kit(id.toLowerCase(), displayName, cd, items));
        }
    }

    @SuppressWarnings("unchecked")
    private ItemStack parseItem(Map<?, ?> raw) {
        // Спец-формат: { sphere: <id> } — взять предмет-сферу из SphereManager
        Object sphereId = raw.get("sphere");
        if (sphereId != null && plugin.getSphereManager() != null) {
            me.arisdonate.models.Sphere s = plugin.getSphereManager().getSphere(sphereId.toString());
            if (s != null) {
                ItemStack si = s.toItem(plugin);
                if (raw.get("amount") != null) si.setAmount(((Number) raw.get("amount")).intValue());
                return si;
            }
        }
        Object mat = raw.get("material");
        if (mat == null) return null;
        Material m = Material.matchMaterial(mat.toString());
        if (m == null) return null;
        int amount = raw.get("amount") == null ? 1 : ((Number) raw.get("amount")).intValue();
        ItemStack it = new ItemStack(m, amount);
        Object name = raw.get("name");
        if (name != null) {
            ItemMeta im = it.getItemMeta();
            if (im != null) {
                im.displayName(me.arisdonate.util.Msg.parse(name.toString()));
                it.setItemMeta(im);
            }
        }
        Object lore = raw.get("lore");
        if (lore instanceof List<?> ll) {
            ItemMeta im = it.getItemMeta();
            if (im != null) {
                List<net.kyori.adventure.text.Component> components = new ArrayList<>();
                for (Object o : ll) components.add(me.arisdonate.util.Msg.parse(o.toString()));
                im.lore(components);
                it.setItemMeta(im);
            }
        }
        Object ench = raw.get("enchantments");
        if (ench instanceof List<?> el) {
            ItemMeta im = it.getItemMeta();
            for (Object e : el) {
                String[] parts = e.toString().split(":");
                if (parts.length < 2) continue;
                Enchantment en = enchantmentByKey(parts[0]);
                if (en == null) continue;
                int lvl = Integer.parseInt(parts[1]);
                if (im instanceof EnchantmentStorageMeta esm) {
                    esm.addStoredEnchant(en, lvl, true);
                } else if (im != null) {
                    im.addEnchant(en, lvl, true);
                }
            }
            if (im != null) it.setItemMeta(im);
        }
        // shulker-contents: вложенные предметы внутрь шалкера
        Object sc = raw.get("shulker-contents");
        if (sc instanceof List<?> scl && it.getItemMeta() instanceof BlockStateMeta bsm) {
            if (bsm.getBlockState() instanceof ShulkerBox box) {
                int slot = 0;
                for (Object o : scl) {
                    if (slot >= 27) break;
                    if (!(o instanceof Map<?, ?> m2)) continue;
                    ItemStack inner = parseItem(m2);
                    if (inner == null) continue;
                    box.getInventory().setItem(slot++, inner);
                }
                bsm.setBlockState(box);
                it.setItemMeta(bsm);
            }
        }
        return it;
    }

    private Enchantment enchantmentByKey(String key) {
        String low = key.toLowerCase();
        org.bukkit.NamespacedKey nk = org.bukkit.NamespacedKey.minecraft(low);
        Enchantment e = org.bukkit.Registry.ENCHANTMENT.get(nk);
        return e;
    }

    private void loadCooldowns() {
        cooldownFile = new File(plugin.getDataFolder(), "kit_cooldowns.yml");
        cooldownFile.getParentFile().mkdirs();
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(cooldownFile);
        ConfigurationSection s = cfg.getConfigurationSection("cooldowns");
        if (s == null) return;
        for (String pid : s.getKeys(false)) {
            UUID id;
            try { id = UUID.fromString(pid); } catch (Exception e) { continue; }
            ConfigurationSection u = s.getConfigurationSection(pid);
            Map<String, Long> m = new HashMap<>();
            for (String kit : u.getKeys(false)) m.put(kit, u.getLong(kit));
            lastUsed.put(id, m);
        }
    }

    public void saveCooldowns() {
        FileConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Map<String, Long>> e : lastUsed.entrySet()) {
            for (Map.Entry<String, Long> k : e.getValue().entrySet()) {
                cfg.set("cooldowns." + e.getKey() + "." + k.getKey(), k.getValue());
            }
        }
        try { cfg.save(cooldownFile); } catch (IOException ignored) {}
    }
}

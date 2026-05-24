package me.ariscrates.managers;

import me.ariscrates.ArisCratesPlugin;
import me.ariscrates.models.Crate;
import me.ariscrates.models.CrateReward;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class CrateManager {

    private final ArisCratesPlugin plugin;
    private final Map<String, Crate> crates = new LinkedHashMap<>();
    private final NamespacedKey keyCrateId;

    public CrateManager(ArisCratesPlugin plugin) {
        this.plugin = plugin;
        this.keyCrateId = new NamespacedKey(plugin, "crate_key");
        load();
    }

    public void reload() {
        crates.clear();
        load();
    }

    public Crate getCrate(String id) {
        return id == null ? null : crates.get(id.toLowerCase(Locale.ROOT));
    }

    public Collection<Crate> all() { return crates.values(); }

    public NamespacedKey keyCrateId() { return keyCrateId; }

    public ItemStack createKey(Crate crate, int amount) {
        Material mat = crate.keyMaterial();
        ItemStack key = new ItemStack(mat, amount);
        ItemMeta im = key.getItemMeta();
        im.displayName(Msg.parse(crate.keyName()));
        List<Component> lore = new ArrayList<>();
        lore.add(Msg.parse("&7Ключ от крейта: " + crate.displayName()));
        lore.add(Msg.parse("&7ПКМ по крейту, чтобы открыть."));
        im.lore(lore);
        im.getPersistentDataContainer().set(keyCrateId, PersistentDataType.STRING, crate.id());
        im.setEnchantmentGlintOverride(true);
        key.setItemMeta(im);
        return key;
    }

    public String getKeyId(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        return item.getItemMeta().getPersistentDataContainer()
                .get(keyCrateId, PersistentDataType.STRING);
    }

    public int countKeys(org.bukkit.entity.Player player, String crateId) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            String id = getKeyId(item);
            if (id != null && id.equalsIgnoreCase(crateId)) count += item.getAmount();
        }
        return count;
    }

    public boolean consumeKey(org.bukkit.entity.Player player, String crateId) {
        for (ItemStack item : player.getInventory().getContents()) {
            String id = getKeyId(item);
            if (id != null && id.equalsIgnoreCase(crateId)) {
                item.setAmount(item.getAmount() - 1);
                return true;
            }
        }
        return false;
    }

    private void load() {
        ConfigurationSection root = plugin.getConfig().getConfigurationSection("crates");
        if (root == null) return;
        for (String id : root.getKeys(false)) {
            ConfigurationSection s = root.getConfigurationSection(id);
            if (s == null) continue;
            String display = s.getString("display-name", id);
            Material keyMat = Material.matchMaterial(s.getString("key-material", "TRIPWIRE_HOOK"));
            if (keyMat == null) keyMat = Material.TRIPWIRE_HOOK;
            String keyName = s.getString("key-name", "&eКлюч");
            Material blockMat = Material.matchMaterial(s.getString("block-material", "CHEST"));
            if (blockMat == null) blockMat = Material.CHEST;
            boolean broadcast = s.getBoolean("broadcast-win", false);
            String permission = s.getString("permission", "");
            String hologramText = s.getString("hologram", "");

            List<CrateReward> rewards = new ArrayList<>();
            for (Map<?, ?> raw : s.getMapList("rewards")) {
                double chance = raw.get("chance") == null ? 10 : ((Number) raw.get("chance")).doubleValue();
                String desc = raw.get("display") == null ? "???" : raw.get("display").toString();
                String rarity = raw.get("rarity") == null ? "common" : raw.get("rarity").toString();
                String donateRank = raw.get("donate-rank") == null ? null : raw.get("donate-rank").toString();

                if (donateRank != null && !donateRank.isEmpty()) {
                    // Donate reward — use a placeholder item (nether star)
                    Material iconMat = Material.NETHER_STAR;
                    Object iconObj = raw.get("material");
                    if (iconObj != null) {
                        Material parsed = Material.matchMaterial(iconObj.toString());
                        if (parsed != null) iconMat = parsed;
                    }
                    ItemStack icon = new ItemStack(iconMat);
                    rewards.add(new CrateReward(desc, icon, chance, rarity, donateRank));
                } else {
                    ItemStack item = parseItem(raw);
                    if (item == null) continue;
                    rewards.add(new CrateReward(desc, item, chance, rarity, null));
                }
            }
            if (rewards.isEmpty()) continue;
            crates.put(id.toLowerCase(Locale.ROOT),
                    new Crate(id.toLowerCase(Locale.ROOT), display, keyMat, keyName, blockMat, broadcast, permission, hologramText, rewards));
        }
    }

    @SuppressWarnings("unchecked")
    private ItemStack parseItem(Map<?, ?> raw) {
        Object mat = raw.get("material");
        if (mat == null) return null;
        Material m = Material.matchMaterial(mat.toString());
        if (m == null) return null;
        int amount = raw.get("amount") == null ? 1 : ((Number) raw.get("amount")).intValue();
        ItemStack it = new ItemStack(m, amount);
        Object ench = raw.get("enchantments");
        if (ench instanceof List<?> el) {
            ItemMeta im = it.getItemMeta();
            for (Object e : el) {
                String[] parts = e.toString().split(":");
                if (parts.length < 2) continue;
                NamespacedKey nk = NamespacedKey.minecraft(parts[0].toLowerCase());
                Enchantment en = org.bukkit.Registry.ENCHANTMENT.get(nk);
                if (en == null) continue;
                int lvl = Integer.parseInt(parts[1]);
                im.addEnchant(en, lvl, true);
            }
            it.setItemMeta(im);
        }
        return it;
    }
}

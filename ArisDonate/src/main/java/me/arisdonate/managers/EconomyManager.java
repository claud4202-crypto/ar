package me.arisdonate.managers;

import me.arisdonate.ArisDonatePlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Простой кошелёк Aris-coins в economy.yml.
 * Не пытается заменить Vault — это локальная валюта плагина.
 */
public class EconomyManager {

    private final ArisDonatePlugin plugin;
    private final Map<UUID, Long> balances = new HashMap<>();
    private File file;

    public EconomyManager(ArisDonatePlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public long get(UUID id) { return balances.getOrDefault(id, 0L); }

    public void add(UUID id, long amount) {
        balances.merge(id, amount, Long::sum);
        save();
    }

    public boolean take(UUID id, long amount) {
        long cur = get(id);
        if (cur < amount) return false;
        balances.put(id, cur - amount);
        save();
        return true;
    }

    public void set(UUID id, long amount) {
        balances.put(id, Math.max(0, amount));
        save();
    }

    private void load() {
        file = new File(plugin.getDataFolder(), "economy.yml");
        file.getParentFile().mkdirs();
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        if (cfg.isConfigurationSection("balances")) {
            for (String key : cfg.getConfigurationSection("balances").getKeys(false)) {
                try { balances.put(UUID.fromString(key), cfg.getLong("balances." + key)); }
                catch (Exception ignored) {}
            }
        }
    }

    public void save() {
        FileConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Long> e : balances.entrySet()) {
            cfg.set("balances." + e.getKey(), e.getValue());
        }
        try { cfg.save(file); }
        catch (IOException ex) { plugin.getLogger().warning("economy.yml save error: " + ex.getMessage()); }
    }
}

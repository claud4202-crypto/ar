package me.regionblocks.managers;

import me.regionblocks.RegionBlocks;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Хранит баланс валюты «Арис» для каждого игрока.
 */
public class ArisManager {

    private final RegionBlocks plugin;
    private final Map<String, Long> balances = new HashMap<>();
    private File dataFile;

    public ArisManager(RegionBlocks plugin) {
        this.plugin = plugin;
        load();
    }

    // ── API ───────────────────────────────────────────────────────────────────

    public long getBalance(String nick) {
        return balances.getOrDefault(nick.toLowerCase(), 0L);
    }

    public void set(String nick, long amount) {
        balances.put(nick.toLowerCase(), Math.max(0, amount));
        save();
    }

    public void give(String nick, long amount) {
        set(nick, getBalance(nick) + amount);
    }

    public void reset(String nick) {
        balances.put(nick.toLowerCase(), 0L);
        save();
    }

    /** Возвращает false если недостаточно средств */
    public boolean take(String nick, long amount) {
        long bal = getBalance(nick);
        if (bal < amount) return false;
        set(nick, bal - amount);
        return true;
    }

    // ── Сохранение ────────────────────────────────────────────────────────────

    private void load() {
        dataFile = new File(plugin.getDataFolder(), "aris_balances.yml");
        dataFile.getParentFile().mkdirs();
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(dataFile);
        if (cfg.isConfigurationSection("balances")) {
            for (String key : cfg.getConfigurationSection("balances").getKeys(false)) {
                balances.put(key, cfg.getLong("balances." + key));
            }
        }
    }

    public void save() {
        FileConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<String, Long> e : balances.entrySet()) {
            cfg.set("balances." + e.getKey(), e.getValue());
        }
        try { cfg.save(dataFile); }
        catch (IOException ex) { plugin.getLogger().severe("Ошибка сохранения aris_balances.yml: " + ex.getMessage()); }
    }

    /** Все игроки с ненулевым балансом */
    public Map<String, Long> getAllBalances() { return balances; }
}

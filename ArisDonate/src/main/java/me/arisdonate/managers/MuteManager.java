package me.arisdonate.managers;

import me.arisdonate.ArisDonatePlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MuteManager {

    private final ArisDonatePlugin plugin;
    private final Map<UUID, Long> mutes = new HashMap<>(); // 0 = permanent
    private File file;

    public MuteManager(ArisDonatePlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public void mute(UUID id, long untilEpochMillis) {
        mutes.put(id, untilEpochMillis);
        save();
    }

    public void unmute(UUID id) {
        mutes.remove(id);
        save();
    }

    public boolean isMuted(UUID id) {
        Long until = mutes.get(id);
        if (until == null) return false;
        if (until == 0L) return true;
        if (System.currentTimeMillis() > until) {
            mutes.remove(id);
            save();
            return false;
        }
        return true;
    }

    public long until(UUID id) {
        Long v = mutes.get(id);
        return v == null ? -1 : v;
    }

    private void load() {
        file = new File(plugin.getDataFolder(), "mutes.yml");
        file.getParentFile().mkdirs();
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection s = cfg.getConfigurationSection("mutes");
        if (s == null) return;
        for (String k : s.getKeys(false)) {
            try { mutes.put(UUID.fromString(k), s.getLong(k)); } catch (Exception ignored) {}
        }
    }

    public void save() {
        FileConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Long> e : mutes.entrySet()) {
            cfg.set("mutes." + e.getKey(), e.getValue());
        }
        try { cfg.save(file); }
        catch (IOException ex) { plugin.getLogger().severe("Ошибка сохранения mutes.yml: " + ex.getMessage()); }
    }
}

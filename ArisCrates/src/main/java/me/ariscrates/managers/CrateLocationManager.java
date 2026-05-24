package me.ariscrates.managers;

import me.ariscrates.ArisCratesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Хранит расположения крейтов (блок → id крейта).
 */
public class CrateLocationManager {

    private final ArisCratesPlugin plugin;
    private final Map<String, String> locations = new HashMap<>(); // "world,x,y,z" -> crateId
    private File file;

    public CrateLocationManager(ArisCratesPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public String getCrateAt(Location loc) {
        return locations.get(locKey(loc));
    }

    public void setCrate(Location loc, String crateId) {
        locations.put(locKey(loc), crateId);
        save();
    }

    public void removeCrate(Location loc) {
        locations.remove(locKey(loc));
        save();
    }

    private String locKey(Location loc) {
        return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    private void load() {
        file = new File(plugin.getDataFolder(), "locations.yml");
        file.getParentFile().mkdirs();
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection sec = cfg.getConfigurationSection("locations");
        if (sec == null) return;
        for (String key : sec.getKeys(false)) {
            String crateId = sec.getString(key);
            if (crateId != null) locations.put(key, crateId);
        }
    }

    private void save() {
        FileConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<String, String> e : locations.entrySet()) {
            cfg.set("locations." + e.getKey(), e.getValue());
        }
        try { cfg.save(file); } catch (IOException ignored) {}
    }
}

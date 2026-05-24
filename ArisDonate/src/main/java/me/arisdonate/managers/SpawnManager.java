package me.arisdonate.managers;

import me.arisdonate.ArisDonatePlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class SpawnManager {

    private final ArisDonatePlugin plugin;
    private Location spawn;
    private File file;

    public SpawnManager(ArisDonatePlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public Location getSpawn() {
        if (spawn != null) return spawn;
        World w = Bukkit.getWorlds().isEmpty() ? null : Bukkit.getWorlds().get(0);
        return w == null ? null : w.getSpawnLocation();
    }

    public void setSpawn(Location loc) {
        this.spawn = loc;
        save();
    }

    private void load() {
        file = new File(plugin.getDataFolder(), "spawn.yml");
        file.getParentFile().mkdirs();
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection s = cfg.getConfigurationSection("spawn");
        if (s == null) return;
        World w = Bukkit.getWorld(s.getString("world", "world"));
        if (w == null) return;
        spawn = new Location(w, s.getDouble("x"), s.getDouble("y"), s.getDouble("z"),
                (float) s.getDouble("yaw"), (float) s.getDouble("pitch"));
    }

    public void save() {
        FileConfiguration cfg = new YamlConfiguration();
        if (spawn != null) {
            cfg.set("spawn.world", spawn.getWorld().getName());
            cfg.set("spawn.x", spawn.getX());
            cfg.set("spawn.y", spawn.getY());
            cfg.set("spawn.z", spawn.getZ());
            cfg.set("spawn.yaw", spawn.getYaw());
            cfg.set("spawn.pitch", spawn.getPitch());
        }
        try { cfg.save(file); }
        catch (IOException ex) { plugin.getLogger().severe("Ошибка сохранения spawn.yml: " + ex.getMessage()); }
    }
}

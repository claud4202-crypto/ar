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
import java.util.*;

public class WarpManager {

    private final ArisDonatePlugin plugin;
    private final Map<String, Location> warps = new HashMap<>();
    private File file;

    public WarpManager(ArisDonatePlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public void setWarp(String name, Location loc) {
        warps.put(name.toLowerCase(), loc);
        save();
    }

    public boolean delWarp(String name) {
        boolean r = warps.remove(name.toLowerCase()) != null;
        if (r) save();
        return r;
    }

    public Location getWarp(String name) { return warps.get(name.toLowerCase()); }
    public Set<String> listWarps()       { return warps.keySet(); }

    private void load() {
        file = new File(plugin.getDataFolder(), "warps.yml");
        file.getParentFile().mkdirs();
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection s = cfg.getConfigurationSection("warps");
        if (s == null) return;
        for (String name : s.getKeys(false)) {
            ConfigurationSection l = s.getConfigurationSection(name);
            World w = Bukkit.getWorld(l.getString("world", "world"));
            if (w == null) continue;
            warps.put(name.toLowerCase(), new Location(w,
                    l.getDouble("x"), l.getDouble("y"), l.getDouble("z"),
                    (float) l.getDouble("yaw"), (float) l.getDouble("pitch")));
        }
    }

    public void save() {
        FileConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<String, Location> e : warps.entrySet()) {
            String k = "warps." + e.getKey() + ".";
            Location l = e.getValue();
            cfg.set(k + "world", l.getWorld().getName());
            cfg.set(k + "x", l.getX());
            cfg.set(k + "y", l.getY());
            cfg.set(k + "z", l.getZ());
            cfg.set(k + "yaw", l.getYaw());
            cfg.set(k + "pitch", l.getPitch());
        }
        try { cfg.save(file); }
        catch (IOException ex) { plugin.getLogger().severe("Ошибка сохранения warps.yml: " + ex.getMessage()); }
    }
}

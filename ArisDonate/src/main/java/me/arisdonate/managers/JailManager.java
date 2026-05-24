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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class JailManager {

    private final ArisDonatePlugin plugin;
    private Location jailLocation;
    private final Set<UUID> jailed = new HashSet<>();
    private File file;

    public JailManager(ArisDonatePlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public Location getJail() { return jailLocation; }

    public void setJail(Location l) {
        this.jailLocation = l;
        save();
    }

    public void jail(UUID id) { jailed.add(id); save(); }
    public void unjail(UUID id) { jailed.remove(id); save(); }
    public boolean isJailed(UUID id) { return jailed.contains(id); }

    private void load() {
        file = new File(plugin.getDataFolder(), "jail.yml");
        file.getParentFile().mkdirs();
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection s = cfg.getConfigurationSection("location");
        if (s != null) {
            World w = Bukkit.getWorld(s.getString("world", "world"));
            if (w != null) {
                jailLocation = new Location(w, s.getDouble("x"), s.getDouble("y"), s.getDouble("z"),
                        (float) s.getDouble("yaw"), (float) s.getDouble("pitch"));
            }
        }
        for (String id : cfg.getStringList("jailed")) {
            try { jailed.add(UUID.fromString(id)); } catch (Exception ignored) {}
        }
    }

    public void save() {
        FileConfiguration cfg = new YamlConfiguration();
        if (jailLocation != null) {
            cfg.set("location.world", jailLocation.getWorld().getName());
            cfg.set("location.x", jailLocation.getX());
            cfg.set("location.y", jailLocation.getY());
            cfg.set("location.z", jailLocation.getZ());
            cfg.set("location.yaw", jailLocation.getYaw());
            cfg.set("location.pitch", jailLocation.getPitch());
        }
        cfg.set("jailed", jailed.stream().map(UUID::toString).toList());
        try { cfg.save(file); }
        catch (IOException ex) { plugin.getLogger().severe("Ошибка сохранения jail.yml: " + ex.getMessage()); }
    }
}

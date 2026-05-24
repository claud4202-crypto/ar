package me.arisdonate.managers;

import me.arisdonate.ArisDonatePlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

/** Хранение /home'ов игроков. */
public class HomeManager {

    private final ArisDonatePlugin plugin;
    private final Map<UUID, Map<String, Location>> homes = new HashMap<>();
    private File file;

    public HomeManager(ArisDonatePlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public boolean setHome(Player p, String name) {
        int limit = plugin.getDonateManager().getPlayerRank(p.getName()) != null
                ? plugin.getDonateManager().getPlayerRank(p.getName()).homeLimit()
                : plugin.getConfig().getInt("default-home-limit", 1);
        Map<String, Location> map = homes.computeIfAbsent(p.getUniqueId(), k -> new HashMap<>());
        if (!map.containsKey(name.toLowerCase()) && map.size() >= limit) {
            return false;
        }
        map.put(name.toLowerCase(), p.getLocation());
        save();
        return true;
    }

    public boolean delHome(UUID id, String name) {
        Map<String, Location> map = homes.get(id);
        if (map == null) return false;
        boolean removed = map.remove(name.toLowerCase()) != null;
        if (removed) save();
        return removed;
    }

    public boolean renameHome(UUID id, String oldName, String newName) {
        Map<String, Location> map = homes.get(id);
        if (map == null) return false;
        Location loc = map.remove(oldName.toLowerCase());
        if (loc == null) return false;
        map.put(newName.toLowerCase(), loc);
        save();
        return true;
    }

    public Location getHome(UUID id, String name) {
        Map<String, Location> map = homes.get(id);
        return map == null ? null : map.get(name.toLowerCase());
    }

    public Set<String> listHomes(UUID id) {
        Map<String, Location> map = homes.get(id);
        return map == null ? Collections.emptySet() : map.keySet();
    }

    private void load() {
        file = new File(plugin.getDataFolder(), "homes.yml");
        file.getParentFile().mkdirs();
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection root = cfg.getConfigurationSection("homes");
        if (root == null) return;
        for (String idStr : root.getKeys(false)) {
            UUID id;
            try { id = UUID.fromString(idStr); } catch (Exception e) { continue; }
            ConfigurationSection s = root.getConfigurationSection(idStr);
            Map<String, Location> map = new HashMap<>();
            for (String name : s.getKeys(false)) {
                ConfigurationSection l = s.getConfigurationSection(name);
                World w = Bukkit.getWorld(l.getString("world", "world"));
                if (w == null) continue;
                map.put(name.toLowerCase(), new Location(
                        w, l.getDouble("x"), l.getDouble("y"), l.getDouble("z"),
                        (float) l.getDouble("yaw"), (float) l.getDouble("pitch")));
            }
            homes.put(id, map);
        }
    }

    public void save() {
        FileConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Map<String, Location>> e : homes.entrySet()) {
            String base = "homes." + e.getKey() + ".";
            for (Map.Entry<String, Location> h : e.getValue().entrySet()) {
                Location l = h.getValue();
                String hk = base + h.getKey() + ".";
                cfg.set(hk + "world", l.getWorld().getName());
                cfg.set(hk + "x", l.getX());
                cfg.set(hk + "y", l.getY());
                cfg.set(hk + "z", l.getZ());
                cfg.set(hk + "yaw", l.getYaw());
                cfg.set(hk + "pitch", l.getPitch());
            }
        }
        try { cfg.save(file); }
        catch (IOException ex) { plugin.getLogger().severe("Ошибка сохранения homes.yml: " + ex.getMessage()); }
    }
}

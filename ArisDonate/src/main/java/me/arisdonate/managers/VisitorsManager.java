package me.arisdonate.managers;

import me.arisdonate.ArisDonatePlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Хранит список UUID всех игроков, которые когда-либо заходили на сервер.
 * Сохраняется в plugins/ArisDonate/visitors.yml.
 */
public class VisitorsManager {

    private final ArisDonatePlugin plugin;
    private final Set<UUID> visitors = new HashSet<>();
    private File file;

    public VisitorsManager(ArisDonatePlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public synchronized void register(UUID id) {
        if (visitors.add(id)) save();
    }

    public synchronized int total() {
        return visitors.size();
    }

    private void load() {
        file = new File(plugin.getDataFolder(), "visitors.yml");
        file.getParentFile().mkdirs();
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        for (String s : cfg.getStringList("visitors")) {
            try { visitors.add(UUID.fromString(s)); } catch (Exception ignored) {}
        }
    }

    private void save() {
        FileConfiguration cfg = new YamlConfiguration();
        cfg.set("visitors", visitors.stream().map(UUID::toString).toList());
        try {
            cfg.save(file);
        } catch (IOException ex) {
            plugin.getLogger().warning("Не смог сохранить visitors.yml: " + ex.getMessage());
        }
    }
}

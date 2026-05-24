package me.arisdonate.managers;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.models.DonateRank;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Хранит ранги донатов и текущих обладателей.
 */
public class DonateManager {

    private final ArisDonatePlugin plugin;
    private final Map<String, DonateRank> ranks = new LinkedHashMap<>();
    private final Map<String, String> playerRanks = new HashMap<>(); // lowercase nick -> rank id
    private File playersFile;

    public DonateManager(ArisDonatePlugin plugin) {
        this.plugin = plugin;
        loadRanks();
        loadPlayers();
    }

    private void loadRanks() {
        FileConfiguration cfg = plugin.getConfig();
        ConfigurationSection sect = cfg.getConfigurationSection("ranks");
        if (sect == null) return;
        ranks.clear();
        for (String key : sect.getKeys(false)) {
            ConfigurationSection r = sect.getConfigurationSection(key);
            if (r == null) continue;
            DonateRank rank = new DonateRank(
                    key.toLowerCase(),
                    r.getString("display-name", key),
                    r.getString("start-hex", "FFFFFF").replace("#", ""),
                    r.getString("end-hex", "FFFFFF").replace("#", ""),
                    r.getInt("region-limit", 0),
                    r.getInt("home-limit", 1),
                    r.getInt("weight", 0),
                    parseMaterial(r.getString("shulker", "SHULKER_BOX"), Material.SHULKER_BOX),
                    r.getStringList("description"),
                    r.getStringList("commands"),
                    r.getInt("gui-slot", 13),
                    r.getString("kit", null)
            );
            ranks.put(rank.id(), rank);
        }
    }

    private static Material parseMaterial(String name, Material def) {
        if (name == null) return def;
        Material m = Material.matchMaterial(name);
        return m == null ? def : m;
    }

    private void loadPlayers() {
        playersFile = new File(plugin.getDataFolder(), "players.yml");
        playersFile.getParentFile().mkdirs();
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(playersFile);
        if (cfg.isConfigurationSection("ranks")) {
            for (String key : cfg.getConfigurationSection("ranks").getKeys(false)) {
                playerRanks.put(key.toLowerCase(), cfg.getString("ranks." + key));
            }
        }
    }

    public void save() {
        FileConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<String, String> e : playerRanks.entrySet()) {
            cfg.set("ranks." + e.getKey(), e.getValue());
        }
        try { cfg.save(playersFile); }
        catch (IOException ex) { plugin.getLogger().severe("Ошибка сохранения players.yml: " + ex.getMessage()); }
    }

    // ── API ───────────────────────────────────────────────────────────────────

    public DonateRank getRank(String id) {
        return id == null ? null : ranks.get(id.toLowerCase());
    }

    public DonateRank getPlayerRank(String nick) {
        if (nick == null) return null;
        String id = playerRanks.get(nick.toLowerCase());
        return id == null ? null : ranks.get(id);
    }

    public void setPlayerRank(String nick, String rankId) {
        if (rankId == null) {
            playerRanks.remove(nick.toLowerCase());
        } else {
            DonateRank r = ranks.get(rankId.toLowerCase());
            if (r == null) return;
            playerRanks.put(nick.toLowerCase(), r.id());
        }
        save();
    }

    public void removePlayer(String nick) {
        playerRanks.remove(nick.toLowerCase());
        save();
    }

    public List<DonateRank> getOrderedRanks() {
        List<DonateRank> list = new ArrayList<>(ranks.values());
        list.sort((a, b) -> Integer.compare(b.weight(), a.weight()));
        return list;
    }

    public Map<String, DonateRank> getRanks() { return ranks; }
    public Map<String, String> getPlayerRanks() { return playerRanks; }
}

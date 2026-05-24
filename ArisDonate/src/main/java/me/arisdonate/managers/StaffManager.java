package me.arisdonate.managers;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.models.StaffRank;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Управляет служебными рангами (Helper, Moder, Admin, Owner и пр.).
 * Ранги читаются из config.yml → блок staff-ranks.
 * Игроки→ранги сохраняются в staff_players.yml.
 *
 * Если staff-ranks в config.yml пусто, используется встроенный «жёсткий» список,
 * чтобы плагин работал из коробки.
 */
public class StaffManager {

    private final ArisDonatePlugin plugin;
    private final Map<String, StaffRank> ranks = new LinkedHashMap<>();
    private final Map<String, String> players = new HashMap<>();   // lower(name) → rankId
    private File playersFile;

    public StaffManager(ArisDonatePlugin plugin) {
        this.plugin = plugin;
        loadRanks();
        loadPlayers();
    }

    public Map<String, StaffRank> ranks() { return ranks; }

    public StaffRank getRank(String id) {
        return id == null ? null : ranks.get(id.toLowerCase(Locale.ROOT));
    }

    public StaffRank getPlayerRank(String name) {
        if (name == null) return null;
        String id = players.get(name.toLowerCase(Locale.ROOT));
        if (id == null) return null;
        return ranks.get(id);
    }

    public synchronized void setPlayer(String name, String rankId) {
        StaffRank r = getRank(rankId);
        if (r == null) return;
        players.put(name.toLowerCase(Locale.ROOT), r.id());
        savePlayers();
    }

    public synchronized void clearPlayer(String name) {
        players.remove(name.toLowerCase(Locale.ROOT));
        savePlayers();
    }

    // ── load / save ───────────────────────────────────────────────────────────

    public void reload() {
        ranks.clear();
        loadRanks();
        // players не перечитываем — это бд игроков, она независима
    }

    private void loadRanks() {
        ConfigurationSection sec = plugin.getConfig().getConfigurationSection("staff-ranks");
        if (sec != null && !sec.getKeys(false).isEmpty()) {
            for (String key : sec.getKeys(false)) {
                String path = "staff-ranks." + key + ".";
                String display = plugin.getConfig().getString(path + "display-name", key);
                String start   = stripHash(plugin.getConfig().getString(path + "start-hex", "FFFFFF"));
                String end     = stripHash(plugin.getConfig().getString(path + "end-hex",   "AAAAAA"));
                int weight     = plugin.getConfig().getInt(path + "weight", 0);
                ranks.put(key.toLowerCase(Locale.ROOT),
                        new StaffRank(key.toLowerCase(Locale.ROOT), display, start, end, weight));
            }
            return;
        }

        // ── fallback: встроенные ранги (от Helper до Owner) ──
        register("helper",   "Helper",   "55FFFF", "55AADD",  100);
        register("dhelper",  "D.Helper", "55FFFF", "55AAFF",  110);
        register("mlmoder",  "ML.Moder", "55FF55", "AAFF55",  120);
        register("moder",    "Moder",    "55FF55", "00AA00",  130);
        register("dmoder",   "D.Moder",  "00FF77", "00AA77",  140);
        register("sponsor",  "Sponsor",  "FFD700", "FFAA00",  150);
        register("mladmin",  "ML.Admin", "FFAA00", "FF7700",  160);
        register("admin",    "Admin",    "FF5555", "AA0000",  170);
        register("dadmin",   "D.Admin",  "FF5555", "FF1111",  180);
        register("gladmin",  "GL.Admin", "FF55FF", "AA00AA",  190);
        register("curator",  "Curator",  "FF55FF", "FFAAFF",  200);
        register("owner",    "Owner",    "FFD700", "FF3300",  210);
    }

    private void register(String id, String name, String startHex, String endHex, int weight) {
        ranks.put(id, new StaffRank(id, name, startHex, endHex, weight));
    }

    private void loadPlayers() {
        playersFile = new File(plugin.getDataFolder(), "staff_players.yml");
        playersFile.getParentFile().mkdirs();
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(playersFile);
        ConfigurationSection sec = cfg.getConfigurationSection("players");
        if (sec == null) return;
        for (String name : sec.getKeys(false)) {
            String rankId = sec.getString(name);
            if (rankId == null) continue;
            String low = rankId.toLowerCase(Locale.ROOT);
            if (ranks.containsKey(low)) players.put(name.toLowerCase(Locale.ROOT), low);
        }
    }

    private void savePlayers() {
        FileConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<String, String> e : players.entrySet()) {
            cfg.set("players." + e.getKey(), e.getValue());
        }
        try {
            cfg.save(playersFile);
        } catch (IOException ex) {
            plugin.getLogger().warning("Не смог сохранить staff_players.yml: " + ex.getMessage());
        }
    }

    private static String stripHash(String s) { return s == null ? "FFFFFF" : (s.startsWith("#") ? s.substring(1) : s); }
}

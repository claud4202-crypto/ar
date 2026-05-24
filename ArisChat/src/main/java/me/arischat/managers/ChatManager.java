package me.arischat.managers;

import me.arischat.ArisChatPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ChatManager {

    public enum ChatMode { LOCAL, GLOBAL }

    private final ArisChatPlugin plugin;
    private final Map<UUID, ChatMode> modes = new HashMap<>();
    private final Map<UUID, UUID> lastMsg = new HashMap<>(); // reply target
    private final Map<UUID, Set<UUID>> ignores = new HashMap<>();
    private final Map<UUID, Long> lastMessageTime = new HashMap<>();
    private final Set<UUID> spies = new HashSet<>();

    public ChatManager(ArisChatPlugin plugin) {
        this.plugin = plugin;
        loadIgnores();
    }

    public ChatMode getMode(Player p) {
        ChatMode def = "local".equalsIgnoreCase(plugin.getConfig().getString("default-mode", "global"))
                ? ChatMode.LOCAL : ChatMode.GLOBAL;
        return modes.getOrDefault(p.getUniqueId(), def);
    }

    public void setMode(Player p, ChatMode mode) { modes.put(p.getUniqueId(), mode); }

    public void toggleMode(Player p) {
        setMode(p, getMode(p) == ChatMode.GLOBAL ? ChatMode.LOCAL : ChatMode.GLOBAL);
    }

    public UUID getReplyTarget(Player p) { return lastMsg.get(p.getUniqueId()); }
    public void setReplyTarget(UUID sender, UUID target) { lastMsg.put(sender, target); }

    public boolean isIgnoring(Player p, UUID target) {
        Set<UUID> set = ignores.get(p.getUniqueId());
        return set != null && set.contains(target);
    }

    public boolean toggleIgnore(Player p, UUID target) {
        Set<UUID> set = ignores.computeIfAbsent(p.getUniqueId(), k -> new HashSet<>());
        boolean added;
        if (set.contains(target)) { set.remove(target); added = false; }
        else { set.add(target); added = true; }
        saveIgnores();
        return added;
    }

    public boolean isSpy(Player p) { return spies.contains(p.getUniqueId()); }
    public boolean toggleSpy(Player p) {
        if (spies.contains(p.getUniqueId())) { spies.remove(p.getUniqueId()); return false; }
        else { spies.add(p.getUniqueId()); return true; }
    }
    public Set<UUID> getSpies() { return spies; }

    public boolean isSpamming(Player p) {
        long delay = plugin.getConfig().getLong("antispam-delay-ms", 1000);
        long now = System.currentTimeMillis();
        Long last = lastMessageTime.get(p.getUniqueId());
        lastMessageTime.put(p.getUniqueId(), now);
        return last != null && (now - last) < delay;
    }

    public String filterCaps(String msg) {
        int pct = plugin.getConfig().getInt("anticaps-percent", 70);
        int minLen = plugin.getConfig().getInt("anticaps-min-length", 5);
        if (pct <= 0 || msg.length() < minLen) return msg;
        long upper = msg.chars().filter(Character::isUpperCase).count();
        if ((upper * 100 / msg.length()) > pct) return msg.toLowerCase();
        return msg;
    }

    public String filterBannedWords(String msg) {
        List<String> banned = plugin.getConfig().getStringList("banned-words");
        String result = msg;
        for (String w : banned) {
            if (w == null || w.isEmpty()) continue;
            result = result.replaceAll("(?i)" + java.util.regex.Pattern.quote(w), "***");
        }
        return result;
    }

    private void loadIgnores() {
        File f = new File(plugin.getDataFolder(), "ignores.yml");
        if (!f.exists()) return;
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        for (String key : cfg.getKeys(false)) {
            try {
                UUID uid = UUID.fromString(key);
                Set<UUID> set = new HashSet<>();
                for (String s : cfg.getStringList(key)) {
                    try { set.add(UUID.fromString(s)); } catch (Exception ignored) {}
                }
                if (!set.isEmpty()) ignores.put(uid, set);
            } catch (Exception ignored) {}
        }
    }

    private void saveIgnores() {
        File f = new File(plugin.getDataFolder(), "ignores.yml");
        f.getParentFile().mkdirs();
        FileConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Set<UUID>> e : ignores.entrySet()) {
            List<String> list = new ArrayList<>();
            for (UUID u : e.getValue()) list.add(u.toString());
            cfg.set(e.getKey().toString(), list);
        }
        try { cfg.save(f); } catch (IOException ignored) {}
    }
}

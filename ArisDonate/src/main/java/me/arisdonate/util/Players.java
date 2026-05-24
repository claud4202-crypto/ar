package me.arisdonate.util;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("deprecation")
public final class Players {

    private Players() {}

    public static Player online(String name) {
        if (name == null) return null;
        return Bukkit.getPlayerExact(name);
    }

    public static OfflinePlayer offline(String name) {
        if (name == null) return null;
        OfflinePlayer p = Bukkit.getOfflinePlayerIfCached(name);
        if (p != null) return p;
        return Bukkit.getOfflinePlayer(name);
    }

    public static List<String> onlineNames(String prefix) {
        List<String> out = new ArrayList<>();
        String low = prefix == null ? "" : prefix.toLowerCase();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().toLowerCase().startsWith(low)) out.add(p.getName());
        }
        return out;
    }

    public static UUID idOf(String name) {
        OfflinePlayer op = offline(name);
        return op == null ? null : op.getUniqueId();
    }
}

package me.arisdonate.managers;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AfkManager {

    private final Set<UUID> afk = new HashSet<>();

    public boolean toggle(Player p) {
        if (!afk.add(p.getUniqueId())) {
            afk.remove(p.getUniqueId());
            return false;
        }
        return true;
    }

    public boolean isAfk(UUID id) { return afk.contains(id); }
}

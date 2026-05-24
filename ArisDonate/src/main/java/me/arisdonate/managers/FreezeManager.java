package me.arisdonate.managers;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FreezeManager {

    private final Set<UUID> frozen = new HashSet<>();

    public boolean toggle(Player p) {
        if (!frozen.add(p.getUniqueId())) {
            frozen.remove(p.getUniqueId());
            return false;
        }
        return true;
    }

    public boolean isFrozen(UUID id) { return frozen.contains(id); }
}

package me.arisdonate.managers;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Хранит последнюю локацию для /back. */
public class BackManager {

    private final Map<UUID, Location> lastLocations = new HashMap<>();

    public void store(Player p) {
        lastLocations.put(p.getUniqueId(), p.getLocation());
    }

    public void store(Player p, Location loc) {
        lastLocations.put(p.getUniqueId(), loc);
    }

    public Location get(UUID id) {
        return lastLocations.get(id);
    }
}

package me.ariscrates.models;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public record Crate(
        String id,
        String displayName,
        Material keyMaterial,
        String keyName,
        Material blockMaterial,
        boolean broadcastWin,
        String permission,
        String hologramText,
        List<CrateReward> rewards
) {
    public CrateReward roll() {
        double total = rewards.stream().mapToDouble(CrateReward::chance).sum();
        double roll = ThreadLocalRandom.current().nextDouble(total);
        double acc = 0;
        for (CrateReward r : rewards) {
            acc += r.chance();
            if (roll < acc) return r;
        }
        return rewards.get(rewards.size() - 1);
    }

    public boolean requiresPermission() {
        return permission != null && !permission.isEmpty();
    }

    public boolean canOpen(Player p) {
        if (!requiresPermission()) return true;
        return p.hasPermission(permission);
    }

    public boolean isDonateType() {
        return rewards.stream().anyMatch(CrateReward::isDonateReward);
    }
}

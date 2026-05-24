package me.ariscrates.models;

import org.bukkit.Material;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public record Crate(
        String id,
        String displayName,
        Material keyMaterial,
        String keyName,
        Material blockMaterial,
        boolean broadcastWin,
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
}

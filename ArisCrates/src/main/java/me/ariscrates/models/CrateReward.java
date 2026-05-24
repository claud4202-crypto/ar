package me.ariscrates.models;

import org.bukkit.inventory.ItemStack;

/**
 * Награда крейта.
 * Если donateRankId != null — это донат-награда (выдаётся ранг, а не предмет).
 */
public record CrateReward(
        String display,
        ItemStack item,
        double chance,
        String rarity,
        String donateRankId
) {
    public boolean isDonateReward() {
        return donateRankId != null && !donateRankId.isEmpty();
    }
}

package me.ariscrates.models;

import org.bukkit.inventory.ItemStack;

public record CrateReward(
        String display,
        ItemStack item,
        double chance
) {}

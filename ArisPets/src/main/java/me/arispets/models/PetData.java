package me.arispets.models;

import org.bukkit.entity.EntityType;

public record PetData(
        String id,
        String displayName,
        EntityType entityType,
        String permission,
        boolean baby
) {}

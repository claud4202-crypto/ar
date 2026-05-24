package me.arispets.managers;

import me.arispets.ArisPetsPlugin;
import me.arispets.models.PetData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PetManager {

    private static final String META_KEY = "arispets_owner";
    private static final String META_PET = "arispets_id";

    private final ArisPetsPlugin plugin;
    private final Map<String, PetData> petTypes = new LinkedHashMap<>();
    private final Map<UUID, Entity> activePets = new HashMap<>();
    private final Map<UUID, Set<String>> ownedPets = new HashMap<>(); // uuid -> set of pet ids
    private final Map<UUID, String> petNames = new HashMap<>(); // uuid -> custom pet name
    private File dataFile;

    public PetManager(ArisPetsPlugin plugin) {
        this.plugin = plugin;
        loadPetTypes();
        loadPlayerData();
    }

    public Collection<PetData> allPetTypes() { return petTypes.values(); }
    public PetData getPetType(String id) { return petTypes.get(id.toLowerCase(Locale.ROOT)); }

    public boolean hasPet(UUID player, String petId) {
        Set<String> owned = ownedPets.get(player);
        return owned != null && owned.contains(petId.toLowerCase(Locale.ROOT));
    }

    public void givePet(UUID player, String petId) {
        ownedPets.computeIfAbsent(player, k -> new HashSet<>()).add(petId.toLowerCase(Locale.ROOT));
        savePlayerData();
    }

    public void removePetOwnership(UUID player, String petId) {
        Set<String> owned = ownedPets.get(player);
        if (owned != null) { owned.remove(petId.toLowerCase(Locale.ROOT)); savePlayerData(); }
    }

    public Set<String> getOwnedPets(UUID player) {
        return ownedPets.getOrDefault(player, Collections.emptySet());
    }

    public Entity getActivePet(UUID player) { return activePets.get(player); }

    public void spawnPet(Player player, PetData data) {
        despawn(player);
        Location loc = player.getLocation().add(1, 0, 1);
        Entity entity = player.getWorld().spawnEntity(loc, data.entityType());

        // Configure
        entity.customName(Msg.parse(getCustomName(player.getUniqueId(), data)));
        entity.setCustomNameVisible(true);
        entity.setPersistent(false);
        entity.setInvulnerable(true);
        entity.setSilent(true);

        if (entity instanceof Mob mob) {
            mob.setAware(false);
            mob.setCollidable(false);
        }
        if (entity instanceof Tameable t) {
            t.setTamed(true);
            t.setOwner(player);
        }
        if (data.baby() && entity instanceof Ageable a) {
            a.setBaby();
            a.setAgeLock(true);
        }

        entity.setMetadata(META_KEY, new FixedMetadataValue(plugin, player.getUniqueId().toString()));
        entity.setMetadata(META_PET, new FixedMetadataValue(plugin, data.id()));

        activePets.put(player.getUniqueId(), entity);
    }

    public void despawn(Player player) {
        Entity old = activePets.remove(player.getUniqueId());
        if (old != null && old.isValid()) old.remove();
    }

    public void despawnAll() {
        for (Entity e : activePets.values()) if (e.isValid()) e.remove();
        activePets.clear();
    }

    public boolean isOurPet(Entity entity) {
        return entity.hasMetadata(META_KEY);
    }

    public void tick() {
        double followDist = plugin.getConfig().getDouble("follow-distance", 3.0);
        double speed = plugin.getConfig().getDouble("follow-speed", 0.3);
        double tpDist = plugin.getConfig().getDouble("teleport-distance", 20.0);

        Iterator<Map.Entry<UUID, Entity>> it = activePets.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Entity> entry = it.next();
            Player owner = Bukkit.getPlayer(entry.getKey());
            Entity pet = entry.getValue();

            if (owner == null || !owner.isOnline() || !pet.isValid()) {
                if (pet.isValid()) pet.remove();
                it.remove();
                continue;
            }

            if (!pet.getWorld().equals(owner.getWorld())) {
                pet.teleport(owner.getLocation().add(1, 0, 1));
                continue;
            }

            double dist = pet.getLocation().distance(owner.getLocation());
            if (dist > tpDist) {
                pet.teleport(owner.getLocation().add(1, 0, 1));
            } else if (dist > followDist) {
                Location target = owner.getLocation();
                Location curr = pet.getLocation();
                double dx = target.getX() - curr.getX();
                double dy = target.getY() - curr.getY();
                double dz = target.getZ() - curr.getZ();
                double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
                if (len > 0) {
                    double mx = (dx / len) * speed;
                    double my = (dy / len) * speed;
                    double mz = (dz / len) * speed;
                    pet.setVelocity(new org.bukkit.util.Vector(mx, my + 0.05, mz));
                }
            }
        }
    }

    public void setCustomPetName(UUID player, String name) {
        petNames.put(player, name);
        Entity pet = activePets.get(player);
        if (pet != null && pet.isValid()) {
            pet.customName(Msg.parse(name));
        }
        savePlayerData();
    }

    private String getCustomName(UUID player, PetData data) {
        String custom = petNames.get(player);
        if (custom != null && !custom.isEmpty()) return custom;
        return data.displayName();
    }

    public void reload() {
        petTypes.clear();
        loadPetTypes();
    }

    private void loadPetTypes() {
        ConfigurationSection sec = plugin.getConfig().getConfigurationSection("pets");
        if (sec == null) return;
        for (String id : sec.getKeys(false)) {
            ConfigurationSection ps = sec.getConfigurationSection(id);
            if (ps == null) continue;
            String display = ps.getString("display-name", id);
            String etName = ps.getString("entity-type", "WOLF");
            EntityType et;
            try { et = EntityType.valueOf(etName.toUpperCase(Locale.ROOT)); } catch (Exception e) { continue; }
            String perm = ps.getString("permission", "");
            boolean baby = ps.getBoolean("baby", false);
            petTypes.put(id.toLowerCase(Locale.ROOT), new PetData(id.toLowerCase(Locale.ROOT), display, et, perm, baby));
        }
    }

    private void loadPlayerData() {
        dataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        dataFile.getParentFile().mkdirs();
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(dataFile);
        ConfigurationSection sec = cfg.getConfigurationSection("players");
        if (sec == null) return;
        for (String key : sec.getKeys(false)) {
            try {
                UUID uid = UUID.fromString(key);
                List<String> pets = sec.getStringList(key + ".owned");
                ownedPets.put(uid, new HashSet<>(pets));
                String name = sec.getString(key + ".name");
                if (name != null && !name.isEmpty()) petNames.put(uid, name);
            } catch (Exception ignored) {}
        }
    }

    private void savePlayerData() {
        FileConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Set<String>> e : ownedPets.entrySet()) {
            cfg.set("players." + e.getKey() + ".owned", new ArrayList<>(e.getValue()));
            String name = petNames.get(e.getKey());
            if (name != null) cfg.set("players." + e.getKey() + ".name", name);
        }
        try { cfg.save(dataFile); } catch (IOException ignored) {}
    }
}

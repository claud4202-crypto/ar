package me.ariscrates.commands;

import me.ariscrates.ArisCratesPlugin;
import me.ariscrates.managers.Msg;
import me.ariscrates.models.Crate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * /free — даёт 1 ключ от бесплатного донат-крейта каждые 24 часа.
 */
public class FreeCommand implements CommandExecutor {

    private final ArisCratesPlugin plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private File cooldownFile;

    private static final long COOLDOWN_MS = 24 * 60 * 60 * 1000L; // 24 часа

    public FreeCommand(ArisCratesPlugin plugin) {
        this.plugin = plugin;
        loadCooldowns();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(Msg.parse("&cТолько для игроков."));
            return true;
        }

        String freeCrateId = plugin.getConfig().getString("free-crate", "donate_free");
        Crate crate = plugin.getCrateManager().getCrate(freeCrateId);
        if (crate == null) {
            p.sendMessage(Msg.parse("&cБесплатный крейт не настроен!"));
            return true;
        }

        long now = System.currentTimeMillis();
        long lastUsed = cooldowns.getOrDefault(p.getUniqueId(), 0L);
        long remaining = (lastUsed + COOLDOWN_MS) - now;

        if (remaining > 0) {
            long hours = remaining / (60 * 60 * 1000);
            long mins = (remaining % (60 * 60 * 1000)) / (60 * 1000);
            p.sendMessage(Msg.parse("&cВы уже получили бесплатный ключ! Следующий через &e"
                    + hours + "ч " + mins + "м&c."));
            return true;
        }

        ItemStack key = plugin.getCrateManager().createKey(crate, 1);
        var leftover = p.getInventory().addItem(key);
        for (ItemStack drop : leftover.values()) {
            p.getWorld().dropItemNaturally(p.getLocation(), drop);
        }

        cooldowns.put(p.getUniqueId(), now);
        saveCooldowns();

        p.sendMessage(Msg.parse("&a&l✓ &aВы получили бесплатный ключ от " + crate.displayName() + "&a!"));
        p.sendMessage(Msg.parse("&7Следующий ключ будет доступен через &e24 часа&7."));
        return true;
    }

    private void loadCooldowns() {
        cooldownFile = new File(plugin.getDataFolder(), "free-cooldowns.yml");
        cooldownFile.getParentFile().mkdirs();
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(cooldownFile);
        if (cfg.isConfigurationSection("cooldowns")) {
            for (String key : cfg.getConfigurationSection("cooldowns").getKeys(false)) {
                try {
                    cooldowns.put(UUID.fromString(key), cfg.getLong("cooldowns." + key));
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    private void saveCooldowns() {
        FileConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Long> e : cooldowns.entrySet()) {
            cfg.set("cooldowns." + e.getKey().toString(), e.getValue());
        }
        try { cfg.save(cooldownFile); } catch (IOException ignored) {}
    }
}

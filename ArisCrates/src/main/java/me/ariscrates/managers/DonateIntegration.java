package me.ariscrates.managers;

import me.ariscrates.ArisCratesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

/**
 * Интеграция с ArisDonate через рефлексию (softdepend).
 * Позволяет выдавать донат-ранги и проверять текущий ранг.
 */
public class DonateIntegration {

    private final ArisCratesPlugin plugin;
    private boolean available;
    private Plugin arisDonatePlugin;

    public DonateIntegration(ArisCratesPlugin plugin) {
        this.plugin = plugin;
        init();
    }

    private void init() {
        arisDonatePlugin = Bukkit.getPluginManager().getPlugin("ArisDonate");
        if (arisDonatePlugin == null || !arisDonatePlugin.isEnabled()) {
            plugin.getLogger().warning("ArisDonate не найден — донат-крейты не будут выдавать ранги.");
            available = false;
            return;
        }
        available = true;
        plugin.getLogger().info("ArisDonate интеграция активна.");
    }

    public boolean isAvailable() { return available; }

    private Object getDonateManager() throws Exception {
        Method m = arisDonatePlugin.getClass().getMethod("getDonateManager");
        return m.invoke(arisDonatePlugin);
    }

    public int getPlayerDonateWeight(String nick) {
        if (!available) return 0;
        try {
            Object dm = getDonateManager();
            Method getPlayerRank = dm.getClass().getMethod("getPlayerRank", String.class);
            Object rank = getPlayerRank.invoke(dm, nick);
            if (rank == null) return 0;
            Method weight = rank.getClass().getMethod("weight");
            return (int) weight.invoke(rank);
        } catch (Exception e) {
            plugin.getLogger().warning("Ошибка getPlayerDonateWeight: " + e.getMessage());
            return 0;
        }
    }

    public int getRankWeight(String rankId) {
        if (!available) return 0;
        try {
            Object dm = getDonateManager();
            Method getRank = dm.getClass().getMethod("getRank", String.class);
            Object rank = getRank.invoke(dm, rankId);
            if (rank == null) return 0;
            Method weight = rank.getClass().getMethod("weight");
            return (int) weight.invoke(rank);
        } catch (Exception e) {
            plugin.getLogger().warning("Ошибка getRankWeight: " + e.getMessage());
            return 0;
        }
    }

    public String getRankGradientName(String rankId) {
        if (!available) return rankId;
        try {
            Object dm = getDonateManager();
            Method getRank = dm.getClass().getMethod("getRank", String.class);
            Object rank = getRank.invoke(dm, rankId);
            if (rank == null) return rankId;
            Method gradientName = rank.getClass().getMethod("gradientName");
            return (String) gradientName.invoke(rank);
        } catch (Exception e) {
            return rankId;
        }
    }

    /**
     * Выдаёт донат-ранг. Если у игрока уже выше — не понижает.
     * Возвращает true если ранг был выдан.
     */
    public boolean giveRankIfHigher(Player player, String rankId) {
        if (!available) {
            plugin.getLogger().warning("DonateIntegration не доступна для выдачи " + rankId);
            return false;
        }
        try {
            int currentWeight = getPlayerDonateWeight(player.getName());
            int newWeight = getRankWeight(rankId);

            plugin.getLogger().info("[Крейт] Игрок " + player.getName()
                    + " текущий вес=" + currentWeight + " новый=" + newWeight + " ранг=" + rankId);

            if (newWeight <= currentWeight) return false;

            Object dm = getDonateManager();
            Method setPlayerRank = dm.getClass().getMethod("setPlayerRank", String.class, String.class);
            setPlayerRank.invoke(dm, player.getName(), rankId);

            // Re-apply permissions
            try {
                Method permService = arisDonatePlugin.getClass().getMethod("getPermissionService");
                Object ps = permService.invoke(arisDonatePlugin);
                Method reapply = ps.getClass().getMethod("applyAll", Player.class);
                reapply.invoke(ps, player);
            } catch (Exception ignored) {}

            plugin.getLogger().info("[Крейт] Донат " + rankId + " выдан игроку " + player.getName());
            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("Ошибка выдачи доната " + rankId + " игроку " + player.getName() + ": " + e);
            e.printStackTrace();
            return false;
        }
    }
}

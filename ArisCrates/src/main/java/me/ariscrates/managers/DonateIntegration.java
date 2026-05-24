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
    private Object donateManager;
    private Method getPlayerRankMethod;
    private Method setPlayerRankMethod;
    private Method getRankMethod;
    private Method weightMethod;
    private Method gradientNameMethod;
    private boolean available;

    public DonateIntegration(ArisCratesPlugin plugin) {
        this.plugin = plugin;
        init();
    }

    private void init() {
        Plugin arisDonate = Bukkit.getPluginManager().getPlugin("ArisDonate");
        if (arisDonate == null || !arisDonate.isEnabled()) {
            plugin.getLogger().warning("ArisDonate не найден — донат-крейты не будут выдавать ранги.");
            available = false;
            return;
        }
        try {
            Method getDM = arisDonate.getClass().getMethod("getDonateManager");
            donateManager = getDM.invoke(arisDonate);
            getPlayerRankMethod = donateManager.getClass().getMethod("getPlayerRank", String.class);
            setPlayerRankMethod = donateManager.getClass().getMethod("setPlayerRank", String.class, String.class);
            getRankMethod = donateManager.getClass().getMethod("getRank", String.class);

            Class<?> rankClass = getRankMethod.getReturnType();
            weightMethod = rankClass.getMethod("weight");
            gradientNameMethod = rankClass.getMethod("gradientName");

            available = true;
            plugin.getLogger().info("ArisDonate интеграция активна.");

            // Re-apply permissions for online players
            Method permService = arisDonate.getClass().getMethod("getPermissionService");
            Object ps = permService.invoke(arisDonate);
            Method reapply = ps.getClass().getMethod("applyAll", Player.class);
            for (Player p : Bukkit.getOnlinePlayers()) {
                reapply.invoke(ps, p);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Ошибка инициализации ArisDonate: " + e.getMessage());
            available = false;
        }
    }

    public boolean isAvailable() { return available; }

    /**
     * Возвращает вес текущего доната игрока (0 если нет).
     */
    public int getPlayerDonateWeight(String nick) {
        if (!available) return 0;
        try {
            Object rank = getPlayerRankMethod.invoke(donateManager, nick);
            if (rank == null) return 0;
            return (int) weightMethod.invoke(rank);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Возвращает вес ранга по ID.
     */
    public int getRankWeight(String rankId) {
        if (!available) return 0;
        try {
            Object rank = getRankMethod.invoke(donateManager, rankId);
            if (rank == null) return 0;
            return (int) weightMethod.invoke(rank);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Возвращает gradient имя ранга для отображения.
     */
    public String getRankGradientName(String rankId) {
        if (!available) return rankId;
        try {
            Object rank = getRankMethod.invoke(donateManager, rankId);
            if (rank == null) return rankId;
            return (String) gradientNameMethod.invoke(rank);
        } catch (Exception e) {
            return rankId;
        }
    }

    /**
     * Выдаёт донат-ранг, если он выше текущего.
     * Возвращает true если ранг был выдан, false если у игрока уже есть выше.
     */
    public boolean giveRankIfHigher(Player player, String rankId) {
        if (!available) return false;
        try {
            int currentWeight = getPlayerDonateWeight(player.getName());
            int newWeight = getRankWeight(rankId);
            if (newWeight <= currentWeight) return false;
            setPlayerRankMethod.invoke(donateManager, player.getName(), rankId);

            // Re-apply permissions
            Plugin arisDonate = Bukkit.getPluginManager().getPlugin("ArisDonate");
            if (arisDonate != null) {
                Method permService = arisDonate.getClass().getMethod("getPermissionService");
                Object ps = permService.invoke(arisDonate);
                Method reapply = ps.getClass().getMethod("applyAll", Player.class);
                reapply.invoke(ps, player);
            }
            return true;
        } catch (Exception e) {
            plugin.getLogger().warning("Ошибка выдачи доната " + rankId + " игроку " + player.getName() + ": " + e.getMessage());
            return false;
        }
    }
}

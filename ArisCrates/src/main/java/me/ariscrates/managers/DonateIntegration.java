package me.ariscrates.managers;

import me.ariscrates.ArisCratesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

/**
 * Интеграция с ArisDonate — выдаёт донат-ранги через прямой вызов DonateManager.
 * Использует рефлексию + консольную команду как fallback.
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
            plugin.getLogger().warning("[DI] getPlayerDonateWeight ошибка: " + e);
            return 0;
        }
    }

    public int getRankWeight(String rankId) {
        if (!available) return 0;
        try {
            Object dm = getDonateManager();
            Method getRank = dm.getClass().getMethod("getRank", String.class);
            Object rank = getRank.invoke(dm, rankId);
            if (rank == null) {
                plugin.getLogger().warning("[DI] Ранг '" + rankId + "' не найден в ArisDonate!");
                return 0;
            }
            Method weight = rank.getClass().getMethod("weight");
            return (int) weight.invoke(rank);
        } catch (Exception e) {
            plugin.getLogger().warning("[DI] getRankWeight ошибка: " + e);
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
            plugin.getLogger().warning("[DI] Не доступна для выдачи " + rankId);
            return false;
        }

        int currentWeight = getPlayerDonateWeight(player.getName());
        int newWeight = getRankWeight(rankId);

        plugin.getLogger().info("[Крейт] " + player.getName()
                + ": текущий=" + currentWeight + " новый=" + newWeight + " ранг=" + rankId);

        if (newWeight <= currentWeight) {
            plugin.getLogger().info("[Крейт] Ранг " + rankId + " ниже/равен — пропуск.");
            return false;
        }

        // Способ 1: прямой вызов через рефлексию
        boolean success = false;
        try {
            Object dm = getDonateManager();
            Method setPlayerRank = dm.getClass().getMethod("setPlayerRank", String.class, String.class);
            setPlayerRank.invoke(dm, player.getName(), rankId);
            success = true;
            plugin.getLogger().info("[Крейт] Ранг " + rankId + " выдан через API: " + player.getName());
        } catch (Exception e) {
            plugin.getLogger().warning("[Крейт] API-вызов не удался: " + e.getMessage());
        }

        // Способ 2 (fallback): выполнить команду от консоли
        if (!success) {
            try {
                String cmd = "arisdonate set " + player.getName() + " " + rankId;
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                success = true;
                plugin.getLogger().info("[Крейт] Ранг " + rankId + " выдан через команду: " + cmd);
            } catch (Exception e2) {
                plugin.getLogger().warning("[Крейт] Команда fallback не удалась: " + e2.getMessage());
            }
        }

        // Обновить пермишены
        if (success) {
            try {
                Method permService = arisDonatePlugin.getClass().getMethod("getPermissionService");
                Object ps = permService.invoke(arisDonatePlugin);
                Method reapply = ps.getClass().getMethod("applyAll", Player.class);
                reapply.invoke(ps, player);
            } catch (Exception ignored) {}

            try {
                Method getCF = arisDonatePlugin.getClass().getMethod("getChatFormatter");
                Object cf = getCF.invoke(arisDonatePlugin);
                Method applyTab = cf.getClass().getMethod("applyTabPrefix", Player.class);
                applyTab.invoke(cf, player);
            } catch (Exception ignored) {}
        }

        return success;
    }
}

package me.arisdonate.managers;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.models.DonateRank;
import me.arisdonate.util.Msg;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Обновляет верх (header) и низ (footer) таб-листа у всех игроков.
 *
 * Содержимое настраивается в config.yml → блок tab:
 *   server-name      — большая строка-заголовок (по умолчанию ArisWorld)
 *   motto            — подзаголовок (по умолчанию Гриферское выживание)
 *   fake-online-min  — нижняя граница фейкового бонуса к онлайну
 *   fake-online-max  — верхняя граница
 *   refresh-ticks    — частота обновления (тики, 20 = 1 сек)
 */
public class TabService {

    private final ArisDonatePlugin plugin;
    private BukkitTask task;
    private int fakeBoost;
    private long lastBoostShuffle;

    public TabService(ArisDonatePlugin plugin) {
        this.plugin = plugin;
        rerollFakeBoost();
    }

    public void start() {
        stop();
        int period = Math.max(20, plugin.getConfig().getInt("tab.refresh-ticks", 60));
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 20L, period);
    }

    public void stop() {
        if (task != null) task.cancel();
        task = null;
    }

    private void tick() {
        // меняем фейковый бонус каждые ~90 сек, чтобы число выглядело живым
        if (System.currentTimeMillis() - lastBoostShuffle > 90_000) rerollFakeBoost();

        int real = Bukkit.getOnlinePlayers().size();
        int displayedOnline = real + fakeBoost;
        int maxSlots = Math.max(Bukkit.getMaxPlayers(), displayedOnline);
        int visited = plugin.getVisitorsManager() == null ? 0 : plugin.getVisitorsManager().total();

        String name  = plugin.getConfig().getString("tab.server-name", "ArisWorld");
        String motto = plugin.getConfig().getString("tab.motto", "Гриферское выживание");

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendPlayerListHeaderAndFooter(buildHeader(name, motto), buildFooter(p, displayedOnline, maxSlots, visited));
        }
    }

    private void rerollFakeBoost() {
        int lo = Math.max(0, plugin.getConfig().getInt("tab.fake-online-min", 0));
        int hi = Math.max(lo, plugin.getConfig().getInt("tab.fake-online-max", 0));
        fakeBoost = lo == hi ? lo : ThreadLocalRandom.current().nextInt(lo, hi + 1);
        lastBoostShuffle = System.currentTimeMillis();
    }

    private int parseHex(String hex, int fallback) {
        if (hex == null) return fallback;
        String h = hex.startsWith("#") ? hex.substring(1) : hex;
        try { return Integer.parseInt(h, 16); } catch (Exception e) { return fallback; }
    }

    private Component buildHeader(String name, String motto) {
        Component title = Msg.gradient(name, 0xFFD700, 0xFF6A00);
        Component sub   = Component.text(motto, TextColor.color(0xCCCCCC));

        return Component.text("\n")
                .append(Component.text("  "))
                .append(title.decoration(TextDecoration.BOLD, true))
                .append(Component.text("  \n"))
                .append(Component.text("  "))
                .append(sub)
                .append(Component.text("  \n"));
    }

    private Component buildFooter(Player viewer, int online, int max, int visited) {
        me.arisdonate.models.StaffRank staff = plugin.getStaffManager() == null
                ? null
                : plugin.getStaffManager().getPlayerRank(viewer.getName());
        DonateRank rank = plugin.getDonateManager() == null
                ? null
                : plugin.getDonateManager().getPlayerRank(viewer.getName());

        Component rankLine;
        if (staff != null) {
            int from = parseHex(staff.startHex(), 0xFFFFFF);
            int to   = parseHex(staff.endHex(),   0xFFFFFF);
            rankLine = Msg.gradient(staff.displayName(), from, to)
                    .decoration(TextDecoration.BOLD, true);
        } else if (rank != null) {
            int from = parseHex(rank.startHex(), 0xFFFFFF);
            int to   = parseHex(rank.endHex(),   0xFFFFFF);
            rankLine = Msg.gradient(rank.displayName(), from, to)
                    .decoration(TextDecoration.BOLD, true);
        } else {
            rankLine = Component.text("Игрок", TextColor.color(0xAAAAAA));
        }

        long pingMs = viewer.getPing();
        TextColor pingColor =
                  pingMs <  80 ? TextColor.color(0x55FF55)
                : pingMs < 150 ? TextColor.color(0xFFCC55)
                :                 TextColor.color(0xFF5555);

        return Component.text("\n")
                .append(Component.text("  Онлайн: ", TextColor.color(0x888888)))
                .append(Component.text(online + "/" + max, TextColor.color(0x55FF55))
                        .decoration(TextDecoration.BOLD, true))
                .append(Component.text("   ✦   ", TextColor.color(0x444444)))
                .append(Component.text("Посетили: ", TextColor.color(0x888888)))
                .append(Component.text(String.valueOf(visited), TextColor.color(0xFFCC55))
                        .decoration(TextDecoration.BOLD, true))
                .append(Component.text("  \n"))
                .append(Component.text("  Ваш ранг: ", TextColor.color(0x888888)))
                .append(rankLine)
                .append(Component.text("   ✦   ", TextColor.color(0x444444)))
                .append(Component.text("Ping: ", TextColor.color(0x888888)))
                .append(Component.text(pingMs + " ms", pingColor))
                .append(Component.text("  \n"));
    }
}

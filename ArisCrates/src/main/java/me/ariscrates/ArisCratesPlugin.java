package me.ariscrates;

import me.ariscrates.commands.CrateCommand;
import me.ariscrates.commands.FreeCommand;
import me.ariscrates.commands.KeyCommand;
import me.ariscrates.gui.CrateAnimationGui;
import me.ariscrates.gui.CrateOpenGui;
import me.ariscrates.gui.CratePreviewGui;
import me.ariscrates.managers.CrateLocationManager;
import me.ariscrates.managers.CrateManager;
import me.ariscrates.managers.DonateIntegration;
import me.ariscrates.managers.HologramManager;
import me.ariscrates.managers.Msg;
import me.ariscrates.models.Crate;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArisCratesPlugin extends JavaPlugin implements Listener {

    private CrateManager crateManager;
    private CrateLocationManager locationManager;
    private CrateAnimationGui animationGui;
    private CratePreviewGui previewGui;
    private CrateOpenGui openGui;
    private HologramManager hologramManager;
    private DonateIntegration donateIntegration;
    private final Map<UUID, String> openCrateGui = new HashMap<>();
    private final Map<UUID, Location> openCrateLoc = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        crateManager = new CrateManager(this);
        locationManager = new CrateLocationManager(this);
        animationGui = new CrateAnimationGui(this);
        previewGui = new CratePreviewGui(this);
        openGui = new CrateOpenGui(this);
        hologramManager = new HologramManager(this);

        PluginCommand crateCmd = getCommand("crate");
        CrateCommand cc = new CrateCommand(this);
        if (crateCmd != null) { crateCmd.setExecutor(cc); crateCmd.setTabCompleter(cc); }

        PluginCommand keyCmd = getCommand("key");
        KeyCommand kc = new KeyCommand(this);
        if (keyCmd != null) { keyCmd.setExecutor(kc); keyCmd.setTabCompleter(kc); }

        PluginCommand freeCmd = getCommand("free");
        if (freeCmd != null) freeCmd.setExecutor(new FreeCommand(this));

        getServer().getPluginManager().registerEvents(this, this);

        // Spawn holograms after 1 tick (worlds loaded)
        getServer().getScheduler().runTaskLater(this, () -> {
            hologramManager.spawnAll();
            donateIntegration = new DonateIntegration(this);
        }, 20L);

        getLogger().info("ArisCrates v" + getDescription().getVersion() + " включён.");
    }

    @Override
    public void onDisable() {
        if (hologramManager != null) hologramManager.removeAll();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = e.getClickedBlock();
        if (block == null) return;
        String crateId = locationManager.getCrateAt(block.getLocation());
        if (crateId == null) return;

        e.setCancelled(true);
        Player p = e.getPlayer();
        Crate crate = crateManager.getCrate(crateId);
        if (crate == null) { p.sendMessage(Msg.parse("&cКрейт &e" + crateId + " &cне найден.")); return; }

        openCrateGui.put(p.getUniqueId(), crate.id());
        openCrateLoc.put(p.getUniqueId(), block.getLocation());
        openGui.open(p, crate);
        p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 0.7f, 1.0f);
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        String title = PlainTextComponentSerializer.plainText().serialize(e.getView().title());

        if (title.contains(CrateAnimationGui.TITLE_PREFIX)) {
            e.setCancelled(true);
            return;
        }

        if (title.contains(CratePreviewGui.TITLE_PREFIX)) {
            e.setCancelled(true);
            return;
        }

        if (title.contains(CrateOpenGui.TITLE_PREFIX)) {
            e.setCancelled(true);
            int slot = e.getRawSlot();
            String crateId = openCrateGui.get(p.getUniqueId());
            if (crateId == null) return;
            Crate crate = crateManager.getCrate(crateId);
            if (crate == null) return;

            if (slot == 49) {
                int keys = crateManager.countKeys(p, crate.id());
                if (keys <= 0) {
                    p.sendMessage(Msg.parse("&cУ вас нет ключей от этого крейта!"));
                    p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.7f, 1.0f);
                    return;
                }
                crateManager.consumeKey(p, crate.id());
                Location loc = openCrateLoc.remove(p.getUniqueId());
                openCrateGui.remove(p.getUniqueId());
                p.closeInventory();
                // Небольшая задержка чтобы GUI закрылся перед мировой анимацией
                Bukkit.getScheduler().runTaskLater(ArisCratesPlugin.this, () -> animationGui.play(p, crate, loc), 5L);
            } else if (slot == 46) {
                p.closeInventory();
                openCrateGui.remove(p.getUniqueId());
                openCrateLoc.remove(p.getUniqueId());
                previewGui.open(p, crate);
            } else if (slot == 50) {
                p.closeInventory();
                openCrateGui.remove(p.getUniqueId());
                openCrateLoc.remove(p.getUniqueId());
            }
        }
    }

    public CrateManager getCrateManager() { return crateManager; }
    public CrateLocationManager getLocationManager() { return locationManager; }
    public CrateAnimationGui getAnimationGui() { return animationGui; }
    public CratePreviewGui getPreviewGui() { return previewGui; }
    public CrateOpenGui getOpenGui() { return openGui; }
    public HologramManager getHologramManager() { return hologramManager; }
    public DonateIntegration getDonateIntegration() { return donateIntegration; }
}

package me.arispets;

import me.arispets.commands.PetAdminCommand;
import me.arispets.commands.PetCommand;
import me.arispets.listeners.PetListener;
import me.arispets.managers.PetManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class ArisPetsPlugin extends JavaPlugin {

    private PetManager petManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        petManager = new PetManager(this);

        PluginCommand petCmd = getCommand("pet");
        PetCommand pc = new PetCommand(this);
        if (petCmd != null) { petCmd.setExecutor(pc); petCmd.setTabCompleter(pc); }

        PluginCommand adminCmd = getCommand("petadmin");
        PetAdminCommand pac = new PetAdminCommand(this);
        if (adminCmd != null) { adminCmd.setExecutor(pac); adminCmd.setTabCompleter(pac); }

        getServer().getPluginManager().registerEvents(new PetListener(this), this);

        // Follow tick every 5 ticks
        getServer().getScheduler().runTaskTimer(this, petManager::tick, 20L, 5L);

        getLogger().info("ArisPets v" + getDescription().getVersion() + " включён.");
    }

    @Override
    public void onDisable() {
        if (petManager != null) petManager.despawnAll();
    }

    public PetManager getPetManager() { return petManager; }
}

package me.arisdonate;

import me.arisdonate.commands.*;
import me.arisdonate.commands.admin.*;
import me.arisdonate.commands.donate.*;
import me.arisdonate.commands.home.*;
import me.arisdonate.commands.staff.*;
import me.arisdonate.commands.teleport.*;
import me.arisdonate.commands.util.*;
import me.arisdonate.commands.warp.*;
import me.arisdonate.gui.DonateGui;
import me.arisdonate.gui.KitsGui;
import me.arisdonate.gui.SphereShopGui;
import me.arisdonate.listeners.*;
import me.arisdonate.managers.*;
import me.arisdonate.util.ChatFormatter;
import org.bukkit.NamespacedKey;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class ArisDonatePlugin extends JavaPlugin {

    private DonateManager donateManager;
    private HomeManager homeManager;
    private WarpManager warpManager;
    private SpawnManager spawnManager;
    private TeleportManager teleportManager;
    private VanishManager vanishManager;
    private BackManager backManager;
    private AfkManager afkManager;
    private MuteManager muteManager;
    private FreezeManager freezeManager;
    private JailManager jailManager;
    private KitManager kitManager;
    private MessageManager messageManager;
    private ChatFormatter chatFormatter;
    private DonateGui donateGui;
    private KitsGui kitsGui;
    private SphereShopGui sphereShopGui;
    private SphereManager sphereManager;
    private EconomyManager economyManager;
    private PermissionService permissionService;
    private VisitorsManager visitorsManager;
    private TabService tabService;
    private StaffManager staffManager;

    private NamespacedKey keyDonateRank;
    private NamespacedKey keyKitId;
    private NamespacedKey keySphereId;

    private static final int CONFIG_VERSION = 3;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        migrateConfigIfNeeded();

        keyDonateRank = new NamespacedKey(this, "donate_rank");
        keyKitId = new NamespacedKey(this, "kit_id");
        keySphereId = new NamespacedKey(this, "sphere_id");

        // SphereManager должен инициализироваться ДО KitManager,
        // потому что киты могут ссылаться на сферы через { sphere: <id> }.
        sphereManager = new SphereManager(this);
        economyManager = new EconomyManager(this);

        donateManager = new DonateManager(this);
        homeManager = new HomeManager(this);
        warpManager = new WarpManager(this);
        spawnManager = new SpawnManager(this);
        teleportManager = new TeleportManager();
        vanishManager = new VanishManager(this);
        backManager = new BackManager();
        afkManager = new AfkManager();
        muteManager = new MuteManager(this);
        freezeManager = new FreezeManager();
        jailManager = new JailManager(this);
        kitManager = new KitManager(this);
        messageManager = new MessageManager();
        chatFormatter = new ChatFormatter(this);
        donateGui = new DonateGui(this);
        kitsGui = new KitsGui(this);
        sphereShopGui = new SphereShopGui(this);
        permissionService = new PermissionService(this);
        permissionService.refreshAll();
        visitorsManager = new VisitorsManager(this);
        staffManager = new StaffManager(this);
        if (getConfig().getBoolean("tab.enabled", true)) {
            tabService = new TabService(this);
            tabService.start();
        }

        // Listeners
        getServer().getPluginManager().registerEvents(new ChatFormatListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new DonateGuiListener(this), this);
        getServer().getPluginManager().registerEvents(new KitsGuiListener(this), this);
        getServer().getPluginManager().registerEvents(new SphereShopListener(this), this);
        getServer().getPluginManager().registerEvents(new FreezeMoveListener(this), this);
        getServer().getPluginManager().registerEvents(new CommandSpyListener(this), this);

        // Donate
        bind("donate", new DonateCommand(this));
        bind("arisdonate", new ArisDonateAdminCommand(this));
        bind("arisstaff", new ArisStaffAdminCommand(this));

        // Home
        bind("sethome", new SetHomeCommand(this));
        bind("home", new HomeCommand(this));
        bind("delhome", new DelHomeCommand(this));
        bind("renamehome", new RenameHomeCommand(this));
        bind("homes", new HomesListCommand(this));

        // Warp
        bind("setwarp", new SetWarpCommand(this));
        bind("warp", new WarpCommand(this));
        bind("delwarp", new DelWarpCommand(this));
        bind("warps", new WarpsListCommand(this));

        // Spawn
        bind("spawn", new SpawnCommand(this));
        bind("setspawn", new SetSpawnCommand(this));

        // Teleport
        TeleportCommand tp = new TeleportCommand(this);
        bind("tp", tp);
        bind("tphere", new TpHereCommand(this));
        bind("tpa", new TpaCommand(this));
        bind("tpahere", new TpaHereCommand(this));
        bind("tpaccept", new TpAcceptCommand(this));
        bind("tpadeny", new TpDenyCommand(this));
        bind("tpcancel", new TpCancelCommand(this));
        bind("tppos", new TpPosCommand(this));
        bind("tpall", new TpAllCommand(this));
        bind("back", new BackCommand(this));

        // Messages
        MsgCommand msg = new MsgCommand(this);
        bind("msg", msg);
        bind("tell", msg);
        bind("w", msg);
        bind("pm", msg);
        bind("r", new ReplyCommand(this));
        bind("reply", new ReplyCommand(this));
        bind("socialspy", new SocialSpyCommand(this));
        bind("me", new MeActionCommand(this));
        bind("broadcast", new BroadcastCommand(this));
        bind("afk", new AfkCommand(this));

        // Donate-only / Mod
        bind("vanish", new VanishCommand(this));
        bind("gm", new GamemodeCommand(this));
        bind("gmc", new GamemodeCommand(this));
        bind("gms", new GamemodeCommand(this));
        bind("gma", new GamemodeCommand(this));
        bind("gmsp", new GamemodeCommand(this));
        bind("fly", new FlyCommand(this));
        bind("flyspeed", new SpeedCommand(this));
        bind("walkspeed", new SpeedCommand(this));
        bind("speed", new SpeedCommand(this));

        // Admin moderation
        bind("ban", new BanCommand(this));
        bind("tempban", new BanCommand(this));
        bind("unban", new UnbanCommand(this));
        bind("kick", new KickCommand(this));
        bind("mute", new MuteCommand(this));
        bind("tempmute", new MuteCommand(this));
        bind("unmute", new UnmuteCommand(this));
        bind("freeze", new FreezeCommand(this));
        bind("jail", new JailCommand(this));
        bind("unjail", new UnjailCommand(this));
        bind("setjail", new SetJailCommand(this));
        bind("warn", new WarnCommand(this));

        // Inventory
        bind("inv", new InvSeeCommand(this));
        bind("invsee", new InvSeeCommand(this));
        bind("ec", new EnderChestCommand(this));
        bind("enderchest", new EnderChestCommand(this));
        bind("clearinv", new ClearInvCommand(this));

        // Heal / status
        bind("heal", new HealCommand(this));
        bind("feed", new FeedCommand(this));
        bind("god", new GodCommand(this));
        bind("cure", new CureCommand(this));

        // World
        bind("time", new TimeCommand(this));
        bind("day", new TimeShortcutCommand(this, "day"));
        bind("night", new TimeShortcutCommand(this, "night"));
        bind("weather", new WeatherCommand(this));
        bind("sun", new WeatherShortcutCommand(this, "sun"));
        bind("rain", new WeatherShortcutCommand(this, "rain"));
        bind("thunder", new WeatherShortcutCommand(this, "thunder"));
        bind("worldtp", new WorldTpCommand(this));

        // Player info
        bind("seen", new SeenCommand(this));
        bind("near", new NearCommand(this));
        bind("playerlist", new PlayerListCommand(this));
        bind("tps", new TpsCommand(this));
        bind("ping", new PingCommand(this));
        bind("whois", new WhoIsCommand(this));

        // Quality of life
        bind("top", new TopCommand(this));
        bind("jump", new JumpCommand(this));
        bind("repair", new RepairCommand(this));
        bind("craft", new CraftCommand(this));
        bind("anvil", new ContainerCommand(this, "anvil"));
        bind("grindstone", new ContainerCommand(this, "grindstone"));
        bind("loom", new ContainerCommand(this, "loom"));
        bind("smithing", new ContainerCommand(this, "smithing"));
        bind("cartography", new ContainerCommand(this, "cartography"));
        bind("workbench", new CraftCommand(this));

        // Items
        bind("give", new GiveItemCommand(this));
        bind("i", new ItemMeCommand(this));
        bind("more", new MoreCommand(this));
        bind("skull", new SkullCommand(this));
        bind("hat", new HatCommand(this));
        bind("smite", new SmiteCommand(this));
        bind("burn", new BurnCommand(this));
        bind("extinguish", new ExtinguishCommand(this));
        bind("lightning", new SmiteCommand(this));
        bind("clearchat", new ClearChatCommand(this));
        bind("nick", new NickCommand(this));
        bind("unnick", new UnnickCommand(this));
        bind("realname", new RealnameCommand(this));
        bind("condense", new CondenseCommand(this));
        bind("sudo", new SudoCommand(this));
        bind("commandspy", new CommandSpyCommand(this));
        bind("effect", new EffectCommand(this));
        bind("kit", new KitCommand(this));
        bind("kits", new KitListCommand(this));
        bind("check", new CheckCommand(this));
        bind("c", new ClearChatCommand(this));
        // /shop теперь регистрируется только в RegionBlocks (общий магазин);
        // ArisDonate сохраняет только /donate и /sphere как прямые входы.
        bind("sphere", new SphereCommand(this));
        bind("spheres", new SphereCommand(this));
        bind("rules", new SimpleTextCommand(this, "rules"));
        bind("motd", new SimpleTextCommand(this, "motd"));
        bind("help-aris", new HelpArisCommand(this));
        bind("adreload", new ReloadCommand(this));

        getLogger().info("ArisDonate v" + getDescription().getVersion() + " включён. Команд зарегистрировано: см. plugin.yml.");
    }

    @Override
    public void onDisable() {
        if (donateManager != null) donateManager.save();
        if (homeManager   != null) homeManager.save();
        if (warpManager   != null) warpManager.save();
        if (spawnManager  != null) spawnManager.save();
        if (muteManager   != null) muteManager.save();
        if (jailManager   != null) jailManager.save();
        if (kitManager    != null) kitManager.saveCooldowns();
        if (sphereManager != null) sphereManager.stop();
        if (economyManager != null) economyManager.save();
        if (tabService     != null) tabService.stop();
        getLogger().info("ArisDonate выключен, данные сохранены.");
    }

    /**
     * Если на диске лежит config.yml старой версии (например, от первой
     * сборки плагина), переименовываем его в config-backup-vN.yml и
     * распаковываем свежий встроенный config.yml. Player-данные хранятся
     * отдельно (players.yml, kit_cooldowns.yml, homes.yml и т.д.), поэтому
     * прогресс не теряется.
     */
    private void migrateConfigIfNeeded() {
        int diskVersion = getConfig().getInt("config-version", 1);
        if (diskVersion >= CONFIG_VERSION) return;
        java.io.File cfgFile = new java.io.File(getDataFolder(), "config.yml");
        java.io.File backup = new java.io.File(getDataFolder(),
                "config-backup-v" + diskVersion + "-" + System.currentTimeMillis() + ".yml");
        if (cfgFile.exists()) {
            if (cfgFile.renameTo(backup)) {
                getLogger().warning("Старый config.yml (v" + diskVersion
                        + ") сохранён как " + backup.getName());
            } else {
                getLogger().warning("Не смог переименовать старый config.yml, перезапишу.");
                //noinspection ResultOfMethodCallIgnored
                cfgFile.delete();
            }
        }
        saveResource("config.yml", true);
        reloadConfig();
        getLogger().info("config.yml обновлён до v" + CONFIG_VERSION
                + ". Игровые данные (players.yml и т.д.) не затронуты.");
    }

    private void bind(String name, org.bukkit.command.CommandExecutor exec) {
        PluginCommand cmd = getCommand(name);
        if (cmd == null) {
            getLogger().warning("Команда '" + name + "' не объявлена в plugin.yml!");
            return;
        }
        cmd.setExecutor(exec);
        if (exec instanceof org.bukkit.command.TabCompleter tc) {
            cmd.setTabCompleter(tc);
        }
    }

    public NamespacedKey keyDonateRank() { return keyDonateRank; }
    public NamespacedKey keyKitId()      { return keyKitId; }
    public NamespacedKey keySphereId()   { return keySphereId; }
    public KitsGui getKitsGui()          { return kitsGui; }
    public DonateGui getDonateGui()      { return donateGui; }
    public SphereShopGui getSphereShopGui() { return sphereShopGui; }
    public SphereManager getSphereManager() { return sphereManager; }
    public EconomyManager getEconomyManager() { return economyManager; }
    public DonateManager getDonateManager()       { return donateManager; }
    public HomeManager getHomeManager()           { return homeManager; }
    public WarpManager getWarpManager()           { return warpManager; }
    public SpawnManager getSpawnManager()         { return spawnManager; }
    public TeleportManager getTeleportManager()   { return teleportManager; }
    public VanishManager getVanishManager()       { return vanishManager; }
    public BackManager getBackManager()           { return backManager; }
    public AfkManager getAfkManager()             { return afkManager; }
    public MuteManager getMuteManager()           { return muteManager; }
    public FreezeManager getFreezeManager()       { return freezeManager; }
    public JailManager getJailManager()           { return jailManager; }
    public KitManager getKitManager()             { return kitManager; }
    public MessageManager getMessageManager()     { return messageManager; }
    public ChatFormatter getChatFormatter()       { return chatFormatter; }
    public PermissionService getPermissionService() { return permissionService; }
    public VisitorsManager getVisitorsManager()   { return visitorsManager; }
    public TabService getTabService()             { return tabService; }
    public StaffManager getStaffManager()         { return staffManager; }
}

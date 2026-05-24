package me.arischat;

import me.arischat.commands.*;
import me.arischat.listeners.ChatListener;
import me.arischat.managers.ChatManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class ArisChatPlugin extends JavaPlugin {

    private ChatManager chatManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        chatManager = new ChatManager(this);

        reg("msg", new MsgCommand(this));
        reg("reply", new ReplyCommand(this));
        reg("ignore", new IgnoreCommand(this));
        reg("chatmode", new ChatModeCommand(this));
        reg("broadcast", new BroadcastCommand(this));
        reg("spy", new SpyCommand(this));

        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getLogger().info("ArisChat v" + getDescription().getVersion() + " включён.");
    }

    private void reg(String name, Object handler) {
        PluginCommand cmd = getCommand(name);
        if (cmd == null) return;
        if (handler instanceof org.bukkit.command.CommandExecutor ce) cmd.setExecutor(ce);
        if (handler instanceof org.bukkit.command.TabCompleter tc) cmd.setTabCompleter(tc);
    }

    public ChatManager getChatManager() { return chatManager; }
}

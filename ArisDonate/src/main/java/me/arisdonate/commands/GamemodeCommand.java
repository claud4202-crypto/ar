package me.arisdonate.commands;

import me.arisdonate.ArisDonatePlugin;
import me.arisdonate.util.BaseCommand;
import me.arisdonate.util.Msg;
import me.arisdonate.util.Players;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GamemodeCommand extends BaseCommand {
    public GamemodeCommand(ArisDonatePlugin plugin) { super(plugin); }

    @Override
    protected void execute(CommandSender sender, Command command, String label, String[] args) {
        if (!check(sender, "arisdonate.gamemode")) return;
        GameMode mode = null;
        Player target = sender instanceof Player ? (Player) sender : null;

        // shortcut alias
        switch (label.toLowerCase()) {
            case "gmc": mode = GameMode.CREATIVE; break;
            case "gms": mode = GameMode.SURVIVAL; break;
            case "gma": mode = GameMode.ADVENTURE; break;
            case "gmsp": mode = GameMode.SPECTATOR; break;
        }

        if (mode == null && args.length >= 1) {
            mode = parseMode(args[0]);
        }
        if (mode == null) {
            sender.sendMessage(Msg.parse("&7Использование: &e/gm <0|1|2|3|survival|creative|adventure|spectator> [ник]"));
            return;
        }
        if (label.toLowerCase().startsWith("gm") && label.length() > 2 && args.length >= 1) {
            // /gmc <ник>
            Player t = Players.online(args[0]);
            if (t != null) target = t;
        } else if (args.length >= 2) {
            Player t = Players.online(args[1]);
            if (t != null) target = t;
        }
        if (target == null) { sender.sendMessage(Msg.parse("&cУкажите игрока.")); return; }
        target.setGameMode(mode);
        target.sendMessage(Msg.parse("&aРежим: &e" + mode.name().toLowerCase()));
        if (!target.equals(sender)) sender.sendMessage(Msg.parse("&aУ &e" + target.getName() + " &aрежим: &e" + mode.name().toLowerCase()));
    }

    private GameMode parseMode(String s) {
        switch (s.toLowerCase()) {
            case "0": case "s": case "survival": return GameMode.SURVIVAL;
            case "1": case "c": case "creative": return GameMode.CREATIVE;
            case "2": case "a": case "adventure": return GameMode.ADVENTURE;
            case "3": case "sp": case "spectator": return GameMode.SPECTATOR;
            default: return null;
        }
    }
}

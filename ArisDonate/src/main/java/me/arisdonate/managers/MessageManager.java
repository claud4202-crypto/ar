package me.arisdonate.managers;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Хранит последнего собеседника для /r, и socialspy-список. */
public class MessageManager {

    private final Map<UUID, UUID> lastReplyTo = new HashMap<>();
    private final Set<UUID> socialSpy = new HashSet<>();

    public void setLast(CommandSender from, CommandSender to) {
        if (from instanceof Player pf && to instanceof Player pt) {
            lastReplyTo.put(pf.getUniqueId(), pt.getUniqueId());
            lastReplyTo.put(pt.getUniqueId(), pf.getUniqueId());
        }
    }

    public UUID getLast(UUID who) { return lastReplyTo.get(who); }

    public void toggleSocialSpy(UUID who) {
        if (!socialSpy.add(who)) socialSpy.remove(who);
    }

    public boolean inSocialSpy(UUID who) { return socialSpy.contains(who); }
    public Set<UUID> socialSpyMembers()  { return socialSpy; }
}

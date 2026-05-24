package me.arisdonate.managers;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Хранение запросов /tpa и /tpahere. */
public class TeleportManager {

    public enum Type { TPA, TPAHERE }

    public static final class Request {
        public final UUID requester;
        public final Type type;
        public final long timestamp;
        public Request(UUID r, Type t) {
            this.requester = r;
            this.type = t;
            this.timestamp = System.currentTimeMillis();
        }
    }

    /** target -> requester request */
    private final Map<UUID, Request> requests = new HashMap<>();

    public void request(Player from, Player target, Type type) {
        requests.put(target.getUniqueId(), new Request(from.getUniqueId(), type));
    }

    public Request getIncoming(UUID target) {
        Request r = requests.get(target);
        if (r != null && System.currentTimeMillis() - r.timestamp > 120_000L) {
            requests.remove(target);
            return null;
        }
        return r;
    }

    public Request takeIncoming(UUID target) {
        Request r = getIncoming(target);
        if (r != null) requests.remove(target);
        return r;
    }
}

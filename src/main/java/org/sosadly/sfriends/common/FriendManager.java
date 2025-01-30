package org.sosadly.sfriends.common;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;

import org.sosadly.sfriends.network.PacketFriendListSync;
import org.sosadly.sfriends.network.SfriendsNetwork;

import java.util.*;

public class FriendManager {
    private static Map<UUID, Set<UUID>> FRIENDS = new HashMap<>();
    private static final Map<UUID, UUID> PENDING_REQUESTS = new HashMap<>();
    private static FriendSaveData data;

    public static void loadFromDisk() {
        var server = ServerLifecycleHooks.getCurrentServer();
        data = server.overworld().getDataStorage().computeIfAbsent(
            FriendSaveData::load, FriendSaveData::new, "friends"
        );
        FRIENDS = new HashMap<>();
        for (var entry : data.getFriendsMap().entrySet()) {
            UUID owner = UUID.fromString(entry.getKey());
            Set<UUID> set = new HashSet<>();
            for (String s : entry.getValue()) {
                set.add(UUID.fromString(s));
            }
            FRIENDS.put(owner, set);
        }
    }

    private static void saveToDisk() {
        var map = new HashMap<String, Set<String>>();
        for (var e : FRIENDS.entrySet()) {
            String key = e.getKey().toString();
            Set<String> set = new HashSet<>();
            for (UUID u : e.getValue()) {
                set.add(u.toString());
            }
            map.put(key, set);
        }
        data.getFriendsMap().clear();
        data.getFriendsMap().putAll(map);
        data.setDirty();
    }

    public static void tryAddFriend(ServerPlayer from, ServerPlayer to) {
        UUID fromId = from.getUUID();
        UUID toId = to.getUUID();

        if (PENDING_REQUESTS.containsKey(toId) && PENDING_REQUESTS.get(toId).equals(fromId)) {
            addFriends(from, to);
            PENDING_REQUESTS.remove(toId);
        } else {
            PENDING_REQUESTS.put(fromId, toId);
        }
    }

    private static void addFriends(ServerPlayer p1, ServerPlayer p2) {
        UUID id1 = p1.getUUID();
        UUID id2 = p2.getUUID();
        getFriends(id1).add(id2);
        getFriends(id2).add(id1);

        saveToDisk();
        syncFriends(p1);
        syncFriends(p2);
    }

    public static Set<UUID> getFriends(UUID playerId) {
        return FRIENDS.computeIfAbsent(playerId, k -> new HashSet<>());
    }

    public static boolean areFriends(UUID player1, UUID player2) {
        return getFriends(player1).contains(player2);
    }

    private static void syncFriends(ServerPlayer player) {
        Set<UUID> friends = getFriends(player.getUUID());
        SfriendsNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new PacketFriendListSync(friends));
    }

    public static void removeFriends(UUID player1, UUID player2) {
        getFriends(player1).remove(player2);
        getFriends(player2).remove(player1);
        saveToDisk();
        ServerPlayer sp1 = getSP(player1);
        ServerPlayer sp2 = getSP(player2);
        if (sp1 != null) syncFriends(sp1);
        if (sp2 != null) syncFriends(sp2);
    }

    private static ServerPlayer getSP(UUID uuid) {
        if (uuid == null) return null;
        return ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(uuid);
    }
}


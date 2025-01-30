package org.sosadly.sfriends.network;

import java.util.Set;
import java.util.HashSet;
import java.util.UUID;

public class ClientFriendManager {
    private static final Set<UUID> CLIENT_FRIENDS = new HashSet<>();

    public static boolean isFriend(UUID uuid) {
        return CLIENT_FRIENDS.contains(uuid);
    }

    public static void setFriends(Set<UUID> newFriends) {
        CLIENT_FRIENDS.clear();
        CLIENT_FRIENDS.addAll(newFriends);
    }
}

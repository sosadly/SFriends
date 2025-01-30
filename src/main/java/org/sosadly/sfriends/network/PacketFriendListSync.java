package org.sosadly.sfriends.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class PacketFriendListSync {
    private final Set<UUID> friends;

    public PacketFriendListSync(Set<UUID> friends) {
        this.friends = friends;
    }

    public static void encode(PacketFriendListSync msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.friends.size());
        for (UUID friend : msg.friends) {
            buf.writeUUID(friend);
        }
    }

    public static PacketFriendListSync decode(FriendlyByteBuf buf) {
        int size = buf.readInt();
        Set<UUID> friends = new HashSet<>();
        for (int i = 0; i < size; i++) {
            friends.add(buf.readUUID());
        }
        return new PacketFriendListSync(friends);
    }

    public static void handle(PacketFriendListSync msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientFriendManager.setFriends(msg.friends);
        });
        ctx.get().setPacketHandled(true);
    }

}

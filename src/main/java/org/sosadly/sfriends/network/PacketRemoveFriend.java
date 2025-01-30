package org.sosadly.sfriends.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

import org.sosadly.sfriends.common.FriendManager;

public class PacketRemoveFriend {
    private final UUID friendUUID;

    public PacketRemoveFriend(UUID friendUUID) {
        this.friendUUID = friendUUID;
    }

    public static void encode(PacketRemoveFriend msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.friendUUID);
    }

    public static PacketRemoveFriend decode(FriendlyByteBuf buf) {
        return new PacketRemoveFriend(buf.readUUID());
    }

    public static void handle(PacketRemoveFriend msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            if (sender == null) return;
            FriendManager.removeFriends(sender.getUUID(), msg.friendUUID);
        });
        ctx.get().setPacketHandled(true);
    }
}

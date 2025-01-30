package org.sosadly.sfriends.network;

import org.sosadly.sfriends.common.FriendManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class PacketTryAddFriend {
    private final UUID targetPlayerUuid;

    public PacketTryAddFriend(UUID targetPlayerUuid) {
        this.targetPlayerUuid = targetPlayerUuid;
    }

    public static void encode(PacketTryAddFriend msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.targetPlayerUuid);
    }

    public static PacketTryAddFriend decode(FriendlyByteBuf buf) {
        return new PacketTryAddFriend(buf.readUUID());
    }

    public static void handle(PacketTryAddFriend msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            if (sender == null) return;

            ServerPlayer target = sender.getServer().getPlayerList().getPlayer(msg.targetPlayerUuid);
            if (target == null) return;

            double distance = sender.distanceTo(target);
            if (distance <= 3.0D) {
                FriendManager.tryAddFriend(sender, target);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}


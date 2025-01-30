package org.sosadly.sfriends.network;

import org.sosadly.sfriends.Sfriends;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class SfriendsNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(Sfriends.MOD_ID, "network"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        CHANNEL.registerMessage(
            packetId++,
            PacketTryAddFriend.class,
            PacketTryAddFriend::encode,
            PacketTryAddFriend::decode,
            PacketTryAddFriend::handle
        );

        CHANNEL.registerMessage(
            packetId++,
            PacketFriendListSync.class,
            PacketFriendListSync::encode,
            PacketFriendListSync::decode,
            PacketFriendListSync::handle
        );
    }
}

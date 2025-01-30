package org.sosadly.sfriends.client;

import net.minecraftforge.fml.common.Mod;

import org.lwjgl.glfw.GLFW;
import org.sosadly.sfriends.network.SfriendsNetwork;
import org.sosadly.sfriends.network.PacketTryAddFriend;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class KeyBindings {
    public static KeyMapping KEY_ADD_FRIEND;

    public static void register(RegisterKeyMappingsEvent event) {
        KEY_ADD_FRIEND = new KeyMapping(
                "key.sfriends.add_friend",
                GLFW.GLFW_KEY_F,
                "key.categories.sfriends"
        );

        event.register(KEY_ADD_FRIEND);
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if(KEY_ADD_FRIEND.isDown()) {
            LocalPlayer player = Minecraft.getInstance().player;
            if(player == null) return;
            
            var mc = Minecraft.getInstance();
            if(mc.crosshairPickEntity instanceof LocalPlayer) return;
            if (mc.crosshairPickEntity instanceof net.minecraft.world.entity.player.Player targetPlayer) {
                double distance = player.distanceTo(targetPlayer);
                if (distance <= 3.0D) {
                    SfriendsNetwork.CHANNEL.sendToServer(new PacketTryAddFriend(targetPlayer.getUUID()));
                }
            }

        }
    }
    
}

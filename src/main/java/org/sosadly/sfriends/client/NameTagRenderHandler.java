package org.sosadly.sfriends.client;

import org.sosadly.sfriends.network.ClientFriendManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class NameTagRenderHandler {
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRenderNameTag(RenderNameTagEvent event) {
        if (!(event.getEntity() instanceof Player targetPlayer)) return;

        event.setResult(net.minecraftforge.eventbus.api.Event.Result.DENY);

        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer == null) return;

        if (targetPlayer.getUUID().equals(localPlayer.getUUID())) {
            return;
        }

        boolean friends = ClientFriendManager.isFriend(targetPlayer.getUUID());
        double distance = localPlayer.distanceTo(targetPlayer);

        if (friends) {
            if (distance <= 75.0D) {
                String greenName = "\u00A7a" + targetPlayer.getName().getString() + "\u00A7r";
                event.setContent(Component.literal(greenName));
                event.setResult(net.minecraftforge.eventbus.api.Event.Result.ALLOW);
            }
        } else if (distance <= 3.0D && Minecraft.getInstance().crosshairPickEntity == targetPlayer) {
            String keyName = KeyBindings.KEY_ADD_FRIEND.getTranslatedKeyMessage().getString();
            String addFriendText = I18n.get("label.sfriends.add_friend");

            String result = "\u00A7e" 
                + targetPlayer.getName().getString() 
                + " [" + keyName + "] " 
                + addFriendText 
                + "\u00A7r";

            event.setContent(Component.literal(result));
            event.setResult(net.minecraftforge.eventbus.api.Event.Result.ALLOW);
        }
    }
}

package org.sosadly.sfriends;

import org.sosadly.sfriends.client.KeyBindings;
import org.sosadly.sfriends.common.FriendManager;
import org.sosadly.sfriends.network.SfriendsNetwork;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@Mod(Sfriends.MOD_ID)
public class Sfriends {
    public static final String MOD_ID = "sfriends";

    public Sfriends() {
        SfriendsNetwork.register();
        @SuppressWarnings("removal")
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.register(this);
    }

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
            KeyBindings.register(event);
        }
    }

   @EventBusSubscriber(modid = Sfriends.MOD_ID, bus = EventBusSubscriber.Bus.FORGE)
    public static class ServerEvents {
        @SubscribeEvent
        public static void onServerStarted(ServerStartedEvent event) {
            FriendManager.loadFromDisk();
        }
    }
}
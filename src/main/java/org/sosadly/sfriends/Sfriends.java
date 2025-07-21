package org.sosadly.sfriends;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.sosadly.sfriends.commands.ModCommands;
import org.sosadly.sfriends.common.FriendManager;
import org.sosadly.sfriends.network.SfriendsNetwork;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Sfriends.MOD_ID)
public class Sfriends {
    public static final String MOD_ID = "sfriends";

    public Sfriends() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(ModCommands.class);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        SfriendsNetwork.register();
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        FriendManager.loadFromDisk();
    }
    
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            FriendManager.syncFriends(player);
        }
    }
}
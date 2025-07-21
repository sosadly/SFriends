package org.sosadly.sfriends.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider; // <-- НОВИЙ ІМПОРТ
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.sosadly.sfriends.common.FriendManager;

public class DelFriend {

    // Створюємо константу з посиланням на метод, який надає підказки.
    // Це правильний спосіб отримати підказки гравців у сучасних версіях.
    private static final SuggestionProvider<CommandSourceStack> PLAYER_NAMES_SUGGESTION_PROVIDER = (context, builder) ->
            SharedSuggestionProvider.suggest(ServerLifecycleHooks.getCurrentServer().getPlayerNames(), builder);


    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("delfriend")
            .requires(source -> source.hasPermission(0))
            .then(
                Commands.argument("target", StringArgumentType.word())
                    // Використовуємо нашу нову константу як постачальника підказок.
                    .suggests(PLAYER_NAMES_SUGGESTION_PROVIDER)
                    .executes(DelFriend::removeFriend)
            );
    }

    private static int removeFriend(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer sender = ctx.getSource().getPlayerOrException();
        
        String targetName = StringArgumentType.getString(ctx, "target");
        
        ServerPlayer targetPlayer = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByName(targetName);

        if (targetPlayer == null) {
            ctx.getSource().sendFailure(Component.literal("Гравець з ніком '" + targetName + "' не знайдений на сервері."));
            return 0;
        }

        if (sender.getUUID().equals(targetPlayer.getUUID())) {
            ctx.getSource().sendFailure(Component.literal("Ви не можете видалити себе з друзів."));
            return 0;
        }

        if (FriendManager.areFriends(sender.getUUID(), targetPlayer.getUUID())) {
            FriendManager.removeFriends(sender.getUUID(), targetPlayer.getUUID());
            ctx.getSource().sendSuccess(() -> Component.literal("Гравець " + targetPlayer.getName().getString() + " видалений з вашого списку друзів."), true);
            return 1; // Успіх
        }

        ctx.getSource().sendFailure(Component.literal("Гравець " + targetPlayer.getName().getString() + " не є у вашому списку друзів."));
        return 0; // Невдача
    }
}
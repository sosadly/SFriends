package org.sosadly.sfriends.commands;

import org.sosadly.sfriends.common.FriendManager;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

public class DelFriend {
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("delfriend")
            .then(
                Commands.argument("target", EntityArgument.player())
                .executes(ctx -> removeFriend(ctx))
            );
    }

    private static int removeFriend(CommandContext<CommandSourceStack> ctx) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        ServerPlayer sender = source.getPlayerOrException();
        ServerPlayer targetPlayer = EntityArgument.getPlayer(ctx, "target");

        if (targetPlayer == null) {
            return 0;
        }

        if(!FriendManager.areFriends(sender.getUUID(), targetPlayer.getUUID())) {
            return 0;
        }

        FriendManager.removeFriends(sender.getUUID(), targetPlayer.getUUID());

        return 1;
    }
}

package com.gugas749.abyssbubbles.command;

import com.gugas749.abyssbubbles.bubble.BubbleManager;
import com.gugas749.abyssbubbles.network.BubbleNetwork;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber
public class BubbleCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        register(event.getDispatcher());
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("bubble")
                        // /bubble screen — open config screen for the executing player
                        .then(Commands.literal("screen")
                                .executes(BubbleCommand::executeScreen))

                        // /bubble grant usage <player>
                        .then(Commands.literal("grant")
                                .requires(src -> src.hasPermission(2))
                                .then(Commands.literal("usage")
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .executes(ctx -> executeGrantUsage(ctx,
                                                        EntityArgument.getPlayer(ctx, "player")))))
                                // /bubble grant config <player>
                                .then(Commands.literal("config")
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .executes(ctx -> executeGrantConfig(ctx,
                                                        EntityArgument.getPlayer(ctx, "player"))))))

                        // /bubble revoke usage <player>
                        .then(Commands.literal("revoke")
                                .requires(src -> src.hasPermission(2))
                                .then(Commands.literal("usage")
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .executes(ctx -> executeRevokeUsage(ctx,
                                                        EntityArgument.getPlayer(ctx, "player")))))
                                // /bubble revoke config <player>
                                .then(Commands.literal("config")
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .executes(ctx -> executeRevokeConfig(ctx,
                                                        EntityArgument.getPlayer(ctx, "player"))))))
        );
    }

    // -------------------------------------------------------------------------
    // Executors
    // -------------------------------------------------------------------------

    private static int executeScreen(CommandContext<CommandSourceStack> ctx) {
        try {
            CommandSourceStack src = ctx.getSource();
            ServerPlayer player;
            try {
                player = src.getPlayerOrException();
            } catch (Exception e) {
                src.sendFailure(Component.translatable("abyssbubbles.command.player_only"));
                return 0;
            }

            if (!BubbleManager.hasConfigPermission(player.getUUID())) {
                player.sendSystemMessage(Component.translatable("abyssbubbles.command.no_config_permission"));
                return 0;
            }

            BubbleNetwork.openScreenForPlayer(player);
            return 1;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("§c[AbyssBubbles] Screen error: " + e.getClass().getSimpleName() + ": " + e.getMessage()));
            e.printStackTrace();
            return 0;
        }
    }

    private static int executeGrantUsage(CommandContext<CommandSourceStack> ctx, ServerPlayer target) {
        BubbleManager.grantUsage(target);
        target.sendSystemMessage(Component.translatable("abyssbubbles.command.usage_granted"));
        ctx.getSource().sendSuccess(
                () -> Component.translatable("abyssbubbles.command.usage_granted_staff",
                        target.getDisplayName().getString()), true);
        return 1;
    }

    private static int executeGrantConfig(CommandContext<CommandSourceStack> ctx, ServerPlayer target) {
        BubbleManager.grantConfig(target);
        target.sendSystemMessage(Component.translatable("abyssbubbles.command.config_granted"));
        ctx.getSource().sendSuccess(
                () -> Component.translatable("abyssbubbles.command.config_granted_staff",
                        target.getDisplayName().getString()), true);
        return 1;
    }

    private static int executeRevokeUsage(CommandContext<CommandSourceStack> ctx, ServerPlayer target) {
        BubbleManager.revokeUsage(target);
        target.sendSystemMessage(Component.translatable("abyssbubbles.command.usage_revoked"));
        ctx.getSource().sendSuccess(
                () -> Component.translatable("abyssbubbles.command.usage_revoked_staff",
                        target.getDisplayName().getString()), true);
        return 1;
    }

    private static int executeRevokeConfig(CommandContext<CommandSourceStack> ctx, ServerPlayer target) {
        BubbleManager.revokeConfig(target);
        target.sendSystemMessage(Component.translatable("abyssbubbles.command.config_revoked"));
        ctx.getSource().sendSuccess(
                () -> Component.translatable("abyssbubbles.command.config_revoked_staff",
                        target.getDisplayName().getString()), true);
        return 1;
    }
}

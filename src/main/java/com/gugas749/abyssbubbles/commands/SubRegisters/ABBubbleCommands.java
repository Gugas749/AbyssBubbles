package com.gugas749.abyssbubbles.commands.SubRegisters;

import com.gugas749.abyssbubbles.Abyssbubbles;
import com.gugas749.abyssbubbles.commands.BubblePermissionManager;
import com.gugas749.abyssbubbles.data.BubbleConfigAttachment;
import com.gugas749.abyssbubbles.data.ModAttachments;
import com.gugas749.abyssbubbles.network.OpenBubbleScreenPacket;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * /bubble grant usage <player>
 * /bubble grant config <player>
 * /bubble revoke usage <player>
 * /bubble revoke config <player>
 */
public class ABBubbleCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("bubble")
                .requires(src -> src.hasPermission(2))

                // /bubble screen — open config screen for yourself (requires config permission)
                .then(Commands.literal("screen")
                    .executes(ABBubbleCommands::executeScreen))

                .then(Commands.literal("grant")
                    .then(Commands.literal("usage")
                        .then(Commands.argument("player", EntityArgument.player())
                            .executes(ctx -> executeGrantUsage(ctx, EntityArgument.getPlayer(ctx, "player")))))
                    .then(Commands.literal("config")
                        .then(Commands.argument("player", EntityArgument.player())
                            .executes(ctx -> executeGrantConfig(ctx, EntityArgument.getPlayer(ctx, "player"))))))

                .then(Commands.literal("revoke")
                    .then(Commands.literal("usage")
                        .then(Commands.argument("player", EntityArgument.player())
                            .executes(ctx -> executeRevokeUsage(ctx, EntityArgument.getPlayer(ctx, "player")))))
                    .then(Commands.literal("config")
                        .then(Commands.argument("player", EntityArgument.player())
                            .executes(ctx -> executeRevokeConfig(ctx, EntityArgument.getPlayer(ctx, "player"))))))
        );

        Abyssbubbles.LOGGER.info("[AbyssBubbles] Registered: /bubble grant|revoke <usage|config> <player>");
    }

    private static int executeScreen(CommandContext<CommandSourceStack> ctx) {
        try {
            ServerPlayer player = ctx.getSource().getPlayerOrException();
            if (!BubblePermissionManager.get(ctx.getSource().getServer()).hasConfig(player.getUUID())) {
                player.sendSystemMessage(Component.translatable("abyssbubbles.command.no_config_permission"));
                return 0;
            }
            BubbleConfigAttachment data = player.getData(ModAttachments.BUBBLE_CONFIG.get());
            PacketDistributor.sendToPlayer(player, new OpenBubbleScreenPacket(
                    data.getBgColor(), data.getBorderColor(), data.getTextColor(),
                    data.getOffset(), data.getSpacing(), data.isHideNametag()));
            return 1;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.translatable("abyssbubbles.command.player_only"));
            return 0;
        }
    }

    private static int executeGrantUsage(CommandContext<CommandSourceStack> ctx, ServerPlayer target) {
        try {
            BubblePermissionManager.get(ctx.getSource().getServer()).grantUsage(target);
            target.sendSystemMessage(Component.translatable("abyssbubbles.command.usage_granted"));
            ctx.getSource().sendSuccess(
                () -> Component.translatable("abyssbubbles.command.usage_granted_staff", target.getName().getString()), true);
            return 1;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.translatable("abyssbubbles.command.player_not_found"));
            return 0;
        }
    }

    private static int executeGrantConfig(CommandContext<CommandSourceStack> ctx, ServerPlayer target) {
        try {
            BubblePermissionManager.get(ctx.getSource().getServer()).grantConfig(target);
            target.sendSystemMessage(Component.translatable("abyssbubbles.command.config_granted"));
            ctx.getSource().sendSuccess(
                () -> Component.translatable("abyssbubbles.command.config_granted_staff", target.getName().getString()), true);
            return 1;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.translatable("abyssbubbles.command.player_not_found"));
            return 0;
        }
    }

    private static int executeRevokeUsage(CommandContext<CommandSourceStack> ctx, ServerPlayer target) {
        try {
            BubblePermissionManager.get(ctx.getSource().getServer()).revokeUsage(target);
            target.sendSystemMessage(Component.translatable("abyssbubbles.command.usage_revoked"));
            ctx.getSource().sendSuccess(
                () -> Component.translatable("abyssbubbles.command.usage_revoked_staff", target.getName().getString()), true);
            return 1;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.translatable("abyssbubbles.command.player_not_found"));
            return 0;
        }
    }

    private static int executeRevokeConfig(CommandContext<CommandSourceStack> ctx, ServerPlayer target) {
        try {
            BubblePermissionManager.get(ctx.getSource().getServer()).revokeConfig(target);
            target.sendSystemMessage(Component.translatable("abyssbubbles.command.config_revoked"));
            ctx.getSource().sendSuccess(
                () -> Component.translatable("abyssbubbles.command.config_revoked_staff", target.getName().getString()), true);
            return 1;
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.translatable("abyssbubbles.command.player_not_found"));
            return 0;
        }
    }
}

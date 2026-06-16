package com.gugas749.abyssbubbles.network;

import com.gugas749.abyssbubbles.Abyssbubbles;
import com.gugas749.abyssbubbles.config.BubbleConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class BubbleNetwork {

    public static final ResourceLocation CHANNEL = ResourceLocation.fromNamespaceAndPath(Abyssbubbles.MODID, "main");

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(BubbleNetwork::onRegisterPayloads);
    }

    private static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(Abyssbubbles.MODID).versioned("1");

        // Server -> Client: display a bubble above an entity
        registrar.playToClient(
                BubbleShowPacket.TYPE,
                BubbleShowPacket.STREAM_CODEC,
                BubbleShowPacket::handle
        );

        // Server -> Client: sync a player's config to themselves
        registrar.playToClient(
                BubbleSyncConfigPacket.TYPE,
                BubbleSyncConfigPacket.STREAM_CODEC,
                BubbleSyncConfigPacket::handle
        );

        // Server -> Client: notify active state change for a player
        registrar.playToClient(
                BubbleActivePacket.TYPE,
                BubbleActivePacket.STREAM_CODEC,
                BubbleActivePacket::handle
        );

        // Server -> Client: open the config screen
        registrar.playToClient(
                BubbleOpenScreenPacket.TYPE,
                BubbleOpenScreenPacket.STREAM_CODEC,
                BubbleOpenScreenPacket::handle
        );

        // Client -> Server: player submits updated config
        registrar.playToServer(
                BubbleUpdateConfigPacket.TYPE,
                BubbleUpdateConfigPacket.STREAM_CODEC,
                BubbleUpdateConfigPacket::handle
        );
    }

    // -------------------------------------------------------------------------
    // Helpers called by BubbleManager
    // -------------------------------------------------------------------------

    public static void openScreenForPlayer(ServerPlayer player) {
        BubbleOpenScreenPacket packet = new BubbleOpenScreenPacket(
                com.gugas749.abyssbubbles.bubble.BubbleManager.getConfig(player.getUUID()));
        PacketDistributor.sendToPlayer(player, packet);
    }

    public static void sendBubbleToNearby(ServerPlayer source, String message, BubbleConfig config) {
        BubbleShowPacket packet = new BubbleShowPacket(source.getUUID(), message, config);
        // Send to all players tracking this entity (within server-side tracking range)
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(source, packet);
    }

    public static void syncConfigToClient(ServerPlayer player) {
        BubbleSyncConfigPacket packet = new BubbleSyncConfigPacket(player.getUUID(), player.server
                .getPlayerList().getPlayer(player.getUUID()) != null
                ? com.gugas749.abyssbubbles.bubble.BubbleManager.getConfig(player.getUUID())
                : new BubbleConfig());
        PacketDistributor.sendToPlayer(player, packet);
    }

    public static void syncActiveStateToNearby(ServerPlayer source, boolean active) {
        BubbleActivePacket packet = new BubbleActivePacket(source.getUUID(), active);
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(source, packet);
    }
}

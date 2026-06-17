package com.gugas749.abyssbubbles.client;

import com.gugas749.abyssbubbles.client.screen.config.BubbleConfigScreen;
import com.gugas749.abyssbubbles.network.OpenBubbleScreenPacket;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientScreenOpener {

    public static void openBubbleScreen(OpenBubbleScreenPacket packet) {
        Minecraft.getInstance().setScreen(new BubbleConfigScreen(packet));
    }
}

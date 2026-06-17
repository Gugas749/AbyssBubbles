package com.gugas749.abyssbubbles.data;

import com.gugas749.abyssbubbles.AbyssBubblesConfig;
import com.gugas749.abyssbubbles.Abyssbubbles;
import com.gugas749.abyssbubbles.network.BubbleConfigSyncHandler;
import com.gugas749.abyssbubbles.util.Color;
import java.util.function.Supplier;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModAttachments {
   public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Abyssbubbles.MODID);
   public static final Supplier<AttachmentType<BubblesAttachment>> BUBBLES = ATTACHMENT_TYPES.register(
      "bubbles", () -> AttachmentType.builder(() -> new BubblesAttachment()).serialize(BubblesAttachment.CODEC).copyOnDeath().build()
   );
   public static final Supplier<AttachmentType<BubbleConfigAttachment>> BUBBLE_CONFIG = ATTACHMENT_TYPES.register(
      "bubble_config",
      () -> AttachmentType.builder(
            () -> new BubbleConfigAttachment(
               new Color(1.0F, 1.0F, 1.0F),
               new Color(0.0F, 0.0F, 0.0F),
               0,
               (Boolean)AbyssBubblesConfig.HIDE_NAMETAG.get(),
               (Double)AbyssBubblesConfig.BUBBLE_OFFSET.get(),
               (Double)AbyssBubblesConfig.BUBBLE_SPACING.get()
            )
         )
         .serialize(BubbleConfigAttachment.CODEC)
         .sync(new BubbleConfigSyncHandler())
         .copyOnDeath()
         .build()
   );
}

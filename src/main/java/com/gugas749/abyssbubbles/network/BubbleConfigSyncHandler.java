package com.gugas749.abyssbubbles.network;

import com.gugas749.abyssbubbles.data.BubbleConfigAttachment;
import com.gugas749.abyssbubbles.util.Color;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.attachment.AttachmentSyncHandler;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import org.jetbrains.annotations.Nullable;

public class BubbleConfigSyncHandler implements AttachmentSyncHandler<BubbleConfigAttachment> {
   public void write(RegistryFriendlyByteBuf buf, BubbleConfigAttachment data, boolean initialSync) {
      buf.writeBoolean(initialSync);
      if (initialSync) {
         Color.STREAM_CODEC.encode(buf, data.getBgColor());
         Color.STREAM_CODEC.encode(buf, data.getBorderColor());
         buf.writeInt(data.getTextColor());
         buf.writeBoolean(data.isHideNametag());
         buf.writeDouble(data.getOffset());
         buf.writeDouble(data.getSpacing());
      } else {
         boolean isBgChanged = data.isBgColorChanged();
         boolean isBorderChanged = data.isBorderColorChanged();
         boolean isTextChanged = data.isTextColorChanged();
         boolean isHideNametagChanged = data.isHideNametagChanged();
         boolean isOffsetChanged = data.isOffsetChanged();
         boolean isSpacingChanged = data.isSpacingChanged();
         buf.writeBoolean(isBgChanged);
         if (isBgChanged) {
            Color.STREAM_CODEC.encode(buf, data.getBgColor());
         }

         buf.writeBoolean(isBorderChanged);
         if (isBorderChanged) {
            Color.STREAM_CODEC.encode(buf, data.getBorderColor());
         }

         buf.writeBoolean(isTextChanged);
         if (isTextChanged) {
            buf.writeInt(data.getTextColor());
         }

         buf.writeBoolean(isHideNametagChanged);
         if (isHideNametagChanged) {
            buf.writeBoolean(data.isHideNametag());
         }

         buf.writeBoolean(isOffsetChanged);
         if (isOffsetChanged) {
            buf.writeDouble(data.getOffset());
         }

         buf.writeBoolean(isSpacingChanged);
         if (isSpacingChanged) {
            buf.writeDouble(data.getSpacing());
         }

         data.resetFlags();
      }
   }

   @Nullable
   public BubbleConfigAttachment read(IAttachmentHolder holder, RegistryFriendlyByteBuf buf, @Nullable BubbleConfigAttachment previousValue) {
      BubbleConfigAttachment data = previousValue == null
         ? new BubbleConfigAttachment(new Color(1.0F, 1.0F, 1.0F), new Color(0.0F, 0.0F, 0.0F), 0)
         : previousValue;
      boolean isInitialSync = buf.readBoolean();
      if (isInitialSync) {
         data.setBgColor((Color)Color.STREAM_CODEC.decode(buf));
         data.setBorderColor((Color)Color.STREAM_CODEC.decode(buf));
         data.setTextColor(buf.readInt());
         data.setHideNametag(buf.readBoolean());
         data.setOffset(buf.readDouble());
         data.setSpacing(buf.readDouble());
      } else {
         if (buf.readBoolean()) {
            data.setBgColor((Color)Color.STREAM_CODEC.decode(buf));
         }

         if (buf.readBoolean()) {
            data.setBorderColor((Color)Color.STREAM_CODEC.decode(buf));
         }

         if (buf.readBoolean()) {
            data.setTextColor(buf.readInt());
         }

         if (buf.readBoolean()) {
            data.setHideNametag(buf.readBoolean());
         }

         if (buf.readBoolean()) {
            data.setOffset(buf.readDouble());
         }

         if (buf.readBoolean()) {
            data.setSpacing(buf.readDouble());
         }
      }

      return data;
   }
}

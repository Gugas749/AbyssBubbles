package com.gugas749.abyssbubbles.client.screen.config;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.LinearLayout.Orientation;
import net.minecraft.world.entity.player.Player;

public class MainPanel extends LinearLayout {
   private ConfigPanel configPanel;
   private PreviewPanel previewPanel;

   public MainPanel(Player player, BubbleConfigState state, Font font, Runnable onSaveAndClose, Runnable reset) {
      super(0, 0, Orientation.HORIZONTAL);
      this.spacing(40);
      this.defaultCellSetting().alignVerticallyMiddle();
      this.defaultCellSetting().alignHorizontallyCenter();
      this.configPanel = new ConfigPanel(state, font, onSaveAndClose, reset);
      this.previewPanel = new PreviewPanel(player, state, font);
      this.addChild(this.configPanel);
      this.addChild(this.previewPanel);
      this.arrangeElements();
   }
}

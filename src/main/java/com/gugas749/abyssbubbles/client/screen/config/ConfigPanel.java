package com.gugas749.abyssbubbles.client.screen.config;

import java.util.EnumMap;
import java.util.Map;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.layouts.LinearLayout.Orientation;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;

public class ConfigPanel extends LinearLayout {
   private final ColorPanel colorPanel;
   private GeneralPanel generalPanel;
   private final GridLayout tabsLayout;
   private MAIN_TABS currentTab = MAIN_TABS.GENERAL;
   private final Map<MAIN_TABS, Button> tabsButtons = new EnumMap<>(MAIN_TABS.class);
   private static final int TAB_BUTTON_SPACING = 6;
   private static final int WIDGET_WIDTH = 200;
   private BubbleConfigState state;

   public ConfigPanel(BubbleConfigState state, Font font, Runnable onSaveAndClose, Runnable reset) {
      super(0, 0, Orientation.VERTICAL);
      this.state = state;
      this.spacing(8);
      this.tabsLayout = this.createTabs();
      this.generalPanel = new GeneralPanel(this.state, 200);
      this.colorPanel = new ColorPanel(this.state, font);
      FrameLayout tabContentContainer = new FrameLayout();
      tabContentContainer.addChild(this.generalPanel);
      tabContentContainer.addChild(this.colorPanel);
      this.addChild(this.tabsLayout);
      this.addChild(tabContentContainer);
      this.addChild(SpacerElement.height(15));
      LinearLayout footerLayout = LinearLayout.horizontal();
      ((Button)footerLayout.addChild(ExtendedButton.builder(Component.translatable("gui.abyssbubbles.config.button.reset"), b -> reset.run()).width(50).build()))
         .setTooltip(Tooltip.create(Component.translatable("gui.abyssbubbles.config.button.reset.tooltip")));
      footerLayout.addChild(ExtendedButton.builder(Component.translatable("gui.abyssbubbles.config.button.save"), b -> onSaveAndClose.run()).width(150).build());
      this.addChild(footerLayout);
      this.changeTab(MAIN_TABS.GENERAL);
      this.defaultCellSetting().alignHorizontallyCenter();
      this.defaultCellSetting().alignVerticallyMiddle();
      this.arrangeElements();
   }

   private GridLayout createTabs() {
      GridLayout tabsLayout = new GridLayout().rowSpacing(6).columnSpacing(6);
      this.addTabToGroup(tabsLayout, MAIN_TABS.GENERAL, "gui.abyssbubbles.config.tab.general_settings", 0, 0);
      this.addTabToGroup(tabsLayout, MAIN_TABS.COLOR, "gui.abyssbubbles.config.tab.color_settings", 0, 1);
      return tabsLayout;
   }

   private void addTabToGroup(GridLayout layout, MAIN_TABS tab, String translationKey, int row, int col) {
      int width = 97;
      Button button = ExtendedButton.builder(Component.translatable(translationKey), b -> this.changeTab(tab)).width(width).build();
      this.tabsButtons.put(tab, button);
      layout.addChild(button, row, col);
   }

   private void changeTab(MAIN_TABS tab) {
      this.currentTab = tab;
      if (this.currentTab == MAIN_TABS.COLOR) {
         this.generalPanel.visitWidgets(w -> w.visible = false);
         this.colorPanel.visitWidgets(w -> w.visible = true);
      } else {
         this.generalPanel.visitWidgets(w -> w.visible = true);
         this.colorPanel.visitWidgets(w -> w.visible = false);
      }
   }

   private enum MAIN_TABS {
      GENERAL,
      COLOR;
   }
}

package com.gugas749.abyssbubbles.client.screen.config;

import com.gugas749.abyssbubbles.util.Color;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.layouts.LinearLayout.Orientation;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;

public class ColorPanel extends LinearLayout {
   private static final int TAB_BUTTON_SPACING = 4;
   private static final int WIDGET_WIDTH = 200;
   private static final int WIDGET_HEIGHT = 20;
   private TAB currentTab = TAB.BACKGROUND;
   private Map<TAB, Button> tabsButtons = new EnumMap<>(TAB.class);
   private final BubbleConfigState state;
   private final ExtendedSlider redSlider;
   private final ExtendedSlider greenSlider;
   private final ExtendedSlider blueSlider;
   private final EditBox hexColor;

   public ColorPanel(BubbleConfigState state, Font font) {
      super(0, 0, Orientation.VERTICAL);
      this.state = state;
      this.spacing(4);
      this.redSlider = this.createSlider("gui.abyssbubbles.config.color.red");
      this.greenSlider = this.createSlider("gui.abyssbubbles.config.color.green");
      this.blueSlider = this.createSlider("gui.abyssbubbles.config.color.blue");
      this.hexColor = new EditBox(font, 0, 0, 200, 20, Component.empty());
      this.hexColor.setMaxLength(7);
      this.hexColor.setResponder(this::onHexColorChange);
      this.hexColor.setTooltip(Tooltip.create(Component.translatable("gui.abyssbubbles.config.hexColor.tooltip")));
      this.hexColor.setFilter(text -> text.matches("^[#a-fA-F0-9]*$"));
      this.hexColor.setHint(Component.literal("#FFFFFF").withStyle(ChatFormatting.DARK_GRAY));
      GridLayout tabsLayout = this.createTabs();
      this.addChild(tabsLayout);
      this.addChild(this.redSlider);
      this.addChild(this.greenSlider);
      this.addChild(this.blueSlider);
      this.addChild(this.hexColor);
      this.defaultCellSetting().alignVerticallyMiddle();
      this.defaultCellSetting().alignHorizontallyCenter();
      this.arrangeElements();
      this.changeTab(TAB.BACKGROUND);
   }

   private void onHexColorChange(String text) {
      if (text.startsWith("#")) {
         text = text.substring(1);
      }

      if (text.length() == 6) {
         try {
            int hexValue = Integer.parseInt(text, 16);
            Color newColor = Color.hexToRGB(hexValue);
            this.setSlidersByTab(newColor);
         } catch (NumberFormatException var4) {
         }
      }
   }

   private ExtendedSlider createSlider(String translationKey) {
      return new ExtendedSlider(0, 0, 200, 20, Component.translatable(translationKey), Component.empty(), 0.0, 255.0, 255.0, 1.0, 0, true) {
         protected void applyValue() {
            super.applyValue();
            ColorPanel.this.updateColor();
         }
      };
   }

   private void updateColor() {
      float r = (float)(this.redSlider.getValue() / 255.0);
      float g = (float)(this.greenSlider.getValue() / 255.0);
      float b = (float)(this.blueSlider.getValue() / 255.0);
      Color newColor = new Color(r, g, b);
      switch (this.currentTab) {
         case BACKGROUND:
            this.state.bgColor = newColor;
            break;
         case BORDER:
            this.state.borderColor = newColor;
            break;
         case TEXT:
            this.state.textRgbColor = newColor;
            this.state.textColor = Color.rgbToHex(this.state.textRgbColor);
      }

      if (!this.hexColor.isFocused()) {
         int hexInt = Color.rgbToHex(newColor);
         this.hexColor.setValue(String.format("#%06X", 16777215 & hexInt));
      }
   }

   private GridLayout createTabs() {
      GridLayout tabsLayout = new GridLayout().rowSpacing(4).columnSpacing(4);
      this.addTabToGroup(tabsLayout, TAB.BACKGROUND, "gui.abyssbubbles.config.tab.background", 0, 0);
      this.addTabToGroup(tabsLayout, TAB.BORDER, "gui.abyssbubbles.config.tab.border", 0, 1);
      this.addTabToGroup(tabsLayout, TAB.TEXT, "gui.abyssbubbles.config.tab.text", 0, 2);
      return tabsLayout;
   }

   private void addTabToGroup(GridLayout layout, TAB tab, String translationKey, int row, int col) {
      int width = 64;
      Button button = ExtendedButton.builder(Component.translatable(translationKey), b -> this.changeTab(tab)).width(width).build();
      this.tabsButtons.put(tab, button);
      layout.addChild(button, row, col);
   }

   private void setSlidersByTab(Color color) {
      this.redSlider.setValue(color.r() * 255.0);
      this.greenSlider.setValue(color.g() * 255.0);
      this.blueSlider.setValue(color.b() * 255.0);
   }

   private void changeTab(TAB tab) {
      this.currentTab = tab;
      switch (tab) {
         case BACKGROUND:
            this.setSlidersByTab(this.state.bgColor);
            break;
         case BORDER:
            this.setSlidersByTab(this.state.borderColor);
            break;
         case TEXT:
            this.setSlidersByTab(this.state.textRgbColor);
      }
   }

   private enum TAB {
      BACKGROUND,
      BORDER,
      TEXT;
   }
}

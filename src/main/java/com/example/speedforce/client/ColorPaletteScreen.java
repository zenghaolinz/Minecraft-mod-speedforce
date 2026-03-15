package com.example.speedforce.client;

import com.example.speedforce.network.TrailColorPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class ColorPaletteScreen extends Screen {
    private static final int[][] PRESET_COLORS = {
        {255, 210, 0}, {255, 0, 0}, {0, 150, 255}, {0, 255, 100},
        {180, 0, 255}, {255, 255, 255}, {0, 255, 255}, {255, 100, 0},
        {255, 150, 200}, {100, 255, 100}, {150, 150, 255}, {255, 255, 0}
    };
    
    private static final String[] COLOR_KEYS = {
        "yellow", "red", "blue", "green", "purple", "white",
        "cyan", "orange", "pink", "lime", "lightblue", "gold"
    };

    private int selectedR = 255;
    private int selectedG = 210;
    private int selectedB = 0;
    private float hue = 0.0f;
    private int hueSliderY;

    public ColorPaletteScreen() {
        super(Component.translatable("gui.speedforce.color_palette"));
    }

    @Override
    protected void init() {
        super.init();
        
        this.selectedR = ClientSpeedData.trailColorR;
        this.selectedG = ClientSpeedData.trailColorG;
        this.selectedB = ClientSpeedData.trailColorB;
        
        float[] hsb = java.awt.Color.RGBtoHSB(selectedR, selectedG, selectedB, null);
        this.hue = hsb[0];

        int centerX = this.width / 2;
        int startY = this.height / 2 - 80;

        for (int i = 0; i < PRESET_COLORS.length; i++) {
            final int idx = i;
            int row = i / 6;
            int col = i % 6;
            int x = centerX - 153 + col * 52;
            int y = startY + row * 28;
            final int[] color = PRESET_COLORS[idx];
            
            this.addRenderableWidget(Button.builder(
                Component.translatable("gui.speedforce.color." + COLOR_KEYS[idx]), 
                btn -> {
                    selectedR = color[0];
                    selectedG = color[1];
                    selectedB = color[2];
                    updateHue();
                }
            ).bounds(x, y, 50, 22).build());
        }

        hueSliderY = startY + 70;

        this.addRenderableWidget(Button.builder(Component.translatable("gui.speedforce.apply"), btn -> applyColor())
            .bounds(centerX - 55, this.height - 50, 50, 20).build());

        this.addRenderableWidget(Button.builder(Component.translatable("gui.speedforce.cancel"), btn -> this.onClose())
            .bounds(centerX + 5, this.height - 50, 50, 20).build());
    }

    private void updateHue() {
        float[] hsb = java.awt.Color.RGBtoHSB(selectedR, selectedG, selectedB, null);
        this.hue = hsb[0];
    }

    private void applyColor() {
        ClientSpeedData.trailColorR = selectedR;
        ClientSpeedData.trailColorG = selectedG;
        ClientSpeedData.trailColorB = selectedB;
        PacketDistributor.sendToServer(new TrailColorPayload(selectedR, selectedG, selectedB));
        this.onClose();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        
        int centerX = this.width / 2;
        int startY = this.height / 2 - 80;
        
        graphics.fill(centerX - 160, 12, centerX + 160, 32, 0xCC000000);
        graphics.drawCenteredString(this.font, Component.translatable("gui.speedforce.select_color"), centerX, 20, 0xFFFFFF);
        
        for (int i = 0; i < PRESET_COLORS.length; i++) {
            int row = i / 6;
            int col = i % 6;
            int x = centerX - 153 + col * 52;
            int y = startY + row * 28;
            int[] color = PRESET_COLORS[i];
            
            boolean isSelected = (selectedR == color[0] && selectedG == color[1] && selectedB == color[2]);
            if (isSelected) {
                graphics.renderOutline(x - 1, y - 1, 52, 24, 0xFFFFFFFF);
            }
        }

        graphics.fill(centerX - 102, hueSliderY - 18, centerX + 102, hueSliderY - 2, 0xCC000000);
        graphics.drawCenteredString(this.font, Component.translatable("gui.speedforce.hue"), centerX, hueSliderY - 14, 0xFFFFFF);

        int sliderX = centerX - 100;
        int sliderWidth = 200;
        graphics.fill(sliderX - 1, hueSliderY - 1, sliderX + sliderWidth + 1, hueSliderY + 21, 0xFF000000);
        for (int i = 0; i < sliderWidth; i++) {
            float h = i / (float) sliderWidth;
            int color = java.awt.Color.HSBtoRGB(h, 1.0f, 1.0f);
            graphics.fill(sliderX + i, hueSliderY, sliderX + i + 1, hueSliderY + 20, color | 0xFF000000);
        }
        
        int markerX = sliderX + (int) (hue * sliderWidth);
        graphics.fill(markerX - 2, hueSliderY - 3, markerX + 2, hueSliderY + 23, 0xFFFFFFFF);

        int previewX = centerX - 40;
        int previewY = this.height - 90;
        graphics.fill(previewX - 1, previewY - 1, previewX + 81, previewY + 31, 0xFF000000);
        graphics.fill(previewX, previewY, previewX + 80, previewY + 30, 
            (0xFF << 24) | (selectedR << 16) | (selectedG << 8) | selectedB);
        
        graphics.fill(centerX - 80, this.height - 58, centerX + 80, this.height - 45, 0xCC000000);
        graphics.drawCenteredString(this.font, String.format("RGB: %d, %d, %d", selectedR, selectedG, selectedB), 
            centerX, this.height - 55, 0xFFFFFF);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        handleSliderClick(mouseX, mouseY);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        handleSliderClick(mouseX, mouseY);
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    private void handleSliderClick(double mouseX, double mouseY) {
        int sliderX = this.width / 2 - 100;
        int sliderWidth = 200;
        
        if (mouseY >= hueSliderY - 5 && mouseY <= hueSliderY + 25) {
            hue = (float) Math.max(0, Math.min(1, (mouseX - sliderX) / sliderWidth));
            int color = java.awt.Color.HSBtoRGB(hue, 1.0f, 1.0f);
            selectedR = (color >> 16) & 0xFF;
            selectedG = (color >> 8) & 0xFF;
            selectedB = color & 0xFF;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
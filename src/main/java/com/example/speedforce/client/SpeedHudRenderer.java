package com.example.speedforce.client;

import com.example.speedforce.SpeedForceMod;
import com.example.speedforce.item.FlashSuitArmorItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = SpeedForceMod.MOD_ID, value = Dist.CLIENT)
public class SpeedHudRenderer {

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.options.hideGui) return;

        GuiGraphics graphics = event.getGuiGraphics();

        if (!ClientSpeedData.hasPower) {
            if (ClientSpeedData.showHelp) {
                graphics.fill(8, 8, 180, 52, 0x80000000);
                graphics.drawString(mc.font, "未获得神速力", 12, 12, 0xFF5555);
                graphics.drawString(mc.font, "获取方式: 中毒+闪电击中", 12, 24, 0xAAAAAA);
                graphics.drawString(mc.font, "或使用粒子加速器", 12, 36, 0xAAAAAA);
                graphics.drawString(mc.font, "U - 收起帮助", 12, 48, 0xAAAAAA);
            } else {
                graphics.fill(8, 8, 100, 24, 0x80000000);
                graphics.drawString(mc.font, "未获得神速力", 12, 12, 0xFF5555);
                graphics.drawString(mc.font, "[U] 帮助", 70, 12, 0xAAAAAA);
            }
            return;
        }

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        boolean hasFullSuit = hasFullFlashSuit(player);
        int maxLevel = hasFullSuit ? 14 : 10;

        if (ClientSpeedData.showHelp) {
            graphics.fill(8, 8, 140, 108, 0x80000000);
            graphics.drawString(mc.font, "C - 开关超能力", 12, 12, 0xFFFFFF);
            graphics.drawString(mc.font, "X - 加速 / Z - 减速", 12, 24, 0xFFFFFF);
            graphics.drawString(mc.font, "B - 子弹时间", 12, 36, 0xFFFFFF);
            graphics.drawString(mc.font, "N - 拖尾颜色", 12, 48, 0xFFFFFF);
            graphics.drawString(mc.font, "R - 时间回溯", 12, 60, 0xFFFFFF);
            graphics.drawString(mc.font, "G - 切换箭袋箭矢", 12, 72, 0xFFFFFF);
            graphics.drawString(mc.font, "U - 收起帮助", 12, 84, 0xAAAAAA);
            
            String statusText = ClientSpeedData.speedLevel > 0 ? 
                "状态: Lv." + ClientSpeedData.speedLevel + "/" + maxLevel : "状态: 未启用";
            int statusColor = ClientSpeedData.speedLevel > 0 ? 0x55FF55 : 0xFF5555;
            graphics.drawString(mc.font, statusText, 12, 96, statusColor);

            int colorBoxX = 115;
            graphics.fill(colorBoxX, 84, colorBoxX + 20, 104, 0xFF000000);
            int trailColor = (0xFF << 24) | (ClientSpeedData.trailColorR << 16) | 
                             (ClientSpeedData.trailColorG << 8) | ClientSpeedData.trailColorB;
            graphics.fill(colorBoxX + 1, 85, colorBoxX + 19, 103, trailColor);
        } else {
            graphics.fill(8, 8, 100, 24, 0x80000000);
            graphics.drawString(mc.font, "[U] 帮助", 12, 12, 0xAAAAAA);
            
            String statusText = "Lv." + ClientSpeedData.speedLevel + "/" + maxLevel;
            int statusColor = ClientSpeedData.speedLevel > 0 ? 0x55FF55 : 0xFF5555;
            graphics.drawString(mc.font, statusText, 60, 12, statusColor);
        }

        int rightX = screenWidth - 70;
        
        graphics.fill(rightX, 10, rightX + 60, 30, 0x80000000);
        graphics.fill(rightX, 10, rightX + 60, 11, 0xFFD4AF37);
        graphics.fill(rightX, 29, rightX + 60, 30, 0xFFD4AF37);
        graphics.fill(rightX, 10, rightX + 1, 30, 0xFFD4AF37);
        graphics.fill(rightX + 59, 10, rightX + 60, 30, 0xFFD4AF37);
        
        graphics.drawString(mc.font, "⚡", rightX + 12, 16, 0xFFFF00);

        if (ClientSpeedData.isBulletTimeActive) {
            graphics.drawString(mc.font, "⌛", rightX + 42, 16, 0x00FFFF);
        }

        graphics.drawString(mc.font, String.valueOf(ClientSpeedData.speedLevel), rightX + 5, 38, 0xFFFFFF);

        double dx = player.getX() - player.xo;
        double dz = player.getZ() - player.zo;
        double speedBps = Math.sqrt(dx * dx + dz * dz) * 20.0;
        double speedKmh = speedBps * 3.6;

        int barWidth = 36;
        int barX = rightX + 20;
        int barY = 40;
        graphics.fill(barX, barY, barX + barWidth, barY + 4, 0xFF555555);

        double maxDisplaySpeed = 150.0; 
        float speedRatio = (float) Math.min(1.0, speedKmh / maxDisplaySpeed);
        int fillWidth = (int) (barWidth * speedRatio);
        
        if (fillWidth > 0) {
            graphics.fill(barX, barY, barX + fillWidth, barY + 4, 0xFFFF0000);
        }

        String kmhText = String.format("%.0f km/h", speedKmh);
        int textWidth = mc.font.width(kmhText);
        graphics.drawString(mc.font, kmhText, rightX + 60 - textWidth, 52, 0xFFFFFF);

        if (ClientSpeedData.hasPower && ClientSpeedData.speedLevel > 0) {
            int rewindBarWidth = 120;
            int rewindBarX = screenWidth / 2 - rewindBarWidth / 2;
            int rewindBarY = mc.getWindow().getGuiScaledHeight() - 40;

            graphics.fill(rewindBarX, rewindBarY, rewindBarX + rewindBarWidth, rewindBarY + 4, 0xFF444444);
            
            float rewindRatio = ClientRewindData.getRewindProgress();
            int rewindFillWidth = (int) (rewindBarWidth * rewindRatio);
            
            if (rewindFillWidth > 0) {
                graphics.fill(rewindBarX, rewindBarY, rewindBarX + rewindFillWidth, rewindBarY + 4, 0xFF9933FF);
            }
            
            if (ClientRewindData.isRewinding()) {
                graphics.drawCenteredString(mc.font, "§d时间回溯中... §7速度: " + ClientRewindData.rewindSpeed + "x", 
                    screenWidth / 2, rewindBarY - 14, 0xFFFFFFFF);
            } else if (ClientRewindData.isConfirming()) {
                float timeLeft = ClientRewindData.getConfirmTimeSeconds();
                graphics.drawCenteredString(mc.font, 
                    String.format("§e按 LEFT ALT 取消回溯 §7(%.1fs)", timeLeft), 
                    screenWidth / 2, rewindBarY - 14, 0xFFFFFFFF);
            } else {
                String rewindText = String.format("%.1f s", ClientSpeedData.clientHistorySize / 20.0f);
                graphics.drawString(mc.font, rewindText, rewindBarX + rewindBarWidth + 6, rewindBarY - 2, 0xFF9933FF);
            }
        }
    }

    private static boolean hasFullFlashSuit(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof FlashSuitArmorItem
            && player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof FlashSuitArmorItem
            && player.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof FlashSuitArmorItem
            && player.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof FlashSuitArmorItem;
    }
}
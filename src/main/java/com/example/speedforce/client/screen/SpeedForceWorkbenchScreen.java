package com.example.speedforce.client.screen;

import com.example.speedforce.item.SuitType;
import com.example.speedforce.menu.SpeedForceWorkbenchMenu;
import com.example.speedforce.network.WorkbenchPurchasePayload;
import com.example.speedforce.util.SuitPriceRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class SpeedForceWorkbenchScreen extends AbstractContainerScreen<SpeedForceWorkbenchMenu> {

    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(
        "speedforce", "textures/gui/suit_fabricator.png");

    private double scrollOffset = 0;
    private SuitType selectedSuit = null;
    
    private final int listX = 14;
    private final int listY = 14;
    private final int listWidth = 108;
    private final int listHeight = 70;
    private final int itemHeight = 12;

    public SpeedForceWorkbenchScreen(SpeedForceWorkbenchMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 200;
        this.inventoryLabelY = 106;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        clearWidgets();
    }

    private String getSuitDisplayName(SuitType suit) {
        return switch (suit) {
            case FLASH -> "Flash";
            case REVERSE_FLASH -> "Reverse";
            case ZOOM -> "Zoom";
            case FLASH_S4 -> "Flash S4";
            case FLASH_S5 -> "Flash S5";
            case KID_FLASH -> "Kid Flash";
            case GREEN_ARROW -> "Arrow";
            default -> suit.name();
        };
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        SuitType[] suits = SuitType.values();
        int maxScroll = Math.max(0, suits.length * itemHeight - listHeight);
        this.scrollOffset = Mth.clamp(this.scrollOffset - scrollY * itemHeight, 0, maxScroll);
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            double relX = mouseX - this.leftPos;
            double relY = mouseY - this.topPos;

            if (relX >= listX && relX <= listX + listWidth && relY >= listY && relY <= listY + listHeight) {
                int clickedIndex = (int) ((relY - listY + scrollOffset) / itemHeight);
                if (clickedIndex >= 0 && clickedIndex < SuitType.values().length) {
                    selectedSuit = SuitType.values()[clickedIndex];
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type) {
        if (slot != null && slot.index >= 0 && slot.index < 4) {
            if (selectedSuit != null) {
                PacketDistributor.sendToServer(new WorkbenchPurchasePayload(selectedSuit, slot.index));
            }
            return;
        }
        super.slotClicked(slot, slotId, mouseButton, type);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, 5, 0x404040, false);
        
        int totalCost = selectedSuit != null ? SuitPriceRegistry.getPrice(selectedSuit) : 0;
        String costStr = String.valueOf(totalCost);
        int costWidth = this.font.width(costStr);
        graphics.drawString(this.font, Component.literal(costStr), 162 - costWidth, 91, 0x404040, false);

        int materialValue = menu.getMaterialValue();
        String valStr = String.valueOf(materialValue);
        int valWidth = this.font.width(valStr);
        int valColor = (selectedSuit != null && materialValue >= totalCost) ? 0x55FF55 : 0xFF5555;
        if (selectedSuit == null) valColor = 0xFFFFFF;
        
        graphics.drawString(this.font, Component.literal(valStr).withColor(valColor), 162 - valWidth, 103, 0xFFFFFF, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        
        graphics.fill(this.leftPos + listX, this.topPos + listY, this.leftPos + listX + listWidth, this.topPos + listY + listHeight, 0xFF000000);
        
        graphics.enableScissor(this.leftPos + listX, this.topPos + listY, this.leftPos + listX + listWidth, this.topPos + listY + listHeight);
        
        SuitType[] suits = SuitType.values();
        for (int i = 0; i < suits.length; i++) {
            int yPos = this.topPos + listY + (i * itemHeight) - (int) scrollOffset;
            if (yPos + itemHeight > this.topPos + listY && yPos < this.topPos + listY + listHeight) {
                String text = getSuitDisplayName(suits[i]);
                if (suits[i] == selectedSuit) {
                    graphics.fill(this.leftPos + listX, yPos, this.leftPos + listX + listWidth, yPos + itemHeight, 0xFF000000);
                    graphics.fill(this.leftPos + listX, yPos, this.leftPos + listX + listWidth, yPos + 1, 0xFFFFFFFF);
                    graphics.fill(this.leftPos + listX, yPos + itemHeight - 1, this.leftPos + listX + listWidth, yPos + itemHeight, 0xFFFFFFFF);
                    graphics.fill(this.leftPos + listX, yPos, this.leftPos + listX + 1, yPos + itemHeight, 0xFFFFFFFF);
                    graphics.fill(this.leftPos + listX + listWidth - 1, yPos, this.leftPos + listX + listWidth, yPos + itemHeight, 0xFFFFFFFF);
                    graphics.drawString(this.font, Component.literal(text), this.leftPos + listX + 4, yPos + 2, 0xFFFFFF, false);
                } else {
                    graphics.drawString(this.font, Component.literal(text), this.leftPos + listX + 4, yPos + 2, 0xAAAAAA, false);
                }
            }
        }
        graphics.disableScissor();

        if (selectedSuit != null) {
            for (int i = 0; i < 4; i++) {
                ItemStack piece = SuitPriceRegistry.getSuitPiece(selectedSuit, i);
                if (piece != null && !piece.isEmpty()) {
                    int slotX = this.leftPos + 145;
                    int slotY = this.topPos + 19 + i * 18;
                    graphics.renderItem(piece, slotX, slotY);
                }
            }
        }

        this.renderTooltip(graphics, mouseX, mouseY);

        if (selectedSuit != null) {
            for (int i = 0; i < 4; i++) {
                int slotX = this.leftPos + 145;
                int slotY = this.topPos + 19 + i * 18;
                if (mouseX >= slotX && mouseX < slotX + 16 && mouseY >= slotY && mouseY < slotY + 16) {
                    int price = SuitPriceRegistry.getPiecePrice(selectedSuit, i);
                    int tooltipColor = menu.getMaterialValue() >= price ? 0x55FF55 : 0xFF5555;
                    Component costText = Component.literal("Cost: " + price).withColor(tooltipColor);
                    graphics.renderTooltip(this.font, costText, mouseX, mouseY);
                }
            }
        }
    }
}
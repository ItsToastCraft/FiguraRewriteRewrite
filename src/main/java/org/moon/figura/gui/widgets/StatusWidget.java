package org.moon.figura.gui.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import org.moon.figura.utils.FiguraText;
import org.moon.figura.utils.TextUtils;
import org.moon.figura.utils.ui.UIHelper;

import java.util.List;

public class StatusWidget implements FiguraWidget, FiguraTickable, GuiEventListener {

    public static final String STATUS_INDICATORS = "-*/+";
    public static final List<String> STATUS_NAMES = List.of("model", "texture", "script", "backend");
    public static final List<Style> TEXT_COLORS = List.of(
            Style.EMPTY.withColor(ChatFormatting.WHITE),
            Style.EMPTY.withColor(ChatFormatting.RED),
            Style.EMPTY.withColor(ChatFormatting.YELLOW),
            Style.EMPTY.withColor(ChatFormatting.GREEN)
    );

    private final Font font;
    private byte status = 0;
    private Component disconnectedReason;

    public int x, y;
    public int width, height;
    private boolean visible = true;

    public StatusWidget(int x, int y, int width) {
        this.x = x;
        this.y = y;
        this.font = Minecraft.getInstance().font;
        this.width = width;
        this.height = font.lineHeight + 5;
    }

    @Override
    public void tick() {
        if (!visible) return;

        //update status indicators
        int model = 2;
        status = (byte) model;

        int texture = 0;
        status += (byte) (texture << 2);

        int script = 3;
        status += (byte) (script << 4);

        int backend = 1;
        status += (byte) (backend << 6);
        if (backend != 3) disconnectedReason = new TextComponent("your mom!");
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float delta) {
        if (!visible) return;

        //background
        UIHelper.renderSliced(stack, x, y, width, height, UIHelper.OUTLINE);

        //text
        float size = (width - 44) / 3f + 10;
        for (int i = 0; i < 4; i++) {
            Component text = getStatus(i);
            UIHelper.drawString(stack, font, text, (int) (x + size * i + 2), y + 3, 0xFFFFFF);
        }

        //mouse over
        this.isMouseOver(mouseX, mouseY);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (!UIHelper.isMouseOver(x, y, width, height, mouseX, mouseY))
            return false;

        //get status text tooltip
        MutableComponent text = null;
        String part = "gui.status.";

        float size = (width - 44) / 3f + 10;
        for (int i = 0; i < 4; i++) {
            double x = this.x + 7 + size * i - size / 2f; //x + 2 spacing + 5 half icon + size - half size
            if (mouseX >= x && mouseX <= x + size) {
                //get name and color
                int color = status >> (i * 2) & 3;
                text = new FiguraText(part += STATUS_NAMES.get(i)).append("\n• ").append(new FiguraText(part + "." + color)).setStyle(TEXT_COLORS.get(color));

                //get backend disconnect reason
                if (i == 3 && disconnectedReason != null)
                    text.append("\n").append("\n").append(new FiguraText(part + ".reason")).append("\n• ").append(disconnectedReason);

                break;
            }
        }

        //set tooltip
        UIHelper.setTooltip(text);

        return true;
    }

    private MutableComponent getStatus(int type) {
        return new TextComponent(String.valueOf(STATUS_INDICATORS.charAt(status >> (type * 2) & 3))).setStyle(Style.EMPTY.withFont(TextUtils.FIGURA_FONT));
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}

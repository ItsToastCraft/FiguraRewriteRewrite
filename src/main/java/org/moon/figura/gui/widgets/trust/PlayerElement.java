package org.moon.figura.gui.widgets.trust;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import org.moon.figura.gui.widgets.ContextMenu;
import org.moon.figura.gui.widgets.lists.PlayerList;
import org.moon.figura.trust.TrustContainer;
import org.moon.figura.trust.TrustManager;
import org.moon.figura.utils.FiguraIdentifier;
import org.moon.figura.utils.FiguraText;
import org.moon.figura.utils.ui.UIHelper;

import java.util.ArrayList;

public class PlayerElement extends AbstractTrustElement {

    private final String name;
    private final ResourceLocation skin;
    private final ContextMenu context;

    private static final ResourceLocation BACKGROUND = new FiguraIdentifier("textures/gui/player_trust.png");

    public PlayerElement(String name, TrustContainer trust, ResourceLocation skin, PlayerList parent) {
        super(40, trust, parent);
        this.name = name;
        this.skin = skin;
        this.context = new ContextMenu(this);

        generateContext();
    }

    private void generateContext() {
        //header
        context.addDivisor(new FiguraText("gui.set_trust"));

        //actions
        ArrayList<ResourceLocation> groupList = new ArrayList<>(TrustManager.GROUPS.keySet());
        for (int i = 0; i < (TrustManager.isLocal(trust) ? groupList.size() : groupList.size() - 1); i++) {
            ResourceLocation parentID = groupList.get(i);
            TrustContainer container = TrustManager.get(parentID);
            context.addAction(container.getGroupName().copy().setStyle(Style.EMPTY.withColor(container.getGroupColor())), button -> {
                trust.setParent(parentID);
                if (parent.getSelectedEntry() == this)
                    parent.parent.updateTrustData(trust);
            });
        }
    }

    @Override
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float delta) {
        stack.pushPose();
        stack.translate(x + width / 2f, y + height / 2f, 100);
        stack.scale(scale, scale, scale);

        animate(mouseX, mouseY, delta);

        //fix x, y
        int x = -width / 2;
        int y = -height / 2;

        //selected overlay
        if (this.parent.getSelectedEntry() == this) {
            UIHelper.fillRounded(stack, x - 1, y - 1, width + 2, height + 2, 0xFFFFFFFF);
        }

        //background
        RenderSystem.setShaderTexture(0, BACKGROUND);
        blit(stack, x, y, width, height, 0f, 0f, 174, 40, 174, 40);

        //head
        RenderSystem.setShaderTexture(0, this.skin);
        blit(stack, x + 4, y + 4, 32, 32, 8f, 8f, 8, 8, 64, 64);

        //hat
        RenderSystem.enableBlend();
        blit(stack, x + 4, y + 4, 32, 32, 40f, 8f, 8, 8, 64, 64);
        RenderSystem.disableBlend();

        //name
        Font font = Minecraft.getInstance().font;
        UIHelper.renderOutlineText(stack, font, new TextComponent(this.name), x + 40, y + 4, 0xFFFFFF, 0);

        //uuid
        stack.pushPose();
        stack.translate(x + 40, y + 4 + font.lineHeight, 0f);
        stack.scale(0.5f, 0.5f, 0.5f);
        drawString(stack, font, new TextComponent(trust.name), 0, 0, 0x888888);
        stack.popPose();

        //trust
        drawString(stack, font, trust.getGroupName(), x + 40, y + height - font.lineHeight - 4, trust.getGroupColor());

        stack.popPose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isMouseOver(mouseX, mouseY))
            return false;

        //context menu on right click
        if (button == 1) {
            context.setPos((int) mouseX, (int) mouseY);
            context.setVisible(true);
            UIHelper.setContext(context);
            return true;
        }
        //hide old context menu
        else if (UIHelper.getContext() == context) {
            context.setVisible(false);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public String getName() {
        return name;
    }
}
package band.kessokuteatime.knowledges.config.modmenu.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import me.shedaniel.clothconfig2.gui.ClothConfigTabButton;
import band.kessokuteatime.knowledges.mixin.client.clothconfig.ClothConfigTabButtonAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.Optional;
import java.util.function.Supplier;

public class ClothConfigTabButtonSeparator extends ClothConfigTabButton {
    public ClothConfigTabButtonSeparator(ClothConfigScreen screen, int index, int int_1, int int_2, int int_3, int int_4, Text string_1, Supplier<Optional<StringVisitable[]>> descriptionSupplier) {
        super(screen, index, int_1, int_2, int_3, int_4, string_1, descriptionSupplier);
    }

    public ClothConfigTabButtonSeparator(ClothConfigScreen screen, int index, int int_1, int int_2, int int_3, int int_4, Text string_1) {
        super(screen, index, int_1, int_2, int_3, int_4, string_1);
    }

    @Override
    public void onPress() {
        // Do nothing
    }

    @Override
    protected boolean clicked(double double_1, double double_2) {
        return false;
    }

    @Override
    public boolean isMouseOver(double double_1, double double_2) {
        return false;
    }

    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float delta) {
        this.active = false;
        renderButton(graphics, mouseX, mouseY, delta);
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        context.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawMessage(context, minecraftClient.textRenderer, 0xFFFFFF | MathHelper.ceil(this.alpha * 255.0F) << 24);
    }
}

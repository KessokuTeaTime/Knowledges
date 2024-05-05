package band.kessokuteatime.knowledges.api.proxy;

import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.render.base.Renderable;
import net.krlite.equator.render.renderer.Flat;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class RenderProxy {
    private final DrawContext context;

    public RenderProxy(DrawContext context) {
        this.context = context;
    }

    protected DrawContext context() {
        return context;
    }

    public void withMatrix(Consumer<MatrixStack> matrixStackConsumer) {
        matrixStackConsumer.accept(context().getMatrices());
    }

    public void pushMatrix() {
        context().getMatrices().push();
    }

    public void popMatrix() {
        context().getMatrices().pop();
    }

    public boolean isMatrixEmpty() {
        return context().getMatrices().isEmpty();
    }

    public void draw(Box box, Function<Flat, Renderable> function) {
        box.render(context(), function);
    }

    public void drawAroundCursor(UnaryOperator<Box> modifier, Function<Flat, Renderable> function) {
        modifier.apply(LayoutProxy.crosshairSafeArea()).render(context(), function);
    }

    public void drawAroundCursor(Function<Flat, Renderable> function) {
        drawAroundCursor(UnaryOperator.identity(), function);
    }

    public void drawInScreen(UnaryOperator<Box> modifier, Function<Flat, Renderable> function) {
        modifier.apply(LayoutProxy.screen()).render(context(), function);
    }

    public void drawInScreen(Function<Flat, Renderable> function) {
        drawInScreen(UnaryOperator.identity(), function);
    }
}

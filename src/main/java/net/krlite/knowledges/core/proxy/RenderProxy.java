package net.krlite.knowledges.core.proxy;

import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.math.geometry.flat.Vector;
import net.krlite.equator.render.base.Renderable;
import net.krlite.equator.render.frame.FrameInfo;
import net.krlite.knowledges.KnowledgesClient;

import java.util.function.Function;
import java.util.function.Supplier;

public class RenderProxy {
    public static class LayoutProxy {
        public double scalar() {
            return 0.5 + 0.5 * KnowledgesClient.CONFIG.global.mainScalar / 1000.0;
        }

        public Box crosshairSafeArea() {
            double size = 16 + 8 * KnowledgesClient.CONFIG.global.crosshairSafeAreaScalar / 1000.0;
            return Box.UNIT.scale(size)
                    .scale(scalar())
                    .center(Vector.ZERO)
                    .shift(0, -1);
        }

        public Box screen() {
            return FrameInfo.scaled();
        }
    }

    public final LayoutProxy layout = new LayoutProxy();

    public void draw(Supplier<Renderable> renderableSupplier) {
        renderableSupplier.get().render();
    }

    public void drawAroundCursor(Function<Box, Renderable> renderableFunction) {
        draw(() -> renderableFunction.apply(layout.crosshairSafeArea()));
    }

    public void drawInScreen(Function<Box, Renderable> renderableFunction) {
        draw(() -> renderableFunction.apply(layout.screen()));
    }
}

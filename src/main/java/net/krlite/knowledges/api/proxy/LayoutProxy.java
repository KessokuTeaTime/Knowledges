package net.krlite.knowledges.api.proxy;

import net.krlite.equator.math.geometry.flat.Box;
import net.krlite.equator.math.geometry.flat.Vector;
import net.krlite.equator.render.frame.FrameInfo;
import net.krlite.knowledges.KnowledgesClient;

public class LayoutProxy {
    public static double scalar() {
        return 0.5 + 0.5 * KnowledgesClient.CONFIG.get().general.mainScalar;
    }

    public static Box crosshairSafeArea() {
        double size = 16 + 8 * KnowledgesClient.CONFIG.get().general.crosshairSafeAreaScalar;
        return Box.UNIT.scale(size)
                .scale(scalar())
                .center(Vector.ZERO)
                .shift(0, -1);
    }

    public static Box screen() {
        return FrameInfo.scaled();
    }
}

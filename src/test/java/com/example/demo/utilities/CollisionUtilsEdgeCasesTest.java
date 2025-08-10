package com.example.demo.utilities;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CollisionUtilsEdgeCasesTest {

    @Test
    void hitbox_clamps_negative_and_large_values() {
        Bounds base = box(0, 0, 10, 10);

        // negative shrinks clamp to 0 (no change)
        Bounds hNeg = CollisionUtils.hitbox(base, -5);
        assertBox(hNeg, 0, 0, 10, 10, 1e-9);

        // >0.49 clamps to 0.49
        Bounds hMax = CollisionUtils.hitbox(base, 0.9);
        // inset = 4.9 each side => width/height = 0.2, minX/minY = 4.9
        assertBox(hMax, 4.9, 4.9, 0.2, 0.2, 1e-9);
    }

    @Test
    void hitbox_never_produces_negative_dimensions() {
        double[] sizes = {0.1, 0.5, 1.0, 2.0};
        for (double s : sizes) {
            Bounds hb = CollisionUtils.hitbox(box(0, 0, s, s), 0.49);
            assertTrue(hb.getWidth() >= 0, "width non-negative");
            assertTrue(hb.getHeight() >= 0, "height non-negative");
        }
    }

    @Test
    void intersects_is_commutative_even_with_shrink() {
        Bounds a = box(0, 0, 10, 10);
        Bounds b = box(8.9, 0, 10, 10);
        boolean ab = CollisionUtils.intersects(a, 0.05, b, 0.05);
        boolean ba = CollisionUtils.intersects(b, 0.05, a, 0.05);
        assertEquals(ab, ba);

        Bounds c = box(0, 0, 10, 10);
        Bounds d = box(10, 0, 10, 10);
        boolean cd = CollisionUtils.intersects(c, 0.25, d, 0.25);
        boolean dc = CollisionUtils.intersects(d, 0.25, c, 0.25);
        assertEquals(cd, dc);
        assertFalse(cd);
    }

    // helpers
    private static Bounds box(double x, double y, double w, double h) {
        return new BoundingBox(x, y, w, h);
    }
    private static void assertBox(Bounds b, double x, double y, double w, double h, double eps) {
        assertAll(
                () -> assertEquals(x, b.getMinX(), eps, "minX"),
                () -> assertEquals(y, b.getMinY(), eps, "minY"),
                () -> assertEquals(w, b.getWidth(), eps, "width"),
                () -> assertEquals(h, b.getHeight(), eps, "height")
        );
    }
}

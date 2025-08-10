package com.example.demo.utilities;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CollisionUtilsTest {

    @Test
    @DisplayName("intersects: real overlap only (no shrink)")
    void intersects_noShrink() {
        // separated
        assertFalse(CollisionUtils.intersects(box(0,0,10,10), 0, box(20,0,10,10), 0));
        assertFalse(CollisionUtils.intersects(box(-15,-15,10,10), 0, box(0,0,10,10), 0));

        // touching edge & corner — flip to true if JavaFX counts touching as intersect on your setup
        assertTrue(CollisionUtils.intersects(box(0,0,10,10), 0, box(10,0,10,10), 0));
        assertTrue(CollisionUtils.intersects(box(0,0,10,10), 0, box(10,10,5,5), 0));

        // real overlaps
        assertTrue(CollisionUtils.intersects(box(0,0,10,10), 0, box(9,0,10,10), 0));
        assertTrue(CollisionUtils.intersects(box(0,0,10,10), 0, box(2,2,3,3), 0));
        assertTrue(CollisionUtils.intersects(box(0.5,0.5,5.2,5.2), 0, box(5.4,0.5,2,2), 0));
        assertTrue(CollisionUtils.intersects(box(-5,-5,10,10), 0, box(-1,-1,2,2), 0));
    }

    @Test
    @DisplayName("intersects: shrink factors reduce false positives")
    void intersects_withShrink() {
        assertFalse(CollisionUtils.intersects(box(0,0,10,10), 0.25, box(10,0,10,10), 0.25));
        assertFalse(CollisionUtils.intersects(box(0,0,10,10), 0.25, box(9.1,0,10,10), 0.25));
        assertTrue(CollisionUtils.intersects(box(0,0,10,10), 0.05, box(8.9,0,10,10), 0.05));
    }

    @Test
    @DisplayName("hitbox: clamps shrink to [0, 0.49] and computes correct box")
    void hitbox_clamps_and_sizes() {
        Bounds base = box(0, 0, 100, 50);

        Bounds hb0  = CollisionUtils.hitbox(base, -0.2);
        assertBox(hb0, 0, 0, 100, 50, 1e-9);

        Bounds hb25 = CollisionUtils.hitbox(base, 0.25);
        assertBox(hb25, 25, 12.5, 50, 25, 1e-9);

        Bounds hb60 = CollisionUtils.hitbox(base, 0.60);
        assertBox(hb60, 49, 24.5, 2, 1, 1e-9);
    }

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

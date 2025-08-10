package com.example.demo.utilities;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CollisionUtilsContactTest {

    private static Bounds box(double x, double y, double w, double h) {
        return new BoundingBox(x, y, w, h);
    }

    @Test
    @DisplayName("intersects: touching edges counts as collision")
    void intersects_touchingEdges_isTrue() {
        Bounds a = box(0, 0, 10, 10);
        Bounds rightTouch  = box(10, 0, 10, 10);
        Bounds bottomTouch = box(0, 10, 10, 10);
        assertTrue(CollisionUtils.intersects(a, 0, rightTouch, 0));
        assertTrue(CollisionUtils.intersects(a, 0, bottomTouch, 0));
    }

    @Test
    @DisplayName("intersects: touching only at a corner counts as collision")
    void intersects_touchingCorner_isTrue() {
        Bounds a = box(0, 0, 10, 10);
        Bounds cornerTouch = box(10, 10, 10, 10);
        assertTrue(CollisionUtils.intersects(a, 0, cornerTouch, 0));
    }

    @Test
    @DisplayName("intersects: real overlap stays with small shrink, disappears with larger shrink")
    void intersects_smallOverlap_then_eliminated_by_shrink() {
        Bounds a = box(0, 0, 10, 10);
        Bounds b = box(9.0, 0, 10, 10); // 1.0 overlap in X
        assertTrue(CollisionUtils.intersects(a, 0, b, 0));        // clear overlap
        assertTrue(CollisionUtils.intersects(a, 0.04, b, 0.04));  // small shrink -> still overlap
        assertFalse(CollisionUtils.intersects(a, 0.20, b, 0.20)); // bigger shrink -> no overlap
    }

    @Test
    @DisplayName("intersects: containment remains true with small shrink")
    void intersects_containment_true_with_small_shrink() {
        Bounds outer = box(0, 0, 20, 20);
        Bounds inner = box(3, 3, 5, 5);
        assertTrue(CollisionUtils.intersects(outer, 0, inner, 0));
        assertTrue(CollisionUtils.intersects(outer, 0.2, inner, 0.2));
    }
}

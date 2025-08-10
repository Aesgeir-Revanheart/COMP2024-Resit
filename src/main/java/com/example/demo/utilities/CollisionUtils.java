package com.example.demo.utilities;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;

public final class CollisionUtils {
    private CollisionUtils() {}

    public static BoundingBox hitbox(Bounds b, double shrinkFactor) {
        if (shrinkFactor < 0) shrinkFactor = 0;
        if (shrinkFactor >= 0.49) shrinkFactor = 0.49;
        double insetX = b.getWidth() * shrinkFactor;
        double insetY = b.getHeight() * shrinkFactor;
        return new BoundingBox(
                b.getMinX() + insetX, b.getMinY() + insetY,
                Math.max(0, b.getWidth() - 2*insetX),
                Math.max(0, b.getHeight() - 2*insetY)
        );
    }

    public static boolean intersects(Bounds a, double shrinkA, Bounds b, double shrinkB) {
        return hitbox(a, shrinkA).intersects(hitbox(b, shrinkB));
    }
}

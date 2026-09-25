package com.codemorph.backend.Util;


public final class NormalizationUtil {

    private NormalizationUtil() {
    }

    /**
     * Min-max normalization to 0-100.
     */
    public static double minMax(
            double value,
            double min,
            double max) {

        if (max == min) {
            return 0.0;
        }

        double normalized =
                ((value - min) / (max - min)) * 100.0;

        return clamp(normalized);
    }

    public static double clamp(double value) {
        return Math.max(0.0, Math.min(100.0, value));
    }
}

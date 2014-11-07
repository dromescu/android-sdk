package io.relayr.model;

public class LightColorProx {
    public long ts;     //"ts":1400776389653,
    public long light;  //"light":65535,                           //format: 16 bit unsigned, range: 0-65535
    public Color clr;   //"clr":{"r":65535,"g":65535,"b":65535}, //format: 16 bit unsigned, range: 0-65535
    public long prox;   //"prox":65535

    public static class Color {
        public int r;
        public int g;
        public int b;

        /**
         * Return a color-int from red, green, blue components.
         * The alpha component is implicitly 255 (fully opaque).
         * These component values should be [0..255], but there is no
         * range check performed, so if they are out of range, the
         * returned color is undefined.
         *
         * <pre> {@code
         *
         * int rgb = color.toRgb();
         * Bitmap image = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
         * image.eraseColor(c);
         *
         * } </pre>
         */
        public int toRgb() {
            float rr = r;
            float gg = g;
            float bb = b;

            //relative correction
            rr *= 2.0/3.0;

            //normalize
            float max = Math.max(rr, Math.max(gg, bb));
            if (max == 0) max = 1;
            rr = (rr/max) * 255;
            gg = (gg/max) * 255;
            bb = (bb/max) * 255;

            return android.graphics.Color.rgb((int) rr, (int) gg, (int) bb);
        }

    }

}

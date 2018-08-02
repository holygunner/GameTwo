package com.holygunner.game_two.values;

import android.graphics.Color;

/**
 * Created by Holygunner on 21.07.2018.
 */

public class ColorValues {
    public static final class FigureColors{
        public static final int BORDO = Color.argb(255, 127, 0, 0); // 7F0000
        public static final int PURPLE = Color.argb(255, 68, 0, 127); // 440089

        public static int[] getColors(){
            return new int[]{BORDO, PURPLE};
        }
    }

    public static final class FillColors{
//        public static final int CURRENT_FIGURE_FILL = Color.argb(255, 215, 218, 186);
        public static final int CURRENT_FIGURE_FILL = Color.argb(155, 0, 247, 16);
    }

    public static final class FigureRes{

    }


}

package com.holygunner.game_two.database;

/**
 * Created by Holygunner on 16.07.2018.
 */

public class FigureDbSchema {
    public static final class FigureTable{
        public static final String NAME = "figures";

        public static final class Cols{
            public static final String UUID = "uuid";
            public static final String FIGURE_TYPE = "figure_type";
            public static final String POSITION = "position";
            public static final String COLOR = "color";
            public static final String CELL_X = "cell_x";
            public static final String CELL_Y = "cell_y";
            public static final String STEP_LIMIT = "step_limit";
        }
    }
}

package com.holygunner.game_two.values;

import com.holygunner.game_two.figures.FigureClassAndColorPair;
import com.holygunner.game_two.figures.SemiCircle;
import com.holygunner.game_two.figures.*;

import static com.holygunner.game_two.values.ColorsValues.FigureColors.BORDO;
import static com.holygunner.game_two.values.ColorsValues.FigureColors.PURPLE;

public abstract class LevelsValues {
    public static final String[] LEVELS_NAMES = {"Level 1", "Level 2", "Level 3", "Level 4", "Endless Mode"};
    public static final int[] LEVELS_ROUNDS = {10, 50, 100, 200, 10000000};
//public static final int[] LEVELS_ROUNDS = {4, 2, 2, 2, 4};
    public static final int[][] DESKS_SIZES = {{3, 3}, {3, 3}, {4, 3}, {4, 4}, {4, 4}}; // DESKS_SIZES[0] is height, DESKS_SIZES[1] is width
//    public static final int[][] DESKS_SIZES = {{2, 2}, {2, 2}, {2, 2}, {2, 2}, {2, 2}}; // DEMO FOR SCREENSHOTS
    public static final int[] ADDS_FOR_STEP = {2, 3, 3, 4, 5};
    public static final int[] ADDS_FOR_TURN = {0, 1, 1, 1, 1}; // max value is 1
    public static final int[] ADDS_FOR_UNIT = {1, 1, 1, 1, 1}; // max value is 1

    public static final FigureClassAndColorPair[][] FIGURE_COLORS_PAIR = {
            {new FigureClassAndColorPair(SemiCircle.class, BORDO)},

            {new FigureClassAndColorPair(SemiSquare.class, PURPLE)},

            {new FigureClassAndColorPair(SemiCircle.class, PURPLE),
                    new FigureClassAndColorPair(SemiSquare.class, BORDO)},

            {new FigureClassAndColorPair(SemiCircle.class, BORDO),
                    new FigureClassAndColorPair(SemiCircle.class, PURPLE),
                    new FigureClassAndColorPair(SemiSquare.class, BORDO)},

            {new FigureClassAndColorPair(SemiCircle.class, BORDO),
                    new FigureClassAndColorPair(SemiCircle.class, PURPLE),
                    new FigureClassAndColorPair(SemiSquare.class, BORDO),
                    new FigureClassAndColorPair(SemiSquare.class, PURPLE)}
    };

    public static boolean isEndlessMode(int levelNumb){
        if (levelNumb >= LevelsValues.LEVELS_NAMES.length - 1){
            return true;
        }   else {
            return false;
        }
    }
}

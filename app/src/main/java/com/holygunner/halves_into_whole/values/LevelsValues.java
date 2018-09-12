package com.holygunner.halves_into_whole.values;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.holygunner.halves_into_whole.R;
import com.holygunner.halves_into_whole.figures.FigureClassAndColorPair;
import com.holygunner.halves_into_whole.figures.SemiCircle;
import com.holygunner.halves_into_whole.figures.*;

import static com.holygunner.halves_into_whole.values.ColorsValues.FigureColors.*;

public abstract class LevelsValues {
    public static final int[] LEVEL_NUMB = {0, 1, 2, 3, 4, 5, 6};
//    public static final int[] LEVELS_ROUNDS = {50, 75, 100, 125, 150, 175, 10000000};
    public static final int[] LEVELS_ROUNDS = {50, 50, 50, 50, 50, 50, 10000000};
    public static final int[][] DESKS_SIZES
            = {{3, 3}, {3, 3}, {3, 3}, {4, 3}, {4, 4}, {5, 4}, {5, 5}}; // DESKS_SIZES[0] is height, DESKS_SIZES[1] is width
    public static final int[] ADDS_FOR_STEP = {1, 2, 2, 3, 3, 4, 4};
    public static final int[] ADDS_FOR_TURN = {1, 1, 1, 1, 1, 1, 1}; // optimal value is 1
    public static final int[] ADDS_FOR_UNIT = {1, 1, 1, 1, 1, 1, 1}; // optimal value is 1

    public static final FigureClassAndColorPair[][] FIGURE_COLORS_PAIR = {
            {new FigureClassAndColorPair(SemiCircle.class, BORDO)}, // 1st

            {new FigureClassAndColorPair(SemiCircle.class, PURPLE), // 2nd
                    new FigureClassAndColorPair(SemiSquare.class, BORDO)},

            {new FigureClassAndColorPair(SemiSquare.class, GREEN), // 3rd
                    new FigureClassAndColorPair(SemiSquare.class, PURPLE),
                    new FigureClassAndColorPair(SemiSquare.class, SALMON)},

            {new FigureClassAndColorPair(SemiCircle.class, GREEN), // 4th
                    new FigureClassAndColorPair(SemiSquare.class, SALMON),
                    new FigureClassAndColorPair(SemiStar.class, BORDO)},

            {new FigureClassAndColorPair(SemiStar.class, PURPLE), // 5th
                    new FigureClassAndColorPair(SemiSquare.class, SALMON),
                    new FigureClassAndColorPair(SemiCircle.class, LILAC),
                    new FigureClassAndColorPair(SemiCircle.class, GREEN)},

            {new FigureClassAndColorPair(SemiStar.class, PURPLE), // 6th
                    new FigureClassAndColorPair(SemiSquare.class, GREEN),
                    new FigureClassAndColorPair(SemiSquare.class, SALMON),
                    new FigureClassAndColorPair(SemiCircle.class, LILAC),
                    new FigureClassAndColorPair(SemiCircle.class, GREEN)},

            {new FigureClassAndColorPair(SemiCircle.class, BORDO), // 7th
                    new FigureClassAndColorPair(SemiCircle.class, PURPLE),
                    new FigureClassAndColorPair(SemiSquare.class, BORDO),
                    new FigureClassAndColorPair(SemiSquare.class, PURPLE),
                    new FigureClassAndColorPair(SemiSquare.class, GREEN),
                    new FigureClassAndColorPair(SemiStar.class, BORDO),
                    new FigureClassAndColorPair(SemiStar.class, SALMON)}
    };

    public static boolean isEndlessMode(int levelNumb){
        return levelNumb < LevelsValues.LEVEL_NUMB.length - 1;
    }

    @NonNull
    public static String getLevelName(Context context, int levelNumb){
        Resources res = context.getResources();
        String[] names =  res.getStringArray(R.array.levels_names);
        return names[levelNumb];
    }

    @NonNull
    public static String[] getLevelsNames(Context context){
        Resources res = context.getResources();
        return res.getStringArray(R.array.levels_names);
    }
}

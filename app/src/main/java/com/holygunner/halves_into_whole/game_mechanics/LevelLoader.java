package com.holygunner.halves_into_whole.game_mechanics;

import android.content.Context;

import com.holygunner.halves_into_whole.values.LevelsValues;

abstract class LevelLoader {
    static Level loadLevel(Context context, int recentLevelNumb){
        Level level = new Level();
        level.setLevelNumb(recentLevelNumb);
        setValues(context, level, recentLevelNumb);
        return level;
    }

    private static void setValues(Context context, Level level, int levelNumb){
        level.setLevelName(LevelsValues.getLevelName(context, levelNumb));
        level.setLevelRounds(LevelsValues.LEVELS_ROUNDS[levelNumb]);
        level.setDeskSize(LevelsValues.DESKS_SIZES[levelNumb]);
        level.setAddForStep(LevelsValues.ADDS_FOR_STEP[levelNumb]);
        level.setAddForTurn(LevelsValues.ADDS_FOR_TURN[levelNumb]);
        level.setAddForUnit(LevelsValues.ADDS_FOR_UNIT[levelNumb]);
        level.setFigureClassAndColorPairs(LevelsValues.FIGURE_COLORS_PAIR[levelNumb]);
    }
}

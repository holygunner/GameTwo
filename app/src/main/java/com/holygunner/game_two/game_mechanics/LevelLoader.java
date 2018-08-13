package com.holygunner.game_two.game_mechanics;

import com.holygunner.game_two.values.LevelsValues;

public abstract class LevelLoader {

    public static Level loadLevel(int levelNumb){
        Level level = new Level();
        level.setLevelNumb(levelNumb);
        setValues(level, levelNumb);
        return level;
    }

    private static void setValues(Level level, int levelNumb){
        level.setLevelName(LevelsValues.LEVELS_NAMES[levelNumb]);
        level.setLevelRounds(LevelsValues.LEVELS_ROUNDS[levelNumb]);
        level.setDeskSize(LevelsValues.DESKS_SIZES[levelNumb]);
        level.setAddForStep(LevelsValues.ADDS_FOR_STEP[levelNumb]);
        level.setAddForTurn(LevelsValues.ADDS_FOR_TURN[levelNumb]);
        level.setAddForUnit(LevelsValues.ADDS_FOR_UNIT[levelNumb]);
        level.setFigureClassAndColorPairs(LevelsValues.FIGURE_COLORS_PAIR[levelNumb]);
    }
}

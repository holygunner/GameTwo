package com.holygunner.game_two.game_mechanics;

import com.holygunner.game_two.figures.FigureClassAndColorPair;
import com.holygunner.game_two.values.LevelsValues;

public class Level {
    public static final int BONUS = 10;
    private int mLevelNumb;
    private int mGamerCount;
    private String mLevelName;
    private int mLevelRounds; // required steps to finish level
    private int[] mDeskSize; // width & height of the desk
    private int mAddForStep; // quantity Of Rand Figures For Empty Step (2-4(?))
    private int mAddForTurn; // quantity Of Rand Figures For Empty Turn (0-1)
    private int mAddForUnit; // quantity Of Rand Figures For Empty Turn (0-1)
    private FigureClassAndColorPair[] mFigureClassAndColorPairs; // available figures and their colors on a level

    public Level(int levelNumb){
        mLevelNumb = levelNumb;
        setValues(levelNumb);
    }

    private void setValues(int levelNumb){
        mLevelName = LevelsValues.LEVELS_NAMES[levelNumb];
        mLevelRounds = LevelsValues.LEVELS_ROUNDS[levelNumb];
        mDeskSize = LevelsValues.DESKS_SIZES[levelNumb];
        mAddForStep = LevelsValues.ADDS_FOR_STEP[levelNumb];
        mAddForTurn = LevelsValues.ADDS_FOR_TURN[levelNumb];
        mAddForUnit = LevelsValues.ADDS_FOR_UNIT[levelNumb];
        mFigureClassAndColorPairs = LevelsValues.FIGURE_COLORS_PAIR[levelNumb];
    }

    public int getLevelNumb() {
        return mLevelNumb;
    }

    public int getLevelRounds() {
        return mLevelRounds;
    }

    public int[] getDeskSize() {
        return mDeskSize;
    }

    public int getAddForStep() {
        return mAddForStep;
    }

    public int getAddForTurn() {
        return mAddForTurn;
    }

    public int getAddForUnit() {
        return mAddForUnit;
    }

    public FigureClassAndColorPair[] getFigureClassesAndColorPairs() {
        return mFigureClassAndColorPairs;
    }

    public String getLevelName() {
        return mLevelName;
    }

    public int getGamerCount() {
        return mGamerCount;
    }

    public void setGamerCount(int gamerCount) {
        mGamerCount = gamerCount;
    }

    public void increaseGamerCount(){
        ++mGamerCount;
    }

    public void addBonus(){
        mGamerCount += BONUS;
    }

    public boolean isLevelComplete(){
        if (mGamerCount >= mLevelRounds){
            return true;
        }   else {
            return false;
        }
    }

    public static boolean isEndlessMode(int levelNumb){
        if (levelNumb >= LevelsValues.LEVELS_NAMES.length - 1){
            return true;
        }   else {
            return false;
        }
    }
}

package com.holygunner.halves_into_whole.game_mechanics;

import com.holygunner.halves_into_whole.figures.FigureClassAndColorPair;
import com.holygunner.halves_into_whole.values.LevelsValues;

public class Level {
    private int mLevelNumb;
    private int mGamerCount;
    private String mLevelName;
    private int mLevelRounds; // required steps to finish level
    private int[] mDeskSize; // width & height of the desk
    private int mAddForStep; // quantity Of Rand Figures For Empty Step (2-4(?))
    private int mAddForTurn; // quantity Of Rand Figures For Empty Turn (0-1)
    private int mAddForUnit; // quantity Of Rand Figures For Empty Turn (0-1)
    private FigureClassAndColorPair[] mFigureClassAndColorPairs; // available figures and their colors on a level

    Level(){}

    public int getLevelNumb() {
        return mLevelNumb;
    }

    void setLevelNumb(int levelNumb) {
        mLevelNumb = levelNumb;
    }

    public int getLevelRounds() {
        return mLevelRounds;
    }

    void setLevelRounds(int levelRounds){
        mLevelRounds = levelRounds;
    }

    public int[] getDeskSize() {
        return mDeskSize;
    }

    void setDeskSize(int[] deskSize){
        mDeskSize = deskSize;
    }

    int getAddForStep() {
        return mAddForStep;
    }

    void setAddForStep(int addForStep){
        mAddForStep = addForStep;
    }

    int getAddForTurn() {
        return mAddForTurn;
    }

    void setAddForTurn(int addForTurn){
        mAddForTurn = addForTurn;
    }

    int getAddForUnit() {
        return mAddForUnit;
    }

    void setAddForUnit(int addForUnit){
        mAddForUnit = addForUnit;
    }

    FigureClassAndColorPair[] getFigureClassesAndColorPairs() {
        return mFigureClassAndColorPairs;
    }

    void setFigureClassAndColorPairs(FigureClassAndColorPair[] pairs){
        mFigureClassAndColorPairs = pairs;
    }

    public String getLevelName() {
        return mLevelName;
    }

    void setLevelName(String levelName){
        mLevelName = levelName;
    }

    public int getGamerCount() {
        return mGamerCount;
    }

    void setGamerCount(int gamerCount) {
        mGamerCount = gamerCount;
    }

    void increaseGamerCount(int howMuch){
        int sum = mGamerCount + howMuch;
        int levelRounds = LevelsValues.LEVELS_ROUNDS[mLevelNumb];

        if (sum > levelRounds){
            mGamerCount = levelRounds;
        }   else {
            mGamerCount = sum;
        }
    }

    boolean isLevelComplete(){
        return mGamerCount >= mLevelRounds;
    }
}

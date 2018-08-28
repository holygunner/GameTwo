package com.holygunner.halves_into_whole.game_mechanics;

import com.holygunner.halves_into_whole.figures.FigureClassAndColorPair;
import com.holygunner.halves_into_whole.values.LevelsValues;

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

    public Level(){}

    public int getLevelNumb() {
        return mLevelNumb;
    }

    public void setLevelNumb(int levelNumb) {
        mLevelNumb = levelNumb;
    }

    public int getLevelRounds() {
        return mLevelRounds;
    }

    public void setLevelRounds(int levelRounds){
        mLevelRounds = levelRounds;
    }

    public int[] getDeskSize() {
        return mDeskSize;
    }

    public void setDeskSize(int[] deskSize){
        mDeskSize = deskSize;
    }

    public int getAddForStep() {
        return mAddForStep;
    }

    public void setAddForStep(int addForStep){
        mAddForStep = addForStep;
    }

    public int getAddForTurn() {
        return mAddForTurn;
    }

    public void setAddForTurn(int addForTurn){
        mAddForTurn = addForTurn;
    }

    public int getAddForUnit() {
        return mAddForUnit;
    }

    public void setAddForUnit(int addForUnit){
        mAddForUnit = addForUnit;
    }

    public FigureClassAndColorPair[] getFigureClassesAndColorPairs() {
        return mFigureClassAndColorPairs;
    }

    public void setFigureClassAndColorPairs(FigureClassAndColorPair[] pairs){
        mFigureClassAndColorPairs = pairs;
    }

    public String getLevelName() {
        return mLevelName;
    }

    public void setLevelName(String levelName){
        mLevelName = levelName;
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
}

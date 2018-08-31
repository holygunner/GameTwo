package com.holygunner.halves_into_whole.game_mechanics;

import com.holygunner.halves_into_whole.database.Saver;
import com.holygunner.halves_into_whole.figures.Figure;
import com.holygunner.halves_into_whole.figures.FigureFactory;
import com.holygunner.halves_into_whole.figures.Position;
import com.holygunner.halves_into_whole.values.LevelsValues;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import static com.holygunner.halves_into_whole.game_mechanics.StepResult.*;
import static com.holygunner.halves_into_whole.values.GameValues.*;

public class GamePlay {
    private Integer recentPosition;
    private Level mLevel;
    private int mLevelNumb;
    private Desk mDesk;
    private AvailableSteps mAvailableSteps;
    private RandomGenerator mRandomGenerator;
    private Saver mSaver;
    private List<Figure> mRecentRandomFigures;

    private int unitedFigureRes;
    private boolean isGameStarted;
    private boolean isTurnAvailable;
    private boolean isFilled;
    private StepResult prevStepResult;

    GamePlay(Context context, Desk desk, Saver saver, int levelNumb){
        mSaver = saver;
        mLevel = LevelLoader.loadLevel(levelNumb);
        mLevelNumb = mLevel.getLevelNumb();

        if (getLevelNumb() == Saver.readMaxLevel(context)){
            mLevel.setGamerCount(Saver.readMaxLevelAndCount(context)[1]);
        }   else {
            mLevel.setGamerCount(0);
        }

        mRandomGenerator = new RandomGenerator(mLevel);
        mDesk = desk;
        isGameStarted = Saver.isSaveExists(context);

        mRecentRandomFigures = new ArrayList<>();
    }

    public void setIsFilled(boolean isFilled){
        this.isFilled = isFilled;
    }

    public AvailableSteps getAvailableSteps(){
        return mAvailableSteps;
    }

    public List<Figure> getRecentRandomFigures(){
        return mRecentRandomFigures;
    }

    public Desk loadDesk(Figure[] figures){
        mDesk = new Desk(this, mLevel.getDeskSize());

        for (Figure figure : figures) {
            mDesk.addFigure(figure);
        }
        return mDesk;
    }

    public Desk createNewDesk() {
        mDesk = new Desk(this, mLevel.getDeskSize());
        addRandomFigure(3);
//        addRandomFigure(8); // for test
        return mDesk;
    }

//    public Desk createDemoDesk() { // custom desk for creating screenshots to About (1st idea)
//        // позже будет удалено
//        mDesk = new Desk(this, new int[]{2,2});
//        SemiCircle semiCircle1 = new SemiCircle(UUID.randomUUID(),
//                ColorsValues.FigureColors.PURPLE, Position.POSITION_FOUR, new Cell(0, 1));
//        SemiCircle semiCircle2 = new SemiCircle(UUID.randomUUID(),
//                ColorsValues.FigureColors.PURPLE, Position.POSITION_THREE, new Cell(1, 0));
//
//        SemiSquare semiSquare1 = new SemiSquare(UUID.randomUUID(),
//                ColorsValues.FigureColors.BORDO, Position.POSITION_ONE, new Cell(0, 0));
//        SemiSquare semiSquare2 = new SemiSquare(UUID.randomUUID(),
//                ColorsValues.FigureColors.BORDO, Position.POSITION_TWO, new Cell(1, 1));
//
//        mDesk.addFigure(semiCircle1);
//        mDesk.addFigure(semiCircle2);
//        mDesk.addFigure(semiSquare1);
//        mDesk.addFigure(semiSquare2);
//
//        mRecentRandomFigures.add(semiCircle1);
//        mRecentRandomFigures.add(semiCircle2);
//        mRecentRandomFigures.add(semiSquare1);
//        mRecentRandomFigures.add(semiSquare2);
//
//        return mDesk;
//    }

    public StepResult tryToStep(int position){
        mRecentRandomFigures.clear();
        if (!isGameContinue() || mAvailableSteps == null){
            return STEP_UNAVAILABLE;
        }

        if (mAvailableSteps.isPositionOnStep(position) != -1 && isFilled) {
            Cell fromWhere = mDesk.positionToCell(recentPosition);
            Cell toWhere = mDesk.positionToCell(position);

            StepResult stepResult = makeStep(fromWhere, toWhere);

            if (stepResult != UNITE_FIGURE){
                prevStepResult = null;
            }

            switch (stepResult){
                case REPLACE_FIGURE:
                    addRandomFigure(mLevel.getAddForStep());
                    break;
                case UNITE_FIGURE:
                    if (!isCombo()) {
                        addRandomFigure(mLevel.getAddForUnit());
                    }   else {
                        if (mDesk.isOneFigureLeft()){
                            addRandomFigure(1);
                        }
                        stepResult = COMBO;
                    }
                    prevStepResult = stepResult;
                    break;
                case DESK_EMPTY:
                    mLevel.increaseGamerCount(POINT_FOR_EMPTY_DESK);
                    if (!mLevel.isLevelComplete()) {
                        addRandomFigure(3);
                    }
                    break;
            }

            if (mLevel.isLevelComplete()){
                mSaver.writeSaveExists(false);
                return LEVEL_COMPLETE;
            }

            if (stepResult != STEP_UNAVAILABLE) {

                if (!isGameStarted){
                    isGameStarted = true;
                }
            }

            isFilled = false;
            return stepResult;
        }   else
            return STEP_UNAVAILABLE;
    }

    public boolean isGameContinue(){
        if (mDesk.getFreeCells().isEmpty()){
            mSaver.writeSaveExists(false);
            return false;
        }   else
            return true;
    }

    public Integer getRecentPosition(){
        return recentPosition;
    }

//    public boolean isGameContinue(){ // DEMO FOR SCREENSHOTS, DON'T FORGET TO DELETE
//        if (mDesk.getFreeCells().isEmpty()){
////            mSaver.writeGamerCount(mLevel.getGamerCount());
//            return true;
//        }   else
//            return true;
//    }

    public boolean setAvailableCells(int position){
        if (!isGameContinue()){
            return false;
        }

        if (mDesk.isCellEmpty(mDesk.positionToCell(position))){
            return false;
        }
        recentPosition = position;
        mAvailableSteps = new AvailableSteps(this, recentPosition, mDesk);
        mAvailableSteps = new AvailableSteps(this, position, mDesk);

        return true;
    }

    public boolean turnFigureIfExists(int position){
        if (isGameContinue()) {
            Cell cell = mDesk.positionToCell(position);
            Figure figure = mDesk.getFigure(cell);

            if (figure == null || !isFilled) {
                return false;
            } else {
                if (!isGameStarted){
                    isGameStarted = true;
                }
                figure.position = Position.getTurnedPosition(figure.position);
                prevStepResult = null;
                addRandomFigure(mLevel.getAddForTurn());
                mAvailableSteps = new AvailableSteps(this, recentPosition, mDesk);
                isFilled = true;

                return true;
            }
        }   else
            return false;
    }

    public Level getLevel(){
        return mLevel;
    }

    public Desk getDesk(){
        return mDesk;
    }

    private boolean areSemiFiguresAreWhole(Figure figure1, Figure figure2){ // are the both halves the whole figure
        Position position1 = figure1.position;
        Position position2 = figure2.position;
        int color1 = figure1.color;
        int color2 = figure2.color;

        return Position.areHalfesOfWhole(position1, position2) && (color1 == color2)
                && figure1.getClass() == figure2.getClass();
    }

    public int isStepAvailable(Figure figure, Cell toWhere){
        int isStepAvailable = -1;

        int x0 = figure.mCell.getX();
        int y0 = figure.mCell.getY();

        int xSpace = Math.abs(toWhere.getX() - x0);
        int ySpace = Math.abs(toWhere.getY() - y0);

        boolean isOnStepLimit = xSpace<= figure.stepLimit & ySpace<= figure.stepLimit;

        if (mDesk.getFigure(toWhere) != null){
            Figure figure2 = mDesk.getFigure(toWhere);
            if (isOnStepLimit && areSemiFiguresAreWhole(figure, figure2)) {
                isStepAvailable = 1;
            }
        }   else
        if (isOnStepLimit) {
            isStepAvailable = 0;
        }
        return isStepAvailable;
    }

    public int getLastUnitedFigureRes(){
        return unitedFigureRes;
    }

    public boolean isTurnAvailable() {
        return isTurnAvailable;
    }

    public void setTurnAvailable(boolean turnAvailable) {
        isTurnAvailable = turnAvailable;
    }

    private StepResult makeStep(Cell fromWhere, Cell toWhere){
        Figure ourFigure = mDesk.getFigure(fromWhere);

        if (isStepAvailable(ourFigure,toWhere) != -1){
            if (mDesk.isCellEmpty(toWhere)){
                mDesk.replaceFigure(ourFigure, toWhere);
                return REPLACE_FIGURE;

            }   else {
                Figure figure2 = mDesk.getFigure(toWhere);

                if (areSemiFiguresAreWhole(ourFigure, figure2)){
                    unitedFigureRes = figure2.fullPositionRes;
                    if (isCombo()){
                        mLevel.increaseGamerCount(POINT_FOR_COMBO);
                    }   else {
                        mLevel.increaseGamerCount(POINT_FOR_UNIT);
                    }
                    mDesk.uniteSemiFigures(fromWhere, toWhere);

                    if (mDesk.isDeskEmpty()){
                        return DESK_EMPTY;
                    }
                    return UNITE_FIGURE;
                }
            }
        }
        return STEP_UNAVAILABLE;
    }

    private boolean isCombo(){
        return prevStepResult != null;
    }

    private Figure getRandomFigure(){
        if (!mDesk.getFreeCells().isEmpty()) {
            Figure figure = FigureFactory.getRandomFigure(mDesk.getFreeCells(), mRandomGenerator);
            mDesk.addFigure(figure);
            return figure;
        }
        return null;
    }

    private void addRandomFigure(int howMuchFigures){
//        mRecentRandomFigures.clear();

        if (mLevel.isLevelComplete()){
            return;
        }

        for (int i = 0; i < howMuchFigures; i++){
            Figure figure = getRandomFigure();

            if (figure != null){
                mRecentRandomFigures.add(figure);
            }
        }
    }

    public int getLevelNumb() {
        return mLevelNumb;
    }

    public void increaseLevelNumb(){
        if (!LevelsValues.isEndlessMode(mLevelNumb)){
            mLevelNumb += 1;
        }   else {
            mLevelNumb = LevelsValues.LEVELS_NAMES.length - 1;
        }
    }
}

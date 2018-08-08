package com.holygunner.game_two.game_mechanics;

import android.content.Context;

import com.holygunner.game_two.database.Saver;
import com.holygunner.game_two.figures.Figure;
import com.holygunner.game_two.figures.FigureFactory;
import com.holygunner.game_two.figures.Position;
import com.holygunner.game_two.values.LevelsValues;

import java.util.ArrayList;
import java.util.List;

public class GamePlay {
    public Integer recentPosition;

    private Context mContext;
    private Level mLevel;
    private int mLevelNumb;
    private Desk mDesk;
    private AvailableSteps mAvailableSteps;
    private Randomer mRandomer;
    private Saver mSaver;
    private List<Figure> mRecentRandomFigures;

    private int unitedFigureRes;
    private boolean isGameStarted;
    private boolean isTurnAvailable;
    private boolean isFilled;

    public GamePlay(Context context, Desk desk, Saver saver){
        mContext = context.getApplicationContext();
        mSaver = saver;
        mLevel = new Level(Saver.readCurrentLevel(mContext));
        mLevelNumb = mLevel.getLevelNumb();
        mRandomer = new Randomer(mLevel);
        mDesk = desk;
        isGameStarted = Saver.isSaveExists(mContext);
        mLevel.setGamerCount(Saver.readGamerCount(context));
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

        for (int i = 0; i < figures.length; ++i){
            Figure figure = figures[i];
            mDesk.addFigure(figure);
        }
        return mDesk;
    }

    public Desk createNewDesk() {
        mDesk = new Desk(this, mLevel.getDeskSize());
        addRandomFigure(3);

        return mDesk;
    }

    public boolean isGameStarted() {
        return isGameStarted;
    }

    public int tryToStep(int position){
        if (!isGameContinue() || mAvailableSteps == null){
            return -1;
        }

        if (mAvailableSteps.isPositionOnStep(position) != -1 && isFilled==true) {
            Cell fromWhere = mDesk.positionToCell(recentPosition);
            Cell toWhere = mDesk.positionToCell(position);

            int stepResult = makeStep(fromWhere, toWhere);

            switch (stepResult){
                case 0:
                    addRandomFigure(mLevel.getAddForStep());
                    break;
                case 1:
                    addRandomFigure(mLevel.getAddForUnit());
                    break;
                case 2:
                    addRandomFigure(3);
                    mLevel.addBonus();
                    break;
            }

            if (mLevel.isLevelComplete()){
                return 3;
            }

            if (stepResult != -1) {
                if (isGameStarted() == false){
                    setGameStarted();
                }
            }
            isFilled = false;
            return stepResult;
        }   else
            return -1;
    }

    public boolean isGameContinue(){
        if (mDesk.getFreeCells().isEmpty()){
            mSaver.writeGamerCount(mLevel.getGamerCount());
            return false;
        }   else
            return true;
    }

    public boolean setAvailableCells(int position){
        if (!isGameContinue()){
            return false;
        }

        if (mDesk.isCellEmpty(mDesk.positionToCell(position))){
            return false;
        }
        recentPosition = position;
        mAvailableSteps = new AvailableSteps(this, recentPosition, mDesk);

        return true;
    }

    public boolean turnFigureIfExists(int position){
        if (isGameContinue()) {
            Cell cell = mDesk.positionToCell(position);
            Figure figure = mDesk.getFigure(cell);

            if (figure == null || !isFilled) {
                return false;
            } else {
                if (isGameStarted() == false){
                    setGameStarted();
                }

                Position turnedPosition = Position.getTurnedPosition(figure.position);
                figure.position = turnedPosition;
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

    public boolean areSemiFiguresAreWhole(Figure figure1, Figure figure2){ // are the both halves the whole figure
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

    private int makeStep(Cell fromWhere, Cell toWhere){
        Figure ourFigure = mDesk.getFigure(fromWhere);

        if (isStepAvailable(ourFigure,toWhere) != -1){
            if (mDesk.isCellEmpty(toWhere)){
                mDesk.replaceFigure(ourFigure, toWhere);
                return 0;

            }   else {
                Figure figure2 = mDesk.getFigure(toWhere);

                if (areSemiFiguresAreWhole(ourFigure, figure2)){
                    unitedFigureRes = figure2.fullPositionRes;
                    mLevel.increaseGamerCount();
                    mDesk.uniteSemiFigures(fromWhere, toWhere);

                    if (mDesk.isDeskEmpty()){
                        return 2;
                    }
                    return 1;
                }
            }
        }
        return -1;
    }

    private void setGameStarted(){
        isGameStarted = true;
        Saver.writeIsSaveExists(mContext, isGameStarted);
    }

    private Figure getRandomFigure(){
        if (!mDesk.getFreeCells().isEmpty()) {
            Figure figure = FigureFactory.getInstance().getRandomFigure(mDesk.getFreeCells(), mRandomer);
            mDesk.addFigure(figure);
            return figure;
        }
        return null;
    }

    private void addRandomFigure(int howMuchFigures){
        mRecentRandomFigures.clear();

        for (int i = 0; i < howMuchFigures; i++){
            Figure figure = getRandomFigure();

            if (figure != null){
                mRecentRandomFigures.add(figure);
            }
        }
    }

    public void clearRecentRandomFigures(){
        mRecentRandomFigures.clear();
    }

    public int getLevelNumb() {
        return mLevelNumb;
    }

//    public void setLevelNumb(int levelNumb) {
//        mLevelNumb = levelNumb;
//    }

    public void increseLevelNumb(){
        if (mLevelNumb < LevelsValues.LEVELS_NAMES.length - 1){
            mLevelNumb += 1;
        }   else
            mLevelNumb = LevelsValues.LEVELS_NAMES.length - 1; // проработать переход в endless mode
    }
}

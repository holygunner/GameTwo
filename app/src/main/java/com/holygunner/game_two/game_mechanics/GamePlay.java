package com.holygunner.game_two.game_mechanics;

import android.content.Context;

import com.holygunner.game_two.database.Saver;
import com.holygunner.game_two.figures.Figure;
import com.holygunner.game_two.figures.FigureFactory;
import com.holygunner.game_two.figures.Position;
import com.holygunner.game_two.figures.SemiCircle;
import com.holygunner.game_two.values.ColorValues;
import com.holygunner.game_two.values.DeskValues;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.holygunner.game_two.game_mechanics.GameManager.cellToPosition;
import static com.holygunner.game_two.game_mechanics.GameManager.positionToCell;

public class GamePlay {
    private Desk mDesk;
    private Context mContext;

    private boolean isGameStarted;

    private int unitedFigureRes;

    private int gamerCount; // счет игрока

    private boolean isTurnAvailable;

    private AvailableSteps mAvailableSteps;

    public Integer recentPosition;

    private boolean isFilled;

    private Randomer mRandomer;
    private Saver mSaver;

    private List<Figure> recentRandomFigures;

    public GamePlay(Desk desk, Saver saver, Context context){ // если игрока загружаем с файла
        mRandomer = new Randomer();
        this.mSaver = saver;
        this.mContext = context.getApplicationContext();
        this.mDesk = desk;
        isGameStarted = Saver.isSaveExists(mContext);
        gamerCount = Saver.readGamerCount(context);
        recentRandomFigures = new ArrayList<>();
    }

    public void setIsFilled(boolean isFilled){
        this.isFilled = isFilled;
    }

    public AvailableSteps getAvailableSteps(){
        return mAvailableSteps;
    }

    public List<Figure> getRecentRandomFigures(){
        return recentRandomFigures;
    }

    public void resetRecentRandomFigures(){
        recentRandomFigures.clear();
    }

    public Desk loadDesk(Figure[] figures){
        mDesk = new Desk();

        for (int i = 0; i< figures.length; ++i){
            Figure figure = figures[i];
            mDesk.addFigure(figure);
        }
        return mDesk;
    }

    public Desk createNewDesk() {
        mDesk = new Desk();

//        addRandomFigure(3);
        addRandomFigure(2);

        return mDesk;
    }

    public Figure getRandomFigure(List<Cell> freeCells){
        UUID uuid = UUID.randomUUID();
        Class<?> FigureType = mRandomer.getRandomFigureType();
//        Class<?> FigureType = SemiCircle.class;
        FigureFactory factory = FigureFactory.getInstance();
//        Position position = mRandomer.getRandomPosition();
        Position position = mRandomer.getRandomPositionMethod2();
        Cell cell = mRandomer.getRandomCell(freeCells);
//        int color = mRandomer.getRandomColor();

        int color;

        if (FigureType == SemiCircle.class){
            color = ColorValues.FigureColors.PURPLE;
        }   else {
            color = ColorValues.FigureColors.BORDO;
        }

        return factory.createFigure(uuid, FigureType, color,
                position, cell);
    }

    public boolean isGameStarted() {
        return isGameStarted;
    }

    public void setGameStarted(){
        isGameStarted = true;
        Saver.writeIsSaveExists(mContext, isGameStarted);
    }

    public int tryToStep(int position){
        if (!isGameContinue() || mAvailableSteps == null){
            return -1;
        }

        if (mAvailableSteps.isPositionOnStep(position) != -1 && isFilled==true) {
            Cell fromWhere = positionToCell(recentPosition);
            Cell toWhere = positionToCell(position);

            int stepResult = makeStep(fromWhere, toWhere);

            switch (stepResult){
                case 0:
                    addRandomFigure(3);
                    break;
                case 1:
                    addRandomFigure(1);
                    break;
                case 2:
                    bonus();
                    break;
            }

            if (stepResult != -1) {
                if (isGameStarted() == false){ //если первый шаг не сделан, при выходе игра не сохр
                    setGameStarted();
                }
            }
            isFilled = false;

            return stepResult;
        }   else
            return -1;
    }

    public static final int BONUS = 10;

    private void bonus(){
        gamerCount += BONUS;
        addRandomFigure(3);
    }

    private Figure getRandomFigure(){
        if (!mDesk.getFreeCells().isEmpty()) {
            Figure figure = getRandomFigure(mDesk.getFreeCells());
            mDesk.addFigure(figure);
            return figure;
        }
        return null;
    }

    private void addRandomFigure(int howMuchFigures){
        recentRandomFigures.clear();

        for (int i=0; i<howMuchFigures; i++){
            Figure figure = getRandomFigure();

            if (figure != null){
                recentRandomFigures.add(figure);
            }
        }
    }

    public boolean isGameContinue(){
        if (mDesk.getFreeCells().isEmpty()){
            mSaver.writeGamerCount(mContext, gamerCount);
            return false;
        }   else
            return true;
    }

    public boolean setAvailableCells(int position){
        if (!isGameContinue()){
            return false;
        }

        if (mDesk.isCellEmpty(positionToCell(position))){
            return false;
        }
        recentPosition = position;
        mAvailableSteps = new AvailableSteps(position);

        return true;
    }

    public boolean turnFigureIfExists(int position){
        if (isGameContinue()) {
            Cell cell = positionToCell(position);
            Figure figure = mDesk.getFigure(cell);

            if (figure == null || !isFilled) {
                return false;
            } else {
                if (isGameStarted() == false){
                    setGameStarted();
                }

                Position turnedPosition = Position.getTurnedPosition(figure.position);
                figure.position = turnedPosition;

//                addRandomFigure(1); // +1 рандомная фигура при повороте (также раскомментить код в анимации поворота в фрагменте)

                mAvailableSteps = new AvailableSteps(recentPosition);
                isFilled = true;

                return true;
            }
        }   else
        return false;
    }

    public int makeStep(Cell fromWhere, Cell toWhere){
        Figure ourFigure = mDesk.getFigure(fromWhere);

        if (isStepAvailable(ourFigure,toWhere) != -1){
            if (mDesk.isCellEmpty(toWhere)){
                mDesk.replaceFigure(ourFigure, toWhere);
                return 0;

            }   else {
                Figure figure2 = mDesk.getFigure(toWhere);

                if (areSemiFiguresAreWhole(ourFigure, figure2)){
                    unitedFigureRes = figure2.fullPositionRes;
                    increaseGamerCount();
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

    private void increaseGamerCount(){
        ++gamerCount;
    }

    public int getGamerCount(){
        return gamerCount;
    }

    public boolean areSemiFiguresAreWhole(Figure figure1, Figure figure2){ // являются ли обе половины одной фигурой
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

    public class AvailableSteps{
        private List<Cell> availableToStepCells;
        private List<Cell> availableToUniteCells;

        public AvailableSteps(int position){
            availableToUniteCells = new ArrayList<>();
            availableToStepCells = new ArrayList<>();

            init(position);
        }

        public List<Cell> getAvailableToStepCells(){
            return availableToStepCells;
        }

        public List<Cell> getAvailableToUniteCells(){
            return availableToUniteCells;
        }

        private void init(int position){
            Cell cell = positionToCell(position);

            Figure figure = mDesk.getFigure(cell);

            for (int y = 0; y<mDesk.getArr().length; y++ ){
                for (int x = 0; x<mDesk.getArr()[y].length; x++){
                    int isStepAvailable = isStepAvailable(figure, new Cell(x, y));

                    switch (isStepAvailable){
                        case 1:
                            availableToUniteCells.add(new Cell(x, y));
                            break;
                        case 0:
                            availableToStepCells.add(new Cell(x, y));
                            break;
                        case -1:
                            break;
                    }
                }
            }
        }

        public int isPositionOnStep(int position){ // можно ли переставить фигуру на заданую позицию
            Cell chosenCell = positionToCell(position);

            for (Cell cell: availableToUniteCells){
                if (chosenCell.equals(cell)){
                    return 1;
                }
            }

            for (Cell cell: availableToStepCells){
                if (chosenCell.equals(cell)){
                    return 0;
                }
            }
            return -1;
        }

        public boolean isEmpty(){
            if (availableToStepCells.isEmpty() && availableToUniteCells.isEmpty()){
                return false;
            }   else
                return true;
        }
    }
}

package com.holygunner.game_two.game_mechanics;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.holygunner.game_two.database.Saver;
import com.holygunner.game_two.figures.Figure;
import com.holygunner.game_two.figures.FigureFactory;
import com.holygunner.game_two.figures.Position;
import com.holygunner.game_two.figures.SemiSquare;
import com.holygunner.game_two.values.DeskValues;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.holygunner.game_two.game_mechanics.GameManager.cellToPosition;
import static com.holygunner.game_two.game_mechanics.GameManager.positionToCell;

public class GamePlay {
    private Desk mDesk;
    private Context mContext;

    private static final String TAG = "Log";

    private boolean isGameStarted;

    private int unitedFigureRes;

    private int gamerCount; // счет игрока

    private boolean isTurnAvailable;

    private List<Cell> availableCells;
    public Integer currentFigurePosition;

    private boolean isFilled;

    private int deskWidth = DeskValues.DESK_WIDTH;
    private int deskHeight = DeskValues.DESK_HEIGHT;

    private Randomer mRandomer;
    private Saver mSaver;

    private RecyclerView mRecyclerGridDesk;

    public GamePlay(Desk desk, Saver saver, Context context){ // если игрока загружаем с файла
        mRandomer = new Randomer();
        this.mSaver = saver;
        this.mContext = context.getApplicationContext();
        this.mDesk = desk;
        isGameStarted = Saver.isSaveExists(mContext);
        gamerCount = Saver.readGamerCount(context);
    }

    public void setRecyclerGridDesk(RecyclerView recyclerGridDesk){
        mRecyclerGridDesk = recyclerGridDesk;
    }

    public Desk loadDesk(Figure[] figures){
        mDesk = new Desk(deskWidth, deskHeight);

        for (int i = 0; i< figures.length; ++i){
            Figure figure = figures[i];

            mDesk.addFigure(figure);
        }

        return mDesk;
    }

    public Desk createNewDesk() {
        mDesk = new Desk(deskWidth, deskHeight);

//        Figure figure1 = getRandomFigure(mDesk.getFreeCells());
//        mDesk.addFigure(figure1);
//
//        Figure figure2 = getRandomFigure(mDesk.getFreeCells());
//        mDesk.addFigure(figure2);
//
//        Figure figure3 = getRandomFigure(mDesk.getFreeCells());
//        mDesk.addFigure(figure3);

        addRandomFigure(3);

        return mDesk;
    }

    public Figure getRandomFigure(List<Cell> freeCells){
        UUID uuid = UUID.randomUUID();
        FigureFactory factory = FigureFactory.getInstance();
//        Position position = mRandomer.getRandomPosition();
        Position position = mRandomer.getRandomPositionMethod2();
        Cell cell = mRandomer.getRandomCell(freeCells);
        int color = mRandomer.getRandomColor();

        return factory.createFigure(uuid, SemiSquare.class, color,
                position, cell);
    }

    public boolean isGameStarted() {
        return isGameStarted;
    }

    public int tryToStep(int position){
        if (!isGameContinue()){
            return -1;
        }

        // try to make step or attack
        if (isPositionOnStep(position) && isFilled==true) {
            Cell fromWhere = positionToCell(currentFigurePosition);
            Cell toWhere = positionToCell(position);

            Log.i("FW", "currentFigurePosition: " + currentFigurePosition
                    + " position: " + position);

            int stepResult = makeStep(fromWhere, toWhere);

            switch (stepResult){
                case 1:
                    addRandomFigure();
                    break;
                case 0:
                    addRandomFigure(3);
                    break;
            }

            if (stepResult != -1) {
                if (isGameStarted == false){ //если первый шаг не сделан, при выходе игра не сохр
                    isGameStarted = true;
                    Saver.writeIsSaveExists(mContext, isGameStarted);
                }
            }
            isFilled = false;

            return stepResult;
        }   else
            return -1;
    }

    private void addRandomFigure(){
        if (!mDesk.getFreeCells().isEmpty()) {
            mDesk.addFigure(getRandomFigure(mDesk.getFreeCells()));
        }
    }

    private void addRandomFigure(int howMuchFigures){
        for (int i=0; i<howMuchFigures; i++){
            addRandomFigure();
        }
    }

    public boolean isGameContinue(){
        if (mDesk.getFreeCells().isEmpty()){
            mSaver.writeGamerCount(mContext, gamerCount);
            return false;
        }   else
            return true;
    }

    public boolean fillCells(int position){
        if (!isGameContinue()){
            return false;
        }

        if (isCellEmpty(positionToCell(position)) == true){
            return false;
        }
        boolean isFilled = fillOrClearCells(position);

        return isFilled;
    }

    public boolean isPositionOnStep(int position){ // можно ли переставить фигуру на заданую позицию
        if (availableCells == null){
            return false;
        }

        Cell chosenCell = positionToCell(position);

        for (Cell cell: availableCells){
            if (chosenCell.equals(cell)){
                return true;
            }
        }
        return false;
    }

    private void setCellsColor(boolean isFillColor){
        setBackgroundColor(availableCells, getCellsColor(isFillColor));
        isFilled = isFillColor;
    }

    public boolean fillOrClearCells(int position){
        boolean isFillColor;

//        if (availableCells != null && currentFigurePosition == position){ // снять выделение
//            isFillColor = false;
//            setCellsColor(isFillColor);
//            availableCells = null;
//            return isFillColor;
//        }

        currentFigurePosition = position;

        if (availableCells != null) {
            isFillColor = false;
            setCellsColor(isFillColor);
        }
        availableCells = getAvailableCells(position);
        isFillColor = true;
        setCellsColor(isFillColor);

        return isFillColor;
    }

    private int getCellsColor(boolean isFillColor){
        int color;

        if (isFillColor) {
            color = Color.GREEN;
            isFilled = true;
        }   else {
            color = Color.TRANSPARENT;
            isFilled = false;
        }
        return color;
    }

    public boolean getIsFilled(){
        return isFilled;
    }

    public boolean turnFigureIfExists(int position){
        if (isGameContinue()) {
            Cell cell = positionToCell(position);

            Figure figure = mDesk.getFigure(cell);

            if (figure == null || !isFilled) {
                return false;
            } else {
                Position turnedPosition = Position.getTurnedPosition(figure.position);
                figure.position = turnedPosition;

                addRandomFigure();

                fillOrClearCells(position);
                return true;
            }
        }   else
        return false;
    }

    private void setBackgroundColor(List<Cell> availableCells, int color){
        for (int y = 0; y<mDesk.getDesk().length; y++ ){
            for (int x = 0; x<mDesk.getDesk()[y].length; x++){
                Cell cell = new Cell(x, y);
                int position = cellToPosition(cell);

                if (availableCells.contains(cell)){
                    setBackgroundColor(position, color);
                }   else {
                    setBackgroundColor(position, Color.TRANSPARENT);
                }
            }
        }
        int currentFigureColor;

        if (color == Color.GREEN){
            currentFigureColor = Color.BLUE;
        }   else {
            currentFigureColor = Color.TRANSPARENT;
        }

        setBackgroundColor(this.currentFigurePosition, currentFigureColor);
    }

    private void setBackgroundColor(int position, int color){
        mRecyclerGridDesk.getLayoutManager().findViewByPosition(position).setBackgroundColor(color);
    }

    public List<Cell> getAvailableCells(int position){
        Cell cell = positionToCell(position);

        availableCells = new ArrayList<>();

        Figure figure = mDesk.getFigure(cell);

        for (int y = 0; y<mDesk.getDesk().length; y++ ){
            for (int x = 0; x<mDesk.getDesk()[y].length; x++){
                if (isStepAvailable(figure, new Cell(x, y))){
                    availableCells.add(new Cell(x, y));
                }
            }
        }
        return availableCells;
    }

    private boolean isCellEmpty(Cell cell){
        if (mDesk.getFigure(cell) == null){
            return true;
        }
        else{
            return false;
        }
    }

    public int makeStep(Cell fromWhere, Cell toWhere){
        Figure ourFigure = mDesk.getFigure(fromWhere);

        if (isStepAvailable(ourFigure,toWhere)){
            if (isCellEmpty(toWhere)){
                mDesk.replaceFigure(ourFigure, toWhere);
                return 0;

            }   else {
                Figure figure2 = mDesk.getFigure(toWhere);

                if (areSemiFiguresAreWhole(ourFigure, figure2)){
                    unitedFigureRes = figure2.fullPositionRes;
                    increaseGamerCount();
                    mDesk.uniteSemiFigures(fromWhere, toWhere);
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

    public boolean areSemiFiguresAreWhole(Figure figure1, Figure figure2){ // являются ли обе половины части одной целой фигуры
        Position position1 = figure1.position;
        Position position2 = figure2.position;

        int color1 = figure1.color;
        int color2 = figure2.color;

        // добавить проверку на цвет и тип фигуры


        return Position.areHalfesOfWhole(position1, position2) && (color1 == color2); // добавить проверку одного ли типа фигуры
    }

    public boolean isStepAvailable(Figure figure, Cell toWhere){
        boolean isStepAvailable = false;

        int x0 = figure.mCell.getX();
        int y0 = figure.mCell.getY();

        int xSpace = Math.abs(toWhere.getX() - x0);
        int ySpace = Math.abs(toWhere.getY() - y0);

        boolean isOnStepLimit = xSpace<= figure.stepLimit & ySpace<= figure.stepLimit;

        if (mDesk.getFigure(toWhere) != null){
            Figure figure2 = mDesk.getFigure(toWhere);
            if (isOnStepLimit && areSemiFiguresAreWhole(figure, figure2)) {
                isStepAvailable = true;
            }
        }   else
            if (isOnStepLimit) {
                isStepAvailable = true;
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
}

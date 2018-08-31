package com.holygunner.halves_into_whole.game_mechanics;

import com.holygunner.halves_into_whole.figures.Figure;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Desk {
    private Figure[][] desk;
    private int deskSize;
    private List<Cell> freeCells;
    private GamePlay mGamePlay;

    Desk(GamePlay gamePlay, int[] deskXY){
        mGamePlay = gamePlay;
        int deskHeight = deskXY[0];
        int deskWidth = deskXY[1];

        desk = new Figure[deskHeight][deskWidth];
        deskSize = deskHeight*deskWidth;
        initFreeCells();
    }

    public List<Cell> getFreeCells(){
        return freeCells;
    }

    public boolean isDeskOverload(){
        return  (getFreeCells().size() == 0);
    }


    public void addFigure(Figure figure){
        int x = figure.mCell.getX();
        int y = figure.mCell.getY();

        figure.setCell(new Cell(x, y));

        if (desk[y][x] == null){
            desk[y][x] = figure;
            deleteFromFreeCells(x, y);
        }
    }

    public void replaceFigure(Figure figure, Cell toWhere){
        int x1 = figure.mCell.getX();
        int y1 = figure.mCell.getY();

        desk[y1][x1] = null;

        int x2 = toWhere.getX();
        int y2 = toWhere.getY();

        figure.setCell(new Cell(x2, y2));
        desk[y2][x2] = figure;
        updateFreeCells(x1, y1, x2, y2);
    }

    public void uniteSemiFigures(Cell fromWhere, Cell toWhere){
        int x1 = fromWhere.getX();
        int y1 = fromWhere.getY();

        int x2 = toWhere.getX();
        int y2 = toWhere.getY();

        desk[y1][x1] = null;
        desk[y2][x2] = null;

        addFreeCell(x1, y1);
        addFreeCell(x2, y2);
    }

    public Figure[][] deskToMultiArr(){
        return desk;
    }

    public Figure getFigure(int position){
        Cell cell = positionToCell(position);
        return getFigure(cell);
    }

    public Figure getFigure(Cell cell){
        int x = cell.getX();
        int y = cell.getY();
        return this.desk[y][x];
    }

    public boolean isCellEmpty(Cell cell){
        return  (getFigure(cell) == null);
    }

    public boolean isDeskEmpty(){
        return  (freeCells.size() == deskSize);
    }

    public boolean isOneFigureLeft(){
        return (freeCells.size() == deskSize - 1);
    }

    public int cellToPosition(Cell cell){
//        int desk_height = mGamePlay.getLevel().getDeskSize()[0];
        int desk_width = mGamePlay.getLevel().getDeskSize()[1];
        int x = cell.getX();
        int y = cell.getY();

        return y*desk_width+x;
    }

    public Cell positionToCell(int position){
//        int desk_height = mGamePlay.getLevel().getDeskSize()[0];
        int desk_width = mGamePlay.getLevel().getDeskSize()[1];

        int y = position/desk_width;
        int x = position%desk_width;

        return new Cell(x,y);
    }

    private void initFreeCells(){
        freeCells = new LinkedList<>();

        for (int y = 0; y<desk.length; y++ ){
            for (int x = 0; x<desk[y].length; x++){
                freeCells.add(new Cell(x, y));
            }
        }
    }

    private void deleteFromFreeCells(int x, int y){
        for (Iterator<Cell> iterator = freeCells.iterator(); iterator.hasNext();) {
            Cell cell =  iterator.next();
            if (cell.getX() == x && cell.getY() == y) {
                iterator.remove();
            }
        }
    }

    private void updateFreeCells(int x1, int y1, int x2, int y2){
        addFreeCell(x1, y1);
        deleteFromFreeCells(x2, y2);
    }

    private void addFreeCell(int x , int y){
        freeCells.add(new Cell(x, y));
    }
}

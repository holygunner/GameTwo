package com.holygunner.game_two.game_mechanics;

import com.holygunner.game_two.figures.Figure;
import com.holygunner.game_two.values.DeskValues;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Desk {
    private Figure[][] desk;
    private int deskWidth = DeskValues.DESK_WIDTH;
    private int deskHeight = DeskValues.DESK_HEIGHT;
    private int deskSize;

    private List<Cell> freeCells;

    public Desk(){
        desk = new Figure[deskHeight][deskWidth];
        deskSize = deskHeight*deskWidth;
        initFreeCells();
    }

    public List<Cell> getFreeCells(){
        return freeCells;
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

    public boolean replaceFigure(Figure figure, Cell toWhere){
        int x1 = figure.mCell.getX();
        int y1 = figure.mCell.getY();

        desk[y1][x1] = null;

        int x2 = toWhere.getX();
        int y2 = toWhere.getY();

        figure.setCell(new Cell(x2, y2));
        desk[y2][x2] = figure;
        updateFreeCells(x1, y1, x2, y2);

        return true;
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
        Cell cell = GameManager.positionToCell(position);
        return getFigure(cell);
    }

    public Figure getFigure(Cell cell){
        int x = cell.getX();
        int y = cell.getY();
        return this.desk[y][x];
    }

    public boolean isCellEmpty(Cell cell){
        if (getFigure(cell) == null){
            return true;
        }   else {
            return false;
        }
    }

    public boolean isDeskEmpty(){
        if (freeCells.size() == deskSize){
            return true;
        }   else {
            return false;
        }
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

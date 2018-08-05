package com.holygunner.game_two.figures;

import com.holygunner.game_two.game_mechanics.Cell;

import java.util.UUID;

public class Figure {
    public Position position;
    public int color;
    public int stepLimit = 1; // количество клеток за раз которые может преоделеть персонаж, по умолчанию 1
    public Cell mCell;
    public int fullPositionRes;
    private UUID mUUID;

    public Figure(){
        mUUID = UUID.randomUUID();
    }

    public Figure(UUID uuid) {
        mUUID = uuid;
    }

    public void setFullPositionRes(){
    }

    public void setCell(Cell cell){
        mCell = cell;
    }

    public static int getFigureRes(Figure figure){
        int color = figure.color;
        Position position = figure.position;

        if (figure instanceof SemiSquare)
            return SemiSquare.getRes(color, position);
        if (figure instanceof SemiCircle)
            return SemiCircle.getRes(color, position);
        else
            return 0;
    }

    public String toString(){
        return "" + mUUID.toString() + color + position.toString();
    }

    public UUID getUUID(){
        return mUUID;
    }

    public Position getPosition(){
        return position;
    }

    public int getColor(){
        return color;
    }

    public Cell getCell(){
        return mCell;
    }

    public int getStepLimit(){
        return stepLimit;
    }
}

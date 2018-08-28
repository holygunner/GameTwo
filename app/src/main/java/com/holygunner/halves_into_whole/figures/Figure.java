package com.holygunner.halves_into_whole.figures;

import com.holygunner.halves_into_whole.game_mechanics.Cell;

import java.util.UUID;

public class Figure {

    public Position position;
    public int color;
    public int stepLimit = 1; // how many cells the character can move during one step, 1 by default
    public Cell mCell;
    public int fullPositionRes;
    private UUID mUUID;

    public Figure(){
        mUUID = UUID.randomUUID();
    }

    public Figure(UUID uuid) {
        mUUID = uuid;
    }

    public void setFullPositionRes(){}

    public void setCell(Cell cell){
        mCell = cell;
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

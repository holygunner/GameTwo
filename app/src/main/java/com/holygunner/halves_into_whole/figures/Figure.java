package com.holygunner.halves_into_whole.figures;

import com.holygunner.halves_into_whole.game_mechanics.Cell;

import java.util.UUID;

public class Figure {
    public Position position;
    public int color;
    public int stepLimit = 1; // how many cells the character can move during one step, 1 by default
    public Cell cell;
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
        this.cell = cell;
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
        return cell;
    }

    public int getStepLimit(){
        return stepLimit;
    }
}

package com.holygunner.game_two.figures;

import com.holygunner.game_two.game_mechanics.Cell;

import java.util.UUID;

/**
 * Created by Holygunner on 01.07.2018.
 */

public class Figure {
    private UUID mUUID;
    public Position position;
    public int color;
    public int stepLimit = 1; // количество клеток за раз которые может преоделеть персонаж, по умолчанию 1
    public Cell mCell;
    public int fullPositionRes;

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
        else
            return 0;
    }

    public boolean equals(Object obj){

        if (obj == this)
            return true;
        if (!(obj instanceof Figure))
            return false;

        Figure figure = (Figure) obj;

//        return  (figure.name.equals(this.name) && figure.attack == this.attack
//                && figure.stamina == this.stamina && figure.magicAbility == this.magicAbility);
        return false;
    }

    public int getFullPositionRes(){
        return fullPositionRes;
    }

    public String getFigureType(Figure figure){
        if (figure instanceof SemiSquare){
            return "SemiSquare";
        }

        if (figure instanceof SemiCircle){
            return "SemiCircle";
        }

        return null;
    }

    public int hashCode(){
//        int hashCode = name.hashCode() + attack + stamina;
        int hashCode = 0;
        return hashCode;
    }

    public void setStepLimit(int stepLimit){
        this.stepLimit = stepLimit;
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

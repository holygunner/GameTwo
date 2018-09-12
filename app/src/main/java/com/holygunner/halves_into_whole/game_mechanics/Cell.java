package com.holygunner.halves_into_whole.game_mechanics;

public class Cell {
    private int mX;
    private int mY;

    public Cell (int x, int y){
        this.mX = x;
        this.mY = y;
    }

    public int getX(){
        return mX;
    }

    public int getY(){
        return mY;
    }

    @Override
    public boolean equals(Object obj){
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        Cell checkedCell = (Cell) obj;

        return this.mX == checkedCell.mX && this.mY == checkedCell.mY;
    }

    @Override
    public int hashCode(){
        return (mX *17)+(mY *37);
    }
}
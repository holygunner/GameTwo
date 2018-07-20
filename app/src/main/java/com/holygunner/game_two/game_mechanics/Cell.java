package com.holygunner.game_two.game_mechanics;

/**
 * Created by Holygunner on 04.04.2018.
 */

public class Cell {
    private int x;
    private int y;

    public Cell (int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public void setX(int x){
        this.x = x;
    }

    public void setY(int y){
        this.y = y;
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

        return this.x == checkedCell.x && this.y == checkedCell.y;
    }

    @Override
    public int hashCode(){
        return (x*17)+(y*37);
    }
}

package com.holygunner.game_two.game_mechanics;

import com.holygunner.game_two.figures.Position;
import com.holygunner.game_two.values.FigureValues.FigureColors;

import java.util.List;
import java.util.Random;

/**
 * Created by Holygunner on 01.07.2018.
 */

public class Randomer {

    private final Position[] POSITIONS = new Position[]{Position.POSITION_ONE, Position.POSITION_TWO,
            Position.POSITION_THREE, Position.POSITION_FOUR};;

    Random mRandom;

    public Randomer(){
        mRandom = getRandom();
    }

    private static Random getRandom(){
        return new Random();
    }

    public Position getRandomPosition(){
        int length = POSITIONS.length;
        return POSITIONS[mRandom.nextInt(length)];
    }

    public Cell getRandomCell(List<Cell> cells){
        int length = cells.size();
        return cells.get(mRandom.nextInt(length));
    }

    public int getRandomColor(){
        int[] colors = FigureColors.getColors();
        return colors[mRandom.nextInt(colors.length)];
    }

}

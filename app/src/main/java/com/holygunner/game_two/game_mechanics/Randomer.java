package com.holygunner.game_two.game_mechanics;

import com.holygunner.game_two.figures.Position;
import com.holygunner.game_two.figures.SemiCircle;
import com.holygunner.game_two.figures.SemiSquare;
import com.holygunner.game_two.values.ColorValues.FigureColors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Holygunner on 01.07.2018.
 */

public class Randomer {

    private final Position[] POSITIONS = new Position[]{Position.POSITION_ONE, Position.POSITION_TWO,
            Position.POSITION_THREE, Position.POSITION_FOUR};

    private List<Position> positions;

    Random mRandom;

    public Randomer(){
        mRandom = getRandom();
        initPositions();
    }

    private void initPositions(){
        positions = new ArrayList<>(Arrays.asList(POSITIONS));
    }

    private static Random getRandom(){
        return new Random();
    }

    public Position getRandomPosition(){
        int length = POSITIONS.length;
        return POSITIONS[mRandom.nextInt(length)];
    }

    public Position getRandomPositionMethod2(){ // returned positions are not same by every time
        int size;
        int index;
        Position position;

        if (positions.isEmpty()){
            initPositions();
        }

        size = positions.size();
        index = mRandom.nextInt(size);
        position = positions.get(index);
        positions.remove(index);

        return position;
    }

    public Cell getRandomCell(List<Cell> cells){
        int length = cells.size();
        return cells.get(mRandom.nextInt(length));
    }

    public int getRandomColor(){
        int[] colors = FigureColors.getColors();
        return colors[mRandom.nextInt(colors.length)];
    }

    public Class<?> getRandomFigureType() {
        Class<?>[] classArray = new Class<?>[]{SemiSquare.class, SemiCircle.class};
        return classArray[mRandom.nextInt(classArray.length)];
    }
}

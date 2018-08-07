package com.holygunner.game_two.game_mechanics;

import com.holygunner.game_two.figures.FigureClassAndColorPair;
import com.holygunner.game_two.figures.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Randomer {

    private final Position[] POSITIONS = new Position[]{Position.POSITION_ONE, Position.POSITION_TWO,
            Position.POSITION_THREE, Position.POSITION_FOUR};

    private List<Position> positions;

    private Random mRandom;

    private Level mLevel;

    public Randomer(Level level){
        mLevel = level;
        mRandom = getRandom();
        initPositions();
    }

    public Position getRandomPositionFirstRealization(){
        int length = POSITIONS.length;
        return POSITIONS[mRandom.nextInt(length)];
    }

    public Position getRandomPosition(){ // returned position which is not same by every time
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

    public FigureClassAndColorPair getRandomFigureTypeAndColorPair(){
        FigureClassAndColorPair[] pairArr = mLevel.getFigureClassesAndColorPairs();

        return pairArr[mRandom.nextInt(pairArr.length)];

    }

    private void initPositions(){
        positions = new ArrayList<>(Arrays.asList(POSITIONS));
    }

    private static Random getRandom(){
        return new Random();
    }
}

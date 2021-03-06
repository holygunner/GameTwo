package com.holygunner.halves_into_whole.game_mechanics;

import com.holygunner.halves_into_whole.figures.FigureClassAndColorPair;
import com.holygunner.halves_into_whole.figures.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RandomGenerator {
    private final Position[] POSITIONS = new Position[]{Position.POSITION_ONE, Position.POSITION_TWO,
            Position.POSITION_THREE, Position.POSITION_FOUR};
    private List<Position> mPositions;
    private Random mRandom;
    private Level mLevel;

    RandomGenerator(Level level){
        mLevel = level;
        mRandom = getRandom();
        initPositions();
    }

    public Position getRandomPosition(){ // returned position which is not same by every time
        int size;
        int index;
        Position position;

        if (mPositions.isEmpty()){
            initPositions();
        }

        size = mPositions.size();
        index = mRandom.nextInt(size);
        position = mPositions.get(index);
        mPositions.remove(index);

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
        mPositions = new ArrayList<>(Arrays.asList(POSITIONS));
    }

    private static Random getRandom(){
        return new Random();
    }
}

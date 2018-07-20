package com.holygunner.game_two.game_mechanics;

import com.holygunner.game_two.figures.Position;

import java.util.List;
import java.util.Random;

/**
 * Created by Holygunner on 01.07.2018.
 */

public class Randomer { // будет возвращать случайную свободную клетку и случайное положение половинки фигуры

    private final Position[] POSITIONS = new Position[]{Position.POSITION_ONE, Position.POSITION_TWO,
            Position.POSITION_THREE, Position.POSITION_FOUR};;

    final Random mRandom = new Random();

    public Randomer(){
    }

    public Position getRandomPosition(){
        int length = POSITIONS.length;
        Position randomPosition = POSITIONS[mRandom.nextInt(length)];

        return randomPosition;
    }

    public Cell getRandomCell(List<Cell> cells){
        int length = cells.size();
        Cell randomCell = cells.get(mRandom.nextInt(length));

        return randomCell; // TEST
    }



    private int getRandomColor(){// случайный цвет фигуры из набора доступных цветов
        return 0;
    }

}

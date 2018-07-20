package com.holygunner.game_two.figures;

/**
 * Created by Holygunner on 01.07.2018.
 */

public enum Position {
    POSITION_ONE ("CornerUpLeft"),
    POSITION_TWO ("CornerDownRight"),
    POSITION_THREE ("CornerUpRight"),
    POSITION_FOUR ("CornerDownLeft"), // смотри рисунок в тетрадке
    POSITION_FULL ("Full figure");

    private final String name;

    private Position(String s){
        name = s;
    }

    public static boolean areHalfesOfWhole(Position position1, Position position2){
        if ((position1 == POSITION_ONE && position2 == POSITION_TWO)
                || (position1 == POSITION_TWO && position2 == POSITION_ONE)
                || (position1 == POSITION_THREE && position2 == POSITION_FOUR)
                || (position1 == POSITION_FOUR && position2 == POSITION_THREE)){
            return true;
        }   else
            return false;
    }

    public static Position getPositionFromString(String positionString){
        Position position = null;

        if (positionString.equals(POSITION_ONE.toString())){
            position = POSITION_ONE;
        }   else if (positionString.equals(POSITION_TWO.toString())){
            position = POSITION_TWO;
        }   else if (positionString.equals(POSITION_THREE.toString())){
            position = POSITION_THREE;
        }   else if (positionString.equals(POSITION_FOUR.toString())){
            position = POSITION_FOUR;
        }

        return position;
    }

    public static Position getTurnedPosition(Position position){
        Position turnedPosition = null;

            switch (position){
                case POSITION_ONE:
                    turnedPosition = POSITION_THREE;
                    break;
                case POSITION_TWO:
                    turnedPosition = POSITION_FOUR;
                    break;
                case POSITION_THREE:
                    turnedPosition = POSITION_TWO;
                    break;
                case POSITION_FOUR:
                    turnedPosition = POSITION_ONE;
                    break;
            }

        return turnedPosition;
    }

    @Override
    public String toString(){
        return this.name;
    }
}

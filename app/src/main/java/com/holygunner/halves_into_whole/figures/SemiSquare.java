package com.holygunner.halves_into_whole.figures;

import com.holygunner.halves_into_whole.R;
import com.holygunner.halves_into_whole.game_mechanics.Cell;
import com.holygunner.halves_into_whole.values.ColorsValues.FigureColors;

import java.util.UUID;

public class SemiSquare extends Figure {
    private static int resBordoPos1 = R.drawable.semi_square_bordo_p1;
    private static int resBordoPos2 = R.drawable.semi_square_bordo_p2;
    private static int resBordoPos3 = R.drawable.semi_square_bordo_p3;
    private static int resBordoPos4 = R.drawable.semi_square_bordo_p4;
    private static int resBordoPosFull = R.drawable.full_square_bordo;

    private static int resPurplePos1 = R.drawable.semi_square_purple_p1;
    private static int resPurplePos2 = R.drawable.semi_square_purple_p2;
    private static int resPurplePos3 = R.drawable.semi_square_purple_p3;
    private static int resPurplePos4 = R.drawable.semi_square_purple_p4;
    private static int resPurplePosFull = R.drawable.full_square_purple;

    public SemiSquare(UUID uuid, int color, Position position, Cell cell) {
        super(uuid);
        super.color = color;
        super.position = position;
        super.mCell = cell;
        setFullPositionRes();
    }

    @Override
    public void setFullPositionRes(){
        int color = super.color;

        if (color == FigureColors.BORDO){
            super.fullPositionRes = resBordoPosFull;
        }   else
        if (color == FigureColors.PURPLE){
            super.fullPositionRes = resPurplePosFull;
        }
    }

    public static int getRes(int color, Position position) {
        int res = 0;

        if (color == FigureColors.BORDO) {
            switch (position) {
                case POSITION_ONE:
                    res = resBordoPos1;
                    break;
                case POSITION_TWO:
                    res = resBordoPos2;
                    break;
                case POSITION_THREE:
                    res = resBordoPos3;
                    break;
                case POSITION_FOUR:
                    res = resBordoPos4;
                    break;
            }

        }   else if (color == FigureColors.PURPLE) {
            switch (position) {
                case POSITION_ONE:
                    res = resPurplePos1;
                    break;
                case POSITION_TWO:
                    res = resPurplePos2;
                    break;
                case POSITION_THREE:
                    res = resPurplePos3;
                    break;
                case POSITION_FOUR:
                    res = resPurplePos4;
                    break;
            }
        }
        return res;
    }
}

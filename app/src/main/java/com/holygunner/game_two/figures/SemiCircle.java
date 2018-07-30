package com.holygunner.game_two.figures;

import com.holygunner.game_two.R;
import com.holygunner.game_two.game_mechanics.Cell;
import com.holygunner.game_two.values.ColorValues;

import java.util.UUID;

/**
 * Created by Holygunner on 01.07.2018.
 */

public class SemiCircle extends Figure { // половина круга
    private static int resBordoPos1 = R.drawable.semi_circle_bordo_p1;
    private static int resBordoPos2 = R.drawable.semi_circle_bordo_p2;
    private static int resBordoPos3 = R.drawable.semi_circle_bordo_p3;
    private static int resBordoPos4 = R.drawable.semi_circle_bordo_p4;
    private static int resBordoPosFull = R.drawable.full_circle_bordo;

    private static int resPurplePos1 = R.drawable.semi_circle_purple_p1;
    private static int resPurplePos2 = R.drawable.semi_circle_purple_p2;
    private static int resPurplePos3 = R.drawable.semi_circle_purple_p3;
    private static int resPurplePos4 = R.drawable.semi_circle_purple_p4;
    private static int resPurplePosFull = R.drawable.full_circle_purple;

    public SemiCircle(int color, Position position, Cell cell){
        super.color = color;
        super.position = position;
        super.mCell = cell;
    }

    public SemiCircle(UUID uuid, int color, Position position, Cell cell) {
        super(uuid);
        super.color = color;
        super.position = position;
        super.mCell = cell;
        setFullPositionRes();
    }

    @Override
    public void setFullPositionRes(){
        switch (super.color) {
            case ColorValues.FigureColors.BORDO:
                super.fullPositionRes = resBordoPosFull;
                break;
            case ColorValues.FigureColors.PURPLE:
                super.fullPositionRes = resPurplePosFull;
                break;
        }
    }

    public static int getRes(int color, Position position) {
        int res = 0;

        if (color == ColorValues.FigureColors.BORDO) {
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
            return res;
        }

        if (color == ColorValues.FigureColors.PURPLE) {
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
            return res;
        }

        return res;
    }
}

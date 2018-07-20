package com.holygunner.game_two.figures;

import com.holygunner.game_two.R;
import com.holygunner.game_two.game_mechanics.Cell;

import java.util.UUID;

/**
 * Created by Holygunner on 01.07.2018.
 */

public class SemiSquare extends Figure { // половина квадрата
    private static int resBordoPosition1 = R.drawable.cell_semi_square_color_bordo_position1;
    private static int resBordoPosition2 = R.drawable.cell_semi_square_color_bordo_position2;
    private static int resBordoPosition3 = R.drawable.cell_semi_square_color_bordo_position3;
    private static int resBordoPosition4 = R.drawable.cell_semi_square_color_bordo_position4;

    public SemiSquare(UUID uuid, int color, Position position, Cell cell){
        super(uuid);
        super.color = color;
        super.position = position;
        super.mCell = cell;
    }

    public static int getRes (int color, Position position){
        int res;
        switch (position) {
            case POSITION_ONE:
                res = resBordoPosition1;
                break;
            case POSITION_TWO:
                res = resBordoPosition2;
                break;
            case POSITION_THREE:
                res = resBordoPosition3;
                break;
            case POSITION_FOUR:
                res = resBordoPosition4;
                break;
            default:
                res = 0;
                break;
        }
        return res;
        }

}

package com.holygunner.game_two.figures;

import android.graphics.Color;

import com.holygunner.game_two.R;
import com.holygunner.game_two.game_mechanics.Cell;
import com.holygunner.game_two.values.FigureValues.FigureColors;

import java.util.UUID;

/**
 * Created by Holygunner on 01.07.2018.
 */

public class SemiSquare extends Figure { // половина квадрата
    private static int resBordoPos1 = R.drawable.cell_semi_square_color_bordo_position1;
    private static int resBordoPos2 = R.drawable.cell_semi_square_color_bordo_position2;
    private static int resBordoPos3 = R.drawable.cell_semi_square_color_bordo_position3;
    private static int resBordoPos4 = R.drawable.cell_semi_square_color_bordo_position4;
    private static int resBordoPosFull = R.drawable.cell_full_square_color_bordo;

    private static int resPurplePos1 = R.drawable.cell_semi_square_color_purple_position1;
    private static int resPurplePos2 = R.drawable.cell_semi_square_color_purple_position2;
    private static int resPurplePos3 = R.drawable.cell_semi_square_color_purple_position3;
    private static int resPurplePos4 = R.drawable.cell_semi_square_color_purple_position4;
    private static int resPurplePosFull = R.drawable.cell_full_square_color_purple;

    public SemiSquare(UUID uuid, int color, Position position, Cell cell) {
        super(uuid);
        super.color = color;
        super.position = position;
        super.mCell = cell;
        setFullPositionRes();
    }

    @Override
    public void setFullPositionRes(){
            switch (super.color) {
                case FigureColors.BORDO:
                    super.fullPositionRes = resBordoPosFull;
                    break;
                case FigureColors.PURPLE:
                    super.fullPositionRes = resPurplePosFull;
                    break;
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
            return res;
        }

        if (color == FigureColors.PURPLE) {
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

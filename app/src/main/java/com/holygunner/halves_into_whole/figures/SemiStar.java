package com.holygunner.halves_into_whole.figures;

import com.holygunner.halves_into_whole.game_mechanics.Cell;
import com.holygunner.halves_into_whole.values.ColorsValues;
import com.holygunner.halves_into_whole.values.ColorsValues.FigureColors;

import java.util.UUID;

import static com.holygunner.halves_into_whole.R.drawable.*;

public class SemiStar extends Figure {

    SemiStar(UUID uuid, int color, Position position, Cell cell) {
        super(uuid);
        super.color = color;
        super.position = position;
        super.cell = cell;
        setFullPositionRes();
    }

    @Override
    public void setFullPositionRes(){
        int color = super.color;

        if (color == FigureColors.BORDO){
            super.fullPositionRes = full_star_bordo;
        }   else
        if (color == FigureColors.PURPLE){
            super.fullPositionRes = full_star_purple;
        }   else
        if (color == FigureColors.SALMON){
            super.fullPositionRes = full_star_salmon;
        }   else
        if (color == FigureColors.LILAC){
            super.fullPositionRes = full_star_lilac;
        }   else
        if (color == FigureColors.GREEN){
            super.fullPositionRes = full_star_green;
        }
    }

    public static int getRes(int color, Position position) {
        int res = 0;

        if (color == ColorsValues.FigureColors.BORDO) {
            switch (position) {
                case POSITION_ONE:
                    res = semi_star_bordo_p1;
                    break;
                case POSITION_TWO:
                    res = semi_star_bordo_p2;
                    break;
                case POSITION_THREE:
                    res = semi_star_bordo_p3;
                    break;
                case POSITION_FOUR:
                    res = semi_star_bordo_p4;
                    break;
            }
        } else if (color == ColorsValues.FigureColors.PURPLE) {
            switch (position) {
                case POSITION_ONE:
                    res = semi_star_purple_p1;
                    break;
                case POSITION_TWO:
                    res = semi_star_purple_p2;
                    break;
                case POSITION_THREE:
                    res = semi_star_purple_p3;
                    break;
                case POSITION_FOUR:
                    res = semi_star_purple_p4;
                    break;
            }
        }   else if (color == FigureColors.SALMON) {
            switch (position) {
                case POSITION_ONE:
                    res = semi_star_salmon_p1;
                    break;
                case POSITION_TWO:
                    res = semi_star_salmon_p2;
                    break;
                case POSITION_THREE:
                    res = semi_star_salmon_p3;
                    break;
                case POSITION_FOUR:
                    res = semi_star_salmon_p4;
                    break;
            }
        }   else if (color == FigureColors.LILAC) {
            switch (position) {
                case POSITION_ONE:
                    res = semi_star_lilac_p1;
                    break;
                case POSITION_TWO:
                    res = semi_star_lilac_p2;
                    break;
                case POSITION_THREE:
                    res = semi_star_lilac_p3;
                    break;
                case POSITION_FOUR:
                    res = semi_star_lilac_p4;
                    break;
            }
        }   else if (color == FigureColors.GREEN) {
            switch (position) {
                case POSITION_ONE:
                    res = semi_star_green_p1;
                    break;
                case POSITION_TWO:
                    res = semi_star_green_p2;
                    break;
                case POSITION_THREE:
                    res = semi_star_green_p3;
                    break;
                case POSITION_FOUR:
                    res = semi_star_green_p4;
                    break;
            }
        }
        return res;
    }
}

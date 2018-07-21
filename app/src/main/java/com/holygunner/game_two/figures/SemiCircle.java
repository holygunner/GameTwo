package com.holygunner.game_two.figures;

import com.holygunner.game_two.game_mechanics.Cell;

/**
 * Created by Holygunner on 01.07.2018.
 */

public class SemiCircle extends Figure { // половина круга
    public SemiCircle(int color, Position position, Cell cell){
        super.color = color;
        super.position = position;
        super.mCell = cell;
    }

    @Override
    public void setFullPositionRes() {
        
    }
}

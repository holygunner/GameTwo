package com.holygunner.game_two.game_mechanics;

import com.holygunner.game_two.figures.Figure;

import java.util.LinkedList;
import java.util.List;

public abstract class DeskToCellsListConverter {

    // Convert 2nd dimension array to List. Required for a correct work with a RecyclerView
    public static List<String> getCellList(Desk desk){
        Figure[][] figureTwoDimensionArr = desk.deskToMultiArr();
        List<String> characterList = new LinkedList<>();
        int p = 0;

        for (int x = 0; x< figureTwoDimensionArr.length; ++x){
            for (int y = 0; y< figureTwoDimensionArr[x].length; ++y){
                characterList.add(p++, figureToString(figureTwoDimensionArr[x][y]));
            }
        }
        return characterList;
    }

    private static String figureToString(Figure figure){
        if (figure == null)
            return "";
        else return figure.toString();
    }
}

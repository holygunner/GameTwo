package com.holygunner.halves_into_whole.game_mechanics;

import com.holygunner.halves_into_whole.figures.Figure;

import java.util.LinkedList;
import java.util.List;

public abstract class DeskToCellsListConverter {
    // Convert 2nd dimension array to List. Required for a correct work with a RecyclerView
    public static List<String> getCellList(Desk desk){
        Figure[][] figureTwoDimensionArr = desk.deskToMultiArr();
        List<String> characterList = new LinkedList<>();
        int p = 0;

        for (Figure[] aFigureTwoDimensionArr : figureTwoDimensionArr) {
            for (Figure anAFigureTwoDimensionArr : aFigureTwoDimensionArr) {
                characterList.add(p++, figureToString(anAFigureTwoDimensionArr));
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

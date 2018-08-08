package com.holygunner.game_two.game_mechanics;

import com.holygunner.game_two.figures.Figure;

import java.util.LinkedList;
import java.util.List;

public class DeskToCellsListConverter {
    private static DeskToCellsListConverter instance;

    private DeskToCellsListConverter(){
    }

    public static DeskToCellsListConverter getInstance(){
        if (instance == null){
            instance = new DeskToCellsListConverter();
        }
        return instance;
    }

    // Convert 2nd dimension array to List. Required for a correct work with a RecyclerView
    public List<String> getCellList(Desk desk){
        Figure[][] figureTwoDimensionArr = desk.deskToMultiArr();
        List<String> characterList = new LinkedList<>();
        int p = 0;

        for (int x = 0; x< figureTwoDimensionArr.length; ++x){
            for (int y = 0; y< figureTwoDimensionArr[x].length; ++y){
                characterList.add(p++, whichCharacter(figureTwoDimensionArr[x][y]));
            }
        }
        return characterList;
    }

    private String whichCharacter(Figure figure){
        if (figure == null)
            return "";
        else return figure.toString();
    }
}

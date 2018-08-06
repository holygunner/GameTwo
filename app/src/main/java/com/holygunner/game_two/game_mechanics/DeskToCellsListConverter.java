package com.holygunner.game_two.game_mechanics;

import com.holygunner.game_two.figures.Figure;

import java.util.LinkedList;
import java.util.List;

public class DeskToCellsListConverter {
    private static DeskToCellsListConverter instance;

    private DeskToCellsListConverter(){
    }

    public static DeskToCellsListConverter getInstanse(){
        if (instance == null){
            instance = new DeskToCellsListConverter();
        }
        return instance;
    }

    // Convert 2nd dimension array to List. Required for a correct work with a RecyclerView
    public List<String> getCellList(Desk desk){
        Figure[][] figureTwoDArr = desk.deskToMultiArr();
        List<String> characterList = new LinkedList<>();
        int p = 0;

        for (int x = 0; x< figureTwoDArr.length; ++x){
            for (int y = 0; y< figureTwoDArr[x].length; ++y){
                characterList.add(p++, whichCharacter(figureTwoDArr[x][y]));
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

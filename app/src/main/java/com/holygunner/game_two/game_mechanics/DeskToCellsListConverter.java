package com.holygunner.game_two.game_mechanics;

import android.util.Log;

import com.holygunner.game_two.figures.Figure;
import com.holygunner.game_two.game_mechanics.*;

import java.util.LinkedList;
import java.util.List;

public class DeskToCellsListConverter {
    private Desk mDesk;
//    private String deskVisualization = "";

    public DeskToCellsListConverter(Desk desk){
        this.mDesk = desk;
    }

    // преобразовать 2х-мерный массив в List. Нужно для корректной работы с RecyclerView
    public List<String> getCellList(){
        Figure[][] figureTwoDArr = mDesk.getDesk();

        List<String> characterList = new LinkedList<>();

        int p = 0;

        for (int x = 0; x< figureTwoDArr.length; ++x){
            for (int y = 0; y< figureTwoDArr[x].length; ++y){
                characterList.add(p++, whichCharacter(figureTwoDArr[x][y]));
            }
        }

        for (String ch: characterList){
            Log.i("LOG", ch);
        }

        return characterList;
    }

    private String whichCharacter(Figure figure){

        if (figure == null)
            return "";
        else return figure.toString();
    }
}

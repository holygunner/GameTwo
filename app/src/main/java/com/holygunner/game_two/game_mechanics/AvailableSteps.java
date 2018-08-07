package com.holygunner.game_two.game_mechanics;

import com.holygunner.game_two.figures.Figure;

import java.util.ArrayList;
import java.util.List;

public class AvailableSteps {
    private List<Cell> availableToStepCells;
    private List<Cell> availableToUniteCells;

    private GamePlay mGamePlay;
    private Desk mDesk;

    public AvailableSteps(GamePlay gamePlay, int position, Desk desk){
        mGamePlay = gamePlay;
        mDesk = desk;
        availableToUniteCells = new ArrayList<>();
        availableToStepCells = new ArrayList<>();
        init(position);
    }

    public List<Cell> getAvailableToStepCells(){
        return availableToStepCells;
    }

    public List<Cell> getAvailableToUniteCells(){
        return availableToUniteCells;
    }

    public int isPositionOnStep(int position){ // is Figure removed to given position
        Cell chosenCell = mDesk.positionToCell(position);

        for (Cell cell: availableToUniteCells){
            if (chosenCell.equals(cell)){
                return 1;
            }
        }

        for (Cell cell: availableToStepCells){
            if (chosenCell.equals(cell)){
                return 0;
            }
        }
        return -1;
    }

    public boolean isEmpty(){
        if (availableToStepCells.isEmpty() && availableToUniteCells.isEmpty()){
            return false;
        }   else
            return true;
    }

    private void init(int position){
        Cell cell = mDesk.positionToCell(position);

        Figure figure = mDesk.getFigure(cell);

        for (int y = 0; y<mDesk.deskToMultiArr().length; y++ ){
            for (int x = 0; x<mDesk.deskToMultiArr()[y].length; x++){
                int isStepAvailable = mGamePlay.isStepAvailable(figure, new Cell(x, y));

                switch (isStepAvailable){
                    case 1:
                        availableToUniteCells.add(new Cell(x, y));
                        break;
                    case 0:
                        availableToStepCells.add(new Cell(x, y));
                        break;
                    case -1:
                        break;
                }
            }
        }
    }
}

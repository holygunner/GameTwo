package com.holygunner.halves_into_whole.game_mechanics;

import com.holygunner.halves_into_whole.figures.Figure;

import java.util.ArrayList;
import java.util.List;

public class AvailableSteps {
    private List<Cell> mAvailableToStepCells;
    private List<Cell> mAvailableToUniteCells;

    private GamePlay mGamePlay;
    private Desk mDesk;

    AvailableSteps(GamePlay gamePlay, int position, Desk desk){
        mGamePlay = gamePlay;
        mDesk = desk;
        mAvailableToUniteCells = new ArrayList<>();
        mAvailableToStepCells = new ArrayList<>();
        init(position);
    }

    public List<Cell> getAvailableToStepCells(){
        return mAvailableToStepCells;
    }

    public List<Cell> getAvailableToUniteCells(){
        return mAvailableToUniteCells;
    }

    int isPositionOnStep(int position){ // is Figure removed to given position
        Cell chosenCell = mDesk.positionToCell(position);

        for (Cell cell: mAvailableToUniteCells){
            if (chosenCell.equals(cell)){
                return 1;
            }
        }

        for (Cell cell: mAvailableToStepCells){
            if (chosenCell.equals(cell)){
                return 0;
            }
        }
        return -1;
    }

    private void init(int position){
        Cell cell = mDesk.positionToCell(position);

        Figure figure = mDesk.getFigure(cell);

        for (int y = 0; y < mDesk.deskToMultiArr().length; y++ ){
            for (int x = 0; x < mDesk.deskToMultiArr()[y].length; x++){
                int isStepAvailable = mGamePlay.isStepAvailable(figure, new Cell(x, y));

                switch (isStepAvailable){
                    case 1:
                        mAvailableToUniteCells.add(new Cell(x, y));
                        break;
                    case 0:
                        mAvailableToStepCells.add(new Cell(x, y));
                        break;
                    case -1:
                        break;
                }
            }
        }
    }
}

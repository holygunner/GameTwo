package com.holygunner.game_two.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.graphics.Color;

import com.holygunner.game_two.figures.Figure;
import com.holygunner.game_two.database.FigureDbSchema.FigureTable;
import com.holygunner.game_two.figures.FigureFactory;
import com.holygunner.game_two.figures.Position;
import com.holygunner.game_two.figures.SemiSquare;
import com.holygunner.game_two.game_mechanics.Cell;

import java.util.UUID;

/**
 * Created by Holygunner on 17.07.2018.
 */

public class FigureCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public FigureCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Figure getFigure(){
        String uuidString = getString(getColumnIndex(FigureTable.Cols.UUID));
        String figureTypeString = getString(getColumnIndex(FigureTable.Cols.FIGURE_TYPE));
        String positionString = getString(getColumnIndex(FigureTable.Cols.POSITION));
        String colorString = getString(getColumnIndex(FigureTable.Cols.COLOR));
        String cellXString = getString(getColumnIndex(FigureTable.Cols.CELL_X));
        String cellYString = getString(getColumnIndex(FigureTable.Cols.CELL_Y));
        String stepLimitString = getString(getColumnIndex(FigureTable.Cols.STEP_LIMIT));


        int x = Integer.parseInt(cellXString);
        int y = Integer.parseInt(cellYString);
        UUID uuid = UUID.fromString(uuidString);
        Position position = Position.getPositionFromString(positionString);
        Cell mCell = new Cell(x, y);
        int color = Integer.parseInt(colorString);
        int stepLimit = Integer.parseInt(stepLimitString);

        Class<?> figureType = FigureFactory.getClassOfFigure(figureTypeString);

        Figure figure = FigureFactory.getInstance().createFigure(uuid, figureType,
                color, position, mCell);

        figure.color = color;
        figure.position = position;
        figure.mCell = mCell;
        figure.stepLimit = stepLimit;

        return figure;
    }
}

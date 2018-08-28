package com.holygunner.halves_into_whole.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.holygunner.halves_into_whole.database.FigureDbSchema.FigureTable;

public class FigureBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String FIGURE_BASE_NAME = "figureBase.db";

    public FigureBaseHelper(Context context) {
        super(context, FIGURE_BASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + FigureTable.NAME + "(" +
                " _id integer primary key autoincrement, "
                + FigureTable.Cols.UUID + ", "
                + FigureTable.Cols.FIGURE_TYPE + ", "
                + FigureTable.Cols.POSITION + ", "
                + FigureTable.Cols.COLOR + ", "
                + FigureTable.Cols.CELL_X + ", "
                + FigureTable.Cols.CELL_Y + ", "
                + FigureTable.Cols.STEP_LIMIT
                + ")"
                );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

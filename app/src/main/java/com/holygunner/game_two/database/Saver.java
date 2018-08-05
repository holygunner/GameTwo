package com.holygunner.game_two.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import com.holygunner.game_two.figures.Figure;
import com.holygunner.game_two.figures.FigureFactory;
import com.holygunner.game_two.game_mechanics.*;
import com.holygunner.game_two.database.FigureDbSchema.FigureTable;

import java.util.ArrayList;
import java.util.List;

public class Saver {
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static final String IS_GAME_STARTED_KEY = "is_game_started_key";
    public static final String GAMER_COUNT_KEY = "gamer_count_key";
    public static final String MAX_SCORE_KEY = "max_score_key";
    public static final String IS_TURN_BUTTON_CLICKABLE_KEY = "is_turn_button_clickable_key";

    public Saver (Context context){
        mContext = context.getApplicationContext();
        mDatabase = new FigureBaseHelper(mContext).getWritableDatabase();
    }

    public void addFigure(Figure figure){
        ContentValues values = getContentValues(figure);
        mDatabase.insert(FigureTable.NAME, null, values);
    }

    public void updateFigure(Figure figure){
        String uuidString = figure.getUUID().toString();
        ContentValues values = getContentValues(figure);

        mDatabase.update(FigureTable.NAME, values,
                FigureTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

    public static boolean isSaveExists(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(IS_GAME_STARTED_KEY, false);
    }

    public static void writeIsSaveExists(Context context, boolean isGameStarted){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(IS_GAME_STARTED_KEY, isGameStarted)
                .apply();
    }

    public void saveToSQLiteDatabase(Desk desk){
        if (desk.deskToMultiArr() != null){
            Figure arr[][] = desk.deskToMultiArr();

            clearSQLiteDatabase();

            for (int x=0; x < arr.length; ++x){
                for (int y=0; y < arr[x].length; ++y){
                    if (arr[x][y] != null){
                        Figure figure = arr[x][y];
                        addFigure(figure);
                    }
                }
            }
        }
    }

    public void writeIsTurnButtonClickable(Context context, boolean isClickable){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(IS_TURN_BUTTON_CLICKABLE_KEY, isClickable)
                .apply();
    }

    public void reset(Context context){
        resetIsTurnButtonClickable(context);
        resetGamerCount(context);
    }

    public static boolean readIsTurnButtonClickable(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(IS_TURN_BUTTON_CLICKABLE_KEY, true);
    }

    public void writeGamerCount(Context context, int gamerCount){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(GAMER_COUNT_KEY, gamerCount)
                .apply();

        writeMaxScore(context, gamerCount);
    }

    public static int readMaxScore(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(MAX_SCORE_KEY, 0);
    }

    public static int readGamerCount(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(GAMER_COUNT_KEY, 0);
    }

    public Figure[] loadFigures(){
        List<Figure> figureList = new ArrayList<>();

        FigureCursorWrapper cursor = queryFigures(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                figureList.add(cursor.getFigure());
                cursor.moveToNext();
            }
        }   finally {
            cursor.close();
        }

        return figureList.toArray(new Figure[figureList.size()]);
    }

    public void clearSQLiteDatabase(){
        mDatabase.execSQL("delete from " + FigureTable.NAME);
    }

    private static ContentValues getContentValues (Figure figure){
        ContentValues values = new ContentValues();
        values.put(FigureTable.Cols.UUID, figure.getUUID().toString());
        values.put(FigureTable.Cols.FIGURE_TYPE, FigureFactory.figureTypeToString(figure));
        values.put(FigureTable.Cols.POSITION, figure.getPosition().toString());
        values.put(FigureTable.Cols.COLOR, figure.getColor());
        values.put(FigureTable.Cols.CELL_X, figure.getCell().getX());
        values.put(FigureTable.Cols.CELL_Y, figure.getCell().getY());
        values.put(FigureTable.Cols.STEP_LIMIT, figure.getStepLimit());

        return values;
    }

    private FigureCursorWrapper queryFigures(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(FigureTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null);
        return new FigureCursorWrapper(cursor);
    }

    private void resetGamerCount(Context context){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(GAMER_COUNT_KEY, 0)
                .apply();
    }

    private void writeMaxScore(Context context, int gamerCount){
        int maxScore = readMaxScore(context);

        if (maxScore < gamerCount){
            PreferenceManager.getDefaultSharedPreferences(context)
                    .edit()
                    .putInt(MAX_SCORE_KEY, gamerCount)
                    .apply();
        }
    }

    private void resetIsTurnButtonClickable(Context context){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(IS_TURN_BUTTON_CLICKABLE_KEY, true)
                .apply();
    }
}

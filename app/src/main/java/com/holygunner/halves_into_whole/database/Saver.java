package com.holygunner.halves_into_whole.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

import com.holygunner.halves_into_whole.figures.Figure;
import com.holygunner.halves_into_whole.figures.FigureFactory;
import com.holygunner.halves_into_whole.game_mechanics.*;
import com.holygunner.halves_into_whole.database.FigureDbSchema.FigureTable;
import com.holygunner.halves_into_whole.values.LevelsValues;

import java.util.ArrayList;
import java.util.List;

public class Saver {
    private Context mContext;
    private GameManager mGameManager;
    private SQLiteDatabase mDatabase;

    private static final String IS_TURN_BUTTON_CLICKABLE_KEY = "is_turn_button_clickable_key";
    private static final String CURRENT_LEVEL_KEY = "current_level";
    private static final String MAX_SCORE_KEY = "max_score_key";
    private static final String MAX_LEVEL_KEY = "max_level";
    private static final String MAX_LEVEL_COUNT_KEY = "max_level_count";
    private static final String IS_SAVE_EXISTS = "is_save_exists";
    private static final String SOUND_BUTTON_STATE = "sound_button_state";

    public Saver (GameManager gameManager, Context context){
        mContext = context.getApplicationContext();
        mGameManager = gameManager;
        mDatabase = new FigureBaseHelper(mContext).getWritableDatabase();
    }

    private void addFigure(Figure figure){
        ContentValues values = getContentValues(figure);
        mDatabase.insert(FigureTable.NAME, null, values);
    }

    public static boolean readIsSoundOn(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(SOUND_BUTTON_STATE, true);
    }

    public static void writeInvertedSoundButtonState(Context context){
        boolean state = readIsSoundOn(context);

        state = !state;

        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(SOUND_BUTTON_STATE, state)
                .apply();
    }

    public void writeGameProgress(GamePlay gamePlay){ // save max open level and its count
        int levelNumb = gamePlay.getLevelNumb();
        int levelCount = gamePlay.getLevel().getGamerCount();

        int[] lastSavedScore = readMaxLevelAndCount(mContext);

        if (levelNumb >= readMaxLevel(mContext)) {
            boolean isLevelIncreased = increaseMaxLevel(levelNumb);
            Log.i("TAG", "Level was increased: " + isLevelIncreased);
//            if ((levelCount >= lastSavedScore[1]) && levelCount < LevelsValues.LEVELS_ROUNDS[levelNumb]) {
                if ((levelCount >= lastSavedScore[1])) {
                PreferenceManager.getDefaultSharedPreferences(mContext)
                        .edit()
                        .putInt(MAX_LEVEL_COUNT_KEY, levelCount)
                        .apply();

                saveToSQLiteDatabase(gamePlay.getDesk());
                writeSaveExists(true);
                writeIsTurnButtonClickable(gamePlay.isTurnAvailable());
            }
        }
    }

    private void writeIsTurnButtonClickable(boolean isClickable){
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .edit()
                .putBoolean(IS_TURN_BUTTON_CLICKABLE_KEY, isClickable)
                .apply();
    }

    public boolean readIsTurnButtonClickable() {
//        if (isLevelMax(mContext, mGameManager.getGamePlay().getLevelNumb())) {
//            if (!readSaveExists(mContext)) {
//                return true;
//            }
//
//            return PreferenceManager.getDefaultSharedPreferences(mContext)
//                    .getBoolean(IS_TURN_BUTTON_CLICKABLE_KEY, true);
//        }   else {
//            return true;
//        }

        return !isLevelMax(mContext, mGameManager.getGamePlay().getLevelNumb())
                || !readSaveExists(mContext)
                || PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(IS_TURN_BUTTON_CLICKABLE_KEY, true);
    }

    private boolean increaseMaxLevel(int levelNumb){
        int maxLevel = readMaxLevel(mContext);
        if ((levelNumb > maxLevel) && (maxLevel < LevelsValues.LEVELS_NAMES.length - 1)){
            PreferenceManager.getDefaultSharedPreferences(mContext)
                    .edit()
                    .putInt(MAX_LEVEL_KEY, maxLevel + 1)
                    .apply();
            return true;
        }   else {
            return false;
        }
    }

    public void resetLevelProgress(){ // if current level is max then max level count will be reset
        if (mGameManager.getGamePlay().getLevelNumb()
                >= readMaxLevel(mContext)
                && mGameManager.getGamePlay().getLevel().getGamerCount()
                >= readMaxLevelAndCount(mContext)[1]) {
            resetMaxLevelCount();
            clearSQLiteDatabase();
            writeSaveExists(false);
        }
    }

    private void resetMaxLevelCount(){
        PreferenceManager.getDefaultSharedPreferences(mContext)
                .edit()
                .putInt(MAX_LEVEL_COUNT_KEY, 0)
                .apply();
    }

    public void writeSaveExists(boolean isExists) {
        if (isLevelMax(mContext, mGameManager.getGamePlay().getLevelNumb())
                && mGameManager.getGamePlay().getLevel().getGamerCount() >= readMaxLevelAndCount(mContext)[1]) {
            PreferenceManager.getDefaultSharedPreferences(mContext)
                    .edit()
                    .putBoolean(IS_SAVE_EXISTS, isExists)
                    .apply();
        }
    }

    public static boolean readSaveExists(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(IS_SAVE_EXISTS, false);
    }

    public static int[] readMaxLevelAndCount(Context context){
        int[] maxScore = new int[2];
        maxScore[0] = readMaxLevel(context);
        maxScore[1] = PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(MAX_LEVEL_COUNT_KEY, 0);
        return maxScore;
    }

    public static int readMaxLevel(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(MAX_LEVEL_KEY, 0);
    }

    public static boolean isLevelMax(Context context, int level){
        return (level == readMaxLevel(context));
    }

    public static boolean isSaveExists(Context context){
        return (readMaxLevelAndCount(context)[1] > 0);
    }

    private void saveToSQLiteDatabase(Desk desk){
        if (desk.deskToMultiArr() != null){
            Figure arr[][] = desk.deskToMultiArr();

            clearSQLiteDatabase();

            for (Figure[] anArr : arr) {
                for (Figure anAnArr : anArr) {
                    if (anAnArr != null) {
                        addFigure(anAnArr);
                    }
                }
            }
        }
    }


    public static int getScore(Context context){
        int score = 0;
        int[] maxScore = readMaxLevelAndCount(context);
        score += maxScore[1];
        int maxLevelNumb = maxScore[0];

        if (maxLevelNumb > 0) {
            for (int i = 0; i < (maxLevelNumb); i++) {
                score += LevelsValues.LEVELS_ROUNDS[i];
            }
        }

        int savedScore = readScore(context);

        if (score > savedScore){
            writeScore(context, score);
            return score;
        }   else {
            return savedScore;
        }
    }

    private static void writeScore(Context context, int score){
        PreferenceManager.getDefaultSharedPreferences(context)
                    .edit()
                    .putInt(MAX_SCORE_KEY, score)
                    .apply();

    }

    private static int readScore(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(MAX_SCORE_KEY, 0);
    }

    public Figure[] loadFigures(){
        List<Figure> figureList = new ArrayList<>();

        FigureCursorWrapper cursor = queryFigures();

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

    private void clearSQLiteDatabase(){
        mDatabase.execSQL("delete from " + FigureTable.NAME);
    }

    public static void writeLevel(Context context, int level){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(CURRENT_LEVEL_KEY, level)
                .apply();
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

    private FigureCursorWrapper queryFigures(){
        Cursor cursor = mDatabase.query(FigureTable.NAME,
                null,
                null,
                null,
                null,
                null,
                null);
        return new FigureCursorWrapper(cursor);
    }
}

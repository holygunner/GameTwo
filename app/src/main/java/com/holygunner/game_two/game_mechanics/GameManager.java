package com.holygunner.game_two.game_mechanics;

import android.content.Context;
import android.util.Log;

import com.holygunner.game_two.figures.Figure;
import com.holygunner.game_two.database.*;
import com.holygunner.game_two.values.DeskValues;

public class GameManager {
    private static final String TAG = "Log";
    private Context mContext;
    private Desk mDesk;
    private GamePlay mGamePlay;
    private Saver mSaver;

    public GameManager(Context context){
        mContext = context;
    }

    public static int cellToPosition(Cell cell){
        int desk_width = DeskValues.DESK_WIDTH;
        int desk_height = DeskValues.DESK_HEIGHT;
        int x = cell.getX();
        int y = cell.getY();

        int position = y*desk_width+x;

        return position;
    }

    public static Cell positionToCell(int position){
        int desk_width = DeskValues.DESK_WIDTH;
        int deskHeight = DeskValues.DESK_HEIGHT;

        int y = position/desk_width;
        int x = position%desk_width;

        return new Cell(x,y);
    }

    public void startOrResumeGame(boolean isOpenSave){
        mSaver = new Saver(mContext);

        if (isOpenSave){
            // open SQLite DB
            mGamePlay = new GamePlay(mDesk, mSaver, mContext);
            mDesk = mGamePlay.loadDesk(mSaver.loadFigures());
            Log.i(TAG, "GAME LOAD");
        }   else {
            // create new game
            mSaver.reset(mContext);
            mGamePlay = new GamePlay(mDesk, mSaver, mContext);
            mDesk = mGamePlay.createNewDesk();
            Log.i(TAG, "NEW GAME");
        }
    }

    public boolean save(){
        if (!getGamePlay().isGameStarted()){ // если первый шаг не был сделан, сейв не осуществляется
            Saver.writeIsSaveExists(mContext, getGamePlay().isGameStarted());
            return false;
        }

        if (!getGamePlay().isGameContinue()){ // если игрок проиграл, сейв не осуществляется и последний сейв удаляется
            mSaver.clearSQLiteDatabase();
            Saver.writeIsSaveExists(mContext, false);
            return false;
        }

        mSaver.saveToSQLiteDatabase(mDesk);
        mSaver.writeGamerCount(mContext, mGamePlay.getGamerCount());
        mSaver.writeIsTurnButtonClickable(mContext, mGamePlay.isTurnAvailable());

        Log.i(TAG, "Save is succesful");
        return true;
    }

    public GamePlay getGamePlay(){
        return mGamePlay;
    }

    public Desk getDesk(){
        return mDesk;
    }

    public Saver getSaver(){
        return mSaver;
    }
}
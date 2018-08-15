package com.holygunner.game_two.game_mechanics;

import android.content.Context;
import android.util.Log;

import com.holygunner.game_two.database.*;

public class GameManager {
    private static final String TAG = "Log";
    private Context mContext;
    private Desk mDesk;
    private GamePlay mGamePlay;
    private Saver mSaver;

    public GameManager(Context context){
        mContext = context;
    }

    public void startOrResumeGame(int levelNumb){
        mSaver = new Saver(this, mContext);

        if (Saver.isLevelMax(mContext, levelNumb) && mSaver.readSaveExists(mContext)){
            // open SQLite DB
            mGamePlay = new GamePlay(mContext, mDesk, mSaver, levelNumb);
            mDesk = mGamePlay.loadDesk(mSaver.loadFigures());
            Log.i(TAG, "GAME LOAD");
        }   else {
            // create new game
            mSaver.reset();
            mGamePlay = new GamePlay(mContext, mDesk, mSaver, levelNumb);
            mDesk = mGamePlay.createNewDesk();
//            mDesk = mGamePlay.createDemoDesk();
            Log.i(TAG, "NEW GAME");
        }
    }

    public boolean save(){
//        if (!getGamePlay().isGameStarted()){ // there is no save if the first step or turn is not complete
//            Saver.writeIsSaveExists(mContext, getGamePlay().isGameStarted());
//            return false;
//        }

//        if (!Saver.isLevelMax(mContext, getGamePlay().getLevelNumb())){ // there is no save
//            // if the current level is not max
//            return false;
//        }



        if (!getGamePlay().isGameContinue()){ // there is no save if a gamer loses, also the last save will delete
//            mSaver.clearSQLiteDatabase();
//            Saver.writeIsSaveExists(mContext, false);
            return false;
        }

        mSaver.writeGameProgress(mGamePlay);


//        mSaver.saveToSQLiteDatabase(mDesk);


//        mSaver.writeCurrentLevel();
        mSaver.writeGamerCount(mGamePlay.getLevel().getGamerCount());

//        mSaver.writeIsTurnButtonClickable(mGamePlay.isTurnAvailable());

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
package com.holygunner.halves_into_whole.game_mechanics;

import android.content.Context;
import android.util.Log;

import com.holygunner.halves_into_whole.database.*;
import com.holygunner.halves_into_whole.values.LevelsValues;

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

        if (Saver.isLevelMax(mContext, levelNumb) && Saver.readSaveExists(mContext)){
            // open SQLite DB
            mGamePlay = new GamePlay(mContext, mDesk, mSaver, levelNumb);
            mDesk = mGamePlay.loadDesk(mSaver.loadFigures());
            Log.i(TAG, "GAME LOAD");
        }   else {
            // create new game
            mGamePlay = new GamePlay(mContext, mDesk, mSaver, levelNumb);
            mDesk = mGamePlay.createNewDesk();
            Log.i(TAG, "NEW GAME");
        }
    }

    public boolean save(){
        if (!getGamePlay().isGameContinue()){ // there is no save if a gamer loses, also the last save will delete
            return false;
        }
        mSaver.writeGameProgress(mGamePlay);

        Log.i(TAG, "Save is succesful");
        return true;
    }

    public void finish(){
        if (mGamePlay.getDesk().isDeskOverload()
                || !(mGamePlay.getLevel().getGamerCount()
                < LevelsValues.LEVELS_ROUNDS[mGamePlay.getLevel().getLevelNumb()])) {
            mSaver.resetLevelProgress();
        }
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
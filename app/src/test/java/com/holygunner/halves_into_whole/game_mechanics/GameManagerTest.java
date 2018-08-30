package com.holygunner.halves_into_whole.game_mechanics;

import android.content.Context;

import com.holygunner.halves_into_whole.database.Saver;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class GameManagerTest {
    Context mContext;
    private Desk mDesk;
    private GamePlay mGamePlay;
    private Saver mSaver;

    @Before
    public void setUp() throws Exception {
        mContext = mock(Context.class);
    }

    @Test
    public void startOrResumeGame() {
        mGamePlay = mock(GamePlay.class);
    }

    @Test
    public void save() {
    }

    @Test
    public void finish() {
    }

    @Test
    public void getGamePlay() {
    }

    @Test
    public void getDesk() {
    }

    @Test
    public void getSaver() {
    }
}
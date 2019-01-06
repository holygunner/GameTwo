package com.holygunner.halves_into_whole;

import android.support.v4.app.Fragment;

public class GameFragmentActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return GameDeskFragment.newInstance();
    }
}

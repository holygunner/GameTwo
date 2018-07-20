package com.holygunner.game_two;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class GameFragmentActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return GameDeskFragment.newInstance();
    }
}

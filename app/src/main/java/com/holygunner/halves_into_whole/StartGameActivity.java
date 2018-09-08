package com.holygunner.halves_into_whole;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import com.holygunner.halves_into_whole.database.*;
import com.holygunner.halves_into_whole.sound.SoundPoolWrapper;

public class StartGameActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String OPEN_LEVEL_NUMB_KEY = "open_level_numb";
    private static final String LOG = "TAG";
    private Button chooseLevelButton;
    private Button gameButton;
    private Button soundButton;
    private TextView maxScoreTextView;
    private SoundPoolWrapper mSoundPoolWrapper;
    private AdView mAdView;

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, GameFragmentActivity.class);

        mSoundPoolWrapper.playSound(SoundPoolWrapper.PRESS_BUTTON);

        switch (view.getId()){
            case R.id.choose_level_button:
                showChooseLevelDialog();
                break;
            case R.id.game_button:
                intent.putExtra(OPEN_LEVEL_NUMB_KEY, Saver.readMaxLevel(getApplicationContext()));
                startActivity(intent);
                break;
            case R.id.sound_on_button:
                setSoundButton();
                break;
            case R.id.about_button:
                showAbout();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_start);

        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        // my AdMob ID: ca-app-pub-5986847491806907~4171322067
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        setAdsBanner();

        chooseLevelButton = findViewById(R.id.choose_level_button);
        setChooseLevelButtonVisibility();
        chooseLevelButton.setOnClickListener(this);
        gameButton = findViewById(R.id.game_button);
        gameButton.setOnClickListener(this);
        Button aboutButton = findViewById(R.id.about_button);
        aboutButton.setOnClickListener(this);
        soundButton = findViewById(R.id.sound_on_button);
        soundButton.setOnClickListener(this);
        setSoundButtonTitle();
        maxScoreTextView = findViewById(R.id.maxScoreTextView);
        updateMaxScore();
        setGameButtonText();
        mSoundPoolWrapper = SoundPoolWrapper.getInstance(this);
    }

    private void setAdsBanner(){
        // Test Device ID: 77E6E08ABF5409D1A37C98C74EE45A35
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("77E6E08ABF5409D1A37C98C74EE45A35") // delete before app release
                .build();
        mAdView.loadAd(adRequest);
    }

    private void setSoundButton(){
        Saver.writeInvertedSoundButtonState(getApplicationContext());
        setSoundButtonTitle();
    }

    private void setSoundButtonTitle(){
        boolean state = Saver.readIsSoundOn(getApplicationContext());

        if (state){
            soundButton.setText(R.string.sound_on);
        }   else {
            soundButton.setText(R.string.sound_off);
        }
    }

    private void setChooseLevelButtonVisibility() {
        if (Saver.readMaxLevel(getApplicationContext()) > 0) {
            chooseLevelButton.setVisibility(View.VISIBLE);
        }   else {
            chooseLevelButton.setVisibility(View.INVISIBLE);
        }
    }

    private void updateMaxScore(){
        int score = Saver.getScore(getApplicationContext());

        if (score > 0){
            String text = getResources().getString(R.string.your_max_score) + " " + score;
            maxScoreTextView.setText(text);
            maxScoreTextView.setVisibility(View.VISIBLE);
        }
    }

    private void setGameButtonText(){ // 1st start: New Game button & invisible Choose level button;
                                    // Save exists: Resume Game button, visible Choose level button
        boolean isSaveExists = Saver.readSaveExists(getApplicationContext());

        if (isSaveExists){
            gameButton.setText(R.string.resume_game);
        }   else {
            gameButton.setText(R.string.new_game);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        mAdView.resume();
        updateMaxScore();
        setChooseLevelButtonVisibility();
        setGameButtonText();
        setSoundButtonTitle();
        mSoundPoolWrapper = SoundPoolWrapper.getInstance(this);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mAdView.destroy();
        mSoundPoolWrapper.release();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAdView.pause();
    }

    private void showChooseLevelDialog(){
        FragmentManager manager = getSupportFragmentManager();
        ChooseLevelDialogFragment dialogFragment = new ChooseLevelDialogFragment();
        dialogFragment.show(manager, LOG);
    }

    private void showAbout(){
        FragmentManager manager = getSupportFragmentManager();
        HelpDialogFragment aboutGameFragment = new HelpDialogFragment();
        aboutGameFragment.show(manager, LOG);
    }
}

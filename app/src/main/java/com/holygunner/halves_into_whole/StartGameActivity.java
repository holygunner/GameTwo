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
    private Button chooseLevelButton;
    private Button gameButton;
    private Button aboutButton;
    private Button soundButton;
    private TextView maxScoreTextView;

    private AdView mAdView;

    private SoundPoolWrapper mSoundPoolWrapper;


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
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        chooseLevelButton = (Button) findViewById(R.id.choose_level_button);
        setChooseLevelButtonVisibility();
        chooseLevelButton.setOnClickListener(this);
        gameButton = (Button) findViewById(R.id.game_button);
        gameButton.setOnClickListener(this);
        aboutButton = (Button) findViewById(R.id.about_button);
        aboutButton.setOnClickListener(this);
        soundButton = (Button) findViewById(R.id.sound_on_button);
        soundButton.setOnClickListener(this);
        setSoundButtonTitle();
        maxScoreTextView = (TextView) findViewById(R.id.maxScoreTextView);
        updateMaxScore();
        setGameButtonText();

        mSoundPoolWrapper = SoundPoolWrapper.getInstance(this);

//        AdView adView = new AdView(this);
//        adView.setAdSize(AdSize.BANNER);
//        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");

    }

    private void setSoundButton(){
        Saver.writeInvertedSoundButtonState(getApplicationContext());
        setSoundButtonTitle();
    }

    private void setSoundButtonTitle(){
        boolean state = Saver.readSoundButtonState(getApplicationContext());

        if (state){
            soundButton.setText("Sound On");
        }   else {
            soundButton.setText("Sound Off");
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
            maxScoreTextView.setText(getResources().getString(R.string.your_max_score) + " " + score);
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
        updateMaxScore();
        setChooseLevelButtonVisibility();
        setGameButtonText();
        setSoundButtonTitle();
        mSoundPoolWrapper = SoundPoolWrapper.getInstance(this);
    }

    protected void onDestroy(){
        super.onDestroy();
        mSoundPoolWrapper.release();
    }

    private void showChooseLevelDialog(){
        FragmentManager manager = getSupportFragmentManager();
        ChooseLevelDialogFragment dialogFragment = new ChooseLevelDialogFragment();
        dialogFragment.show(manager, "TAG");
    }

    private void showAbout(){
        FragmentManager manager = getSupportFragmentManager();
        AboutGameDialogFragment aboutGameFragment = new AboutGameDialogFragment();
        aboutGameFragment.show(manager, "TAG");
    }
}

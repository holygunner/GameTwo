package com.holygunner.game_two;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.holygunner.game_two.database.*;
import com.holygunner.game_two.sound.SoundPoolWrapper;

public class StartGameActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String OPEN_LEVEL_NUMB_KEY = "open_level_numb";
    private Button chooseLevelButton;
    private Button gameButton;
    private Button aboutButton;
    private TextView maxScoreTextView;

    private SoundPoolWrapper mSoundPoolWrapper;


    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, GameFragmentActivity.class);

        switch (view.getId()){
            case R.id.choose_level_button:
                mSoundPoolWrapper.playSound(SoundPoolWrapper.SOUND_ID_1);
                showChooseLevelDialog();
                break;
            case R.id.game_button:
                mSoundPoolWrapper.playSound(SoundPoolWrapper.SOUND_ID_2);
                intent.putExtra(OPEN_LEVEL_NUMB_KEY, Saver.readMaxLevel(getApplicationContext()));
                startActivity(intent);
                break;
            case R.id.about_button:
                mSoundPoolWrapper.playSound(SoundPoolWrapper.SOUND_ID_1);
                showAbout();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_start_test);
        chooseLevelButton = (Button) findViewById(R.id.choose_level_button);
        setChooseLevelButtonVisibility();
        chooseLevelButton.setOnClickListener(this);
        gameButton = (Button) findViewById(R.id.game_button);
        gameButton.setOnClickListener(this);
        aboutButton = (Button) findViewById(R.id.about_button);
        aboutButton.setOnClickListener(this);
        maxScoreTextView = (TextView) findViewById(R.id.maxScoreTextView);
        updateMaxScore();
        setGameButtonText();

        mSoundPoolWrapper = SoundPoolWrapper.getInstance(this);

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
        // show game rules here by DialogFragment or another View
    }
}

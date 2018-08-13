package com.holygunner.game_two;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.holygunner.game_two.database.*;

public class StartGameActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String IS_OPEN_SAVE_KEY = "is open save";
    private Button chooseLevelButton;
    private Button gameButton;
    private Button exitButton;
    private TextView maxScoreTextView;

    @Override
    public void onClick(View view) {
        boolean isSaveExists = Saver.isSaveExists(getApplicationContext());
        Intent intent = new Intent(this, GameFragmentActivity.class);
        switch (view.getId()){
            case R.id.choose_level_button:
                showChooseLevelDialog();
                break;
            case R.id.game_button:
                intent.putExtra(IS_OPEN_SAVE_KEY, isSaveExists);
                startActivity(intent);
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
        chooseLevelButton = (Button) findViewById(R.id.choose_level_button);
        setChooseLevelButtonVisibility();
        chooseLevelButton.setOnClickListener(this);
        gameButton = (Button) findViewById(R.id.game_button);
        gameButton.setOnClickListener(this);
        exitButton = (Button) findViewById(R.id.about_button);
        exitButton.setOnClickListener(this);
        maxScoreTextView = (TextView) findViewById(R.id.maxScoreTextView);
        updateMaxScore();
        setGameButtonText();
    }

    private void setChooseLevelButtonVisibility() {
        if (Saver.readMaxLevel(getApplicationContext()) > 0) {
            chooseLevelButton.setVisibility(View.VISIBLE);
        }   else {
            chooseLevelButton.setVisibility(View.INVISIBLE);
        }
    }

    private void updateMaxScore(){
        int maxScore = Saver.readMaxScore(this);

        if (maxScore>0){
            maxScoreTextView.setText("Your max score: " + maxScore);
            maxScoreTextView.setVisibility(View.VISIBLE);
        }
    }

    private void setChooseLevelButtonVisibility(boolean isSaveExists){
        if (isSaveExists){
            chooseLevelButton.setVisibility(View.VISIBLE);
//            chooseLevelButton.setText(R.string.choose_level);
        }   else {
            chooseLevelButton.setVisibility(View.INVISIBLE);
        }
    }

    private void setGameButtonText(){ // 1st start: New Game button & invisible Choose level button;
                                    // Save exists: Resume Game button, visible Choose level button
        boolean isSaveExists = Saver.isSaveExists(getApplicationContext());
//        setChooseLevelButtonVisibility(isSaveExists);

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
    }

    private void showChooseLevelDialog(){
        FragmentManager manager = getSupportFragmentManager();
        ChooseLevelDialogFragment dialogFragment = new ChooseLevelDialogFragment();
        dialogFragment.show(manager, "TAG");
    }

    private void exit(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void showAbout(){
        // show game rules here
    }
}

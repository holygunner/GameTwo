package com.holygunner.game_two;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.holygunner.game_two.database.*;

public class StartGameActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String IS_OPEN_SAVE_KEY = "is open save";

    private Button resetGameButton;
    private Button gameButton;
    private Button exitButton;
    private TextView maxScoreTextView;

    @Override
    public void onClick(View view) {
        boolean isSaveExists = Saver.isSaveExists(getApplicationContext());
        Intent intent = new Intent(this, GameFragmentActivity.class);

        if (isSaveExists){
            resumeGameIntent(intent, view);
        }   else {
            newGameIntent(intent, view);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        resetGameButton = (Button) findViewById(R.id.reset_game_button);
        resetGameButton.setOnClickListener(this);
        gameButton = (Button) findViewById(R.id.game_button);
        gameButton.setOnClickListener(this);
        exitButton = (Button) findViewById(R.id.exit_button);
        exitButton.setOnClickListener(this);

        maxScoreTextView = (TextView) findViewById(R.id.maxScoreTextView);
        updateMaxScore();
        setButtonsOrder();
    }

    private void updateMaxScore(){
        int maxScore = Saver.readMaxScore(this);

        if (maxScore>0){
            maxScoreTextView.setText("Your max score: " + maxScore);
            maxScoreTextView.setVisibility(View.VISIBLE);
        }
    }

    private void setResetGameOptionVisibility(boolean isSaveExists){
        if (isSaveExists){
            resetGameButton.setVisibility(View.VISIBLE);
            resetGameButton.setText(R.string.reset_game);
        }   else {
            resetGameButton.setVisibility(View.INVISIBLE);
        }
    }

    private void setButtonsOrder(){ // 1st start: New Game button & invisible Reset Game option;
                                    // Save exists: Resume Game button, visible Reset Game option
        boolean isSaveExists = Saver.isSaveExists(getApplicationContext());
        setResetGameOptionVisibility(isSaveExists);

        if (isSaveExists){
            gameButton.setText(R.string.resume_game);
            resetGameButton.setText(R.string.reset_game);
        }   else {
            gameButton.setText(R.string.new_game);
        }

    }

    @Override
    protected void onResume(){
        super.onResume();
        updateMaxScore();
        setButtonsOrder();
    }

    private void resumeGameIntent(Intent intent, View view){
        switch (view.getId()){
            case R.id.reset_game_button:
                intent.putExtra(IS_OPEN_SAVE_KEY, false);
                startActivity(intent);
                break;
            case R.id.game_button:
                intent.putExtra(IS_OPEN_SAVE_KEY, true);
                startActivity(intent);
                break;
            case R.id.exit_button:
                exit();
                break;
        }
    }

    private void newGameIntent(Intent intent, View view){
        switch (view.getId()){
            case R.id.game_button:
                intent.putExtra(IS_OPEN_SAVE_KEY, false);
                startActivity(intent);
                break;
            case R.id.exit_button:
                exit();
                break;
        }
    }

    private void exit(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory( Intent.CATEGORY_HOME );
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}

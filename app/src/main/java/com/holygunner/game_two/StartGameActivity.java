package com.holygunner.game_two;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.holygunner.game_two.database.*;

public class StartGameActivity extends AppCompatActivity implements View.OnClickListener {
    private Button resumeButton;
    private Button newGameButton;
    private Button exitButton;

    private TextView maxScoreTextView;

    public static final String IS_OPEN_SAVE_KEY = "is open save";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        resumeButton = (Button) findViewById(R.id.start_game_button);
        resumeButton.setOnClickListener(this);
        newGameButton = (Button) findViewById(R.id.new_game_button);
        newGameButton.setOnClickListener(this);
        exitButton = (Button) findViewById(R.id.exit_button);
        exitButton.setOnClickListener(this);

        maxScoreTextView = (TextView) findViewById(R.id.maxScoreTextView);
        updateMaxScore();

        setResumeButtonVisibility();
    }

    private void updateMaxScore(){
        int maxScore = Saver.readMaxScore(this);

        if (maxScore>0){
            maxScoreTextView.setText("Your max score: " + maxScore);
            maxScoreTextView.setVisibility(View.VISIBLE);
        }
    }

    private void setResumeButtonVisibility(){
        boolean isSaveExists = Saver.isSaveExists(getApplicationContext());

        if (isSaveExists){
            resumeButton.setVisibility(View.VISIBLE);
        }   else {
            resumeButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        updateMaxScore();
        setResumeButtonVisibility();
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, GameFragmentActivity.class);

        switch (view.getId()){
            case R.id.start_game_button:
                intent.putExtra(IS_OPEN_SAVE_KEY, true);
                startActivity(intent);
                break;
            case R.id.new_game_button:
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

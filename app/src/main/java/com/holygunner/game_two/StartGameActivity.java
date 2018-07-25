package com.holygunner.game_two;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.holygunner.game_two.database.*;

public class StartGameActivity extends AppCompatActivity implements View.OnClickListener {
    private Button resumeButton;
    private Button newGameButton;
    private Button exitButton;

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

        setResumeButtonVisibility();
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
                finish();
                System.exit(0);
                break;
        }
    }
}

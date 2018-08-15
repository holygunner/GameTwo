package com.holygunner.game_two;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Window;

import com.holygunner.game_two.database.Saver;
import com.holygunner.game_two.values.LevelsValues;

import java.util.Arrays;

public class ChooseLevelDialogFragment extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(R.drawable.gradient_blue_left_rounded_corners);
    }

    @Override
    public Dialog onCreateDialog(Bundle onSaveInstanceState){
        String[] levelNames = getAvailableLevelsNames();
        if (levelNames.length == 0){
            levelNames[0] = LevelsValues.LEVELS_NAMES[0];
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

//                builder.setView(inflater.inflate(R.layout.fragment_dialog, null))
                    builder
                        .setTitle(R.string.select_your_level)
                        .setItems(levelNames, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                runLevel(which);
                            }
                        });

        return builder.create();
    }


//    private void runLevel(int which){
//        Saver.writeIsSaveExists(getContext(), false);
//        Saver.writeLevel(getContext(), which);
//        Intent intent = new Intent(getActivity(), GameFragmentActivity.class);
//        startActivity(intent);
//    }

    private void runLevel(int which){
//        Saver.writeIsSaveExists(getContext(), false);
        Saver.writeLevel(getContext(), which);
        Intent intent = new Intent(getActivity(), GameFragmentActivity.class);
        intent.putExtra(StartGameActivity.OPEN_LEVEL_NUMB_KEY, which);
        startActivity(intent);
    }

    private String[] getAvailableLevelsNames(){
        int maxLevelNumb = Saver.readMaxLevel(getContext());
        String[] availableLevelNames = Arrays.copyOfRange(LevelsValues.LEVELS_NAMES, 0, maxLevelNumb + 1);

        return availableLevelNames;
    }


}

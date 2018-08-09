package com.holygunner.game_two;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.holygunner.game_two.database.Saver;
import com.holygunner.game_two.values.LevelsValues;

public class ChooseLevelDialogFragment extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.dialog);
    }

    @Override
    public Dialog onCreateDialog(Bundle onSaveInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.choose_your_level)
                .setItems(LevelsValues.LEVELS_NAMES, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        runLevel(which);
                    }
                });

        return builder.create();
    }

    private void runLevel(int which){
        Saver.resetSave(getContext());
        Saver.writeLevel(getContext(), which);
        Intent intent = new Intent(getActivity(), GameFragmentActivity.class);
        startActivity(intent);
    }
}

package com.holygunner.game_two;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.holygunner.game_two.database.Saver;
import com.holygunner.game_two.values.LevelsValues;

public class ChooseLevelDialogFragment extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle onSaveInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

//                builder.setView(inflater.inflate(R.layout.fragment_dialog, null))
                    builder
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

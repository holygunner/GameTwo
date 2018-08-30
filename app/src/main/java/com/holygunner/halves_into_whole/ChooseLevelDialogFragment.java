package com.holygunner.halves_into_whole;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.app.AlertDialog;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.TextView;

import com.holygunner.halves_into_whole.database.Saver;
import com.holygunner.halves_into_whole.sound.SoundPoolWrapper;
import com.holygunner.halves_into_whole.values.LevelsValues;

import java.util.Arrays;

public class ChooseLevelDialogFragment extends DialogFragment {
    private String[] levelNames;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        levelNames = getAvailableLevelsNames();
        if (levelNames.length == 0){
            levelNames[0] = LevelsValues.LEVELS_NAMES[0];
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(R.drawable.gradient_blue_left_rounded_corners);
    }

    @Override
    public Dialog onCreateDialog(Bundle onSavedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

//                builder.setView(inflater.inflate(R.layout.fragment_dialog, null))
                    builder
//                        .setTitle(R.string.select_your_level)
                        .setItems(levelNames, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                runLevel(which);
                            }
                        });

        TextView title = new TextView(getContext());

        title.setText(R.string.select_your_level);
        title.setPaddingRelative(0, 40, 0, 0);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(ContextCompat.getColor(getContext(), R.color.grey_text_color));
        title.setTextSize(20);

        builder.setCustomTitle(title);

        return builder.create();
    }

    private void runLevel(int which){
        Saver.writeLevel(getContext(), which);
        Intent intent = new Intent(getActivity(), GameFragmentActivity.class);
        intent.putExtra(StartGameActivity.OPEN_LEVEL_NUMB_KEY, which);
        startActivity(intent);
        SoundPoolWrapper.getInstance(getActivity()).playSound(SoundPoolWrapper.LEVEL_START);
    }

    private String[] getAvailableLevelsNames(){
        int maxLevelNumb = Saver.readMaxLevel(getContext());
        String[] availableLevelNames = Arrays.copyOfRange(LevelsValues.LEVELS_NAMES, 0, maxLevelNumb + 1);

        return availableLevelNames;
    }
}

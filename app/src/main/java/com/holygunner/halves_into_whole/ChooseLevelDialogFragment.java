package com.holygunner.halves_into_whole;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.app.AlertDialog;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.Window;
import android.widget.TextView;

import com.holygunner.halves_into_whole.database.Saver;
import com.holygunner.halves_into_whole.sound.SoundPoolWrapper;
import com.holygunner.halves_into_whole.values.LevelsValues;

import java.util.Arrays;
import java.util.Objects;

public class ChooseLevelDialogFragment extends DialogFragment {
    private String[] mLevelNames;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mLevelNames = getAvailableLevelsNames();
    }

    @Override
    public void onStart() {
        super.onStart();

        Window window = getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(R.drawable.gradient_blue_left_rounded_corners);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle onSavedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setItems(mLevelNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        runLevel(which);
                    }
                });

        TextView title = new TextView(getContext());
        title.setText(R.string.select_your_level);
        title.setPaddingRelative(0, 40, 0, 0);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.grey_text_color));
        title.setTextSize(20);
        builder.setCustomTitle(title);
        return builder.create();
    }

    private void runLevel(int which){
        Saver.writeLevel(getContext(), which);
        SoundPoolWrapper.getInstance(getActivity()).playSound(SoundPoolWrapper.LEVEL_START);
        Intent intent = new Intent(getActivity(), GameFragmentActivity.class);
        intent.putExtra(StartGameActivity.OPEN_LEVEL_NUMB_KEY, which);
        startActivity(intent, null);
    }

    private String[] getAvailableLevelsNames(){
        int maxLevelNumb = Saver.readMaxLevel(getContext());
        return Arrays.copyOfRange(LevelsValues.getLevelsNames(Objects.requireNonNull(getContext())), 0, maxLevelNumb + 1);
    }
}
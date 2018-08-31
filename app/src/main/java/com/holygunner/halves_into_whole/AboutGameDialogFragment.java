package com.holygunner.halves_into_whole;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Window;

import com.holygunner.halves_into_whole.sound.SoundPoolWrapper;

public class AboutGameDialogFragment extends DialogFragment {

    @Override
    public void onCreate(Bundle onSavedInstanceState){
        super.onCreate(onSavedInstanceState);
    }

    @Override
    public void onStart(){
        super.onStart();
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(R.drawable.gradient_blue_left_up_corner_rounded_corners);
        }
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle onSavedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder
                .setView(R.layout.about_game_view)
                .setPositiveButton("Got it!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SoundPoolWrapper.getInstance(getContext()).playSound(SoundPoolWrapper.PRESS_BUTTON);
                    }
                });

        return builder.create();
    }
}

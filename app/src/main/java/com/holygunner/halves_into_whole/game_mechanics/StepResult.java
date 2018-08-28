package com.holygunner.halves_into_whole.game_mechanics;

public enum StepResult {
    STEP_UNAVAILABLE ("-1"),
    REPLACE_FIGURE ("0"),
    UNITE_FIGURE ("1"),
    DESK_EMPTY ("2"),
    LEVEL_COMPLETE("3");

    private final String mName;

    StepResult(String name){
        mName = name;
    }

    @Override
    public String toString(){
        return this.mName;
    }
}

package com.holygunner.halves_into_whole.game_mechanics;

import org.jetbrains.annotations.Contract;

public enum StepResult {
    STEP_UNAVAILABLE ("-1"),
    REPLACE_FIGURE ("0"),
    UNITE_FIGURE ("1"),
    DESK_EMPTY ("2"),
    LEVEL_COMPLETE("3"),
    COMBO("4"),
    BONUS ("5");

    private final String NAME;

    StepResult(String name){
        NAME = name;
    }

    @Contract(pure = true)
    @Override
    public String toString(){
        return this.NAME;
    }
}

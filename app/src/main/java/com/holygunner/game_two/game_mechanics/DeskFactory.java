package com.holygunner.game_two.game_mechanics;

import android.content.Context;

// создает доску с обьектами Figure из xml представления

public class DeskFactory {
    private static DeskFactory ourInstance = new DeskFactory();

    public static DeskFactory getInstance() {
        return ourInstance;
    }

    private DeskFactory() {
    }

    private Desk desk;
    private Context context;

    public void createNewDesk(){
    }

    public void loadDesk(){}

    public void setDesk(Desk desk) {
        this.desk = desk;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}

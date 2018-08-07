package com.holygunner.game_two.figures;

public class FigureClassAndColorPair { // available figures and their colors on a separate level
    private Class<?> mFigureType;
    private int mColor;

    public FigureClassAndColorPair(Class<?> figureType, int color){
        mFigureType = figureType;
        mColor = color;
    }

    public Class<?> getFigureClass() {
        return mFigureType;
    }

    public int getColor() {
        return mColor;
    }
}

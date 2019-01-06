package com.holygunner.halves_into_whole.figures;

public class FigureClassAndColorPair { // available figures and their colors on a separate level
    private Class<?> mFigureType;
    private int mColor;

    public FigureClassAndColorPair(Class<?> figureType, int color){
        mFigureType = figureType;
        mColor = color;
    }

    Class<?> getFigureClass() {
        return mFigureType;
    }

    public int getColor() {
        return mColor;
    }
}

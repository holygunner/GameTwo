package com.holygunner.game_two.figures;


import com.holygunner.game_two.game_mechanics.Cell;

import java.util.UUID;

public class FigureFactory {

    public static final String SEMISQUARE = "SemiSquare";
    public static final String SEMICIRCLE = "SemiCircle";

    private static FigureFactory ourInstance = new FigureFactory();

    private FigureFactory() {
    }

    public static FigureFactory getInstance() {
        return ourInstance;
    }

    public Figure createFigure(UUID uuid, Class figureType, int color, Position position, Cell cell){
        Figure figure = getInstanceOfChildClass(uuid, figureType, color, position, cell);
        return figure;
    }

    public static Class<?> getClassOfFigure(String figureType) {
        if (figureType.equals(SEMISQUARE))
            return SemiSquare.class;
        if (figureType.equals(SEMICIRCLE))
            return SemiCircle.class;
        else
            return null;
    }

    public static int getFigureRes(Figure figure){
        int color = figure.color;
        Position position = figure.position;

        if (figure instanceof SemiSquare)
            return SemiSquare.getRes(color, position);
        if (figure instanceof SemiCircle)
            return SemiCircle.getRes(color, position);
        else
            return 0;
    }

    public static String figureTypeToString(Figure figure) {
        if (figure instanceof SemiSquare)
            return SEMISQUARE;
        if (figure instanceof SemiCircle)
            return SEMICIRCLE;
        else
            return null;
    }

    private Figure getInstanceOfChildClass(UUID uuid, Class figureClass, int color,
                                           Position position, Cell cell){
        if (figureClass == SemiSquare.class)
            return new SemiSquare(uuid, color, position, cell);
        if (figureClass == SemiCircle.class)
            return new SemiCircle(uuid, color, position, cell);
        else
            return new Figure(uuid);
    }
}

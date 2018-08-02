package com.holygunner.game_two.figures;


import com.holygunner.game_two.game_mechanics.Cell;

import java.util.UUID;

public class FigureFactory {
    private static FigureFactory ourInstance = new FigureFactory();

    public static FigureFactory getInstance() {
        return ourInstance;
    }

    private FigureFactory() {
    }

    public Figure createFigure(UUID uuid, Class figureType, int color, Position position, Cell cell){
        Figure figure = getInstanceOfChildClass(uuid, figureType, color, position, cell);
//        figure.mCell = cell;
        return figure;
    }

    public Figure createFigure(UUID uuid, String figureType, int color, Position position, Cell cell,
                               int stepLimit){
        Figure figure = getInstanceOfChildClass(uuid, figureType, color, position, cell);
//        figure.mCell = cell;
        figure.setStepLimit(stepLimit);
        return figure;
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

    private Figure getInstanceOfChildClass(UUID uuid, String childClass, int color,
                                           Position position, Cell cell){
        if (childClass.equals("SemiSquare"))
            return new SemiSquare(uuid, color, position, cell);
        if (childClass.equals("Figure"))
            return new Figure(uuid);
        else
            return new Figure(uuid);
    }

    public static final String SEMISQUARE = "SemiSquare";
    public static final String SEMICIRCLE = "SemiCircle";

    public static Class<?> getClassOfFigure(String figureType) {
        if (figureType.equals(SEMISQUARE))
            return SemiSquare.class;
        if (figureType.equals(SEMICIRCLE))
            return SemiCircle.class;
//        if (figure instanceof Figure)
//            return Figure.class;
        else
            return null;
    }

    public static String figureTypeToString(Figure figure) {
        if (figure instanceof SemiSquare)
            return SEMISQUARE;
        if (figure instanceof SemiCircle)
            return SEMICIRCLE;
//        if (figure instanceof Figure)
//            return Figure.class;
        else
            return null;
    }
}

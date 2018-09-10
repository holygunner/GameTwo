package com.holygunner.halves_into_whole.figures;


import com.holygunner.halves_into_whole.game_mechanics.Cell;
import com.holygunner.halves_into_whole.game_mechanics.RandomGenerator;

import java.util.List;
import java.util.UUID;

public abstract class FigureFactory {
    private static final String SEMISQUARE = "SemiSquare";
    private static final String SEMICIRCLE = "SemiCircle";
    private static final String SEMISTAR = "SemiStar";

    public static Figure getRandomFigure(List<Cell> freeCells, RandomGenerator randomGenerator){
        UUID uuid = UUID.randomUUID();
        FigureClassAndColorPair pair = randomGenerator.getRandomFigureTypeAndColorPair();
        Class<?> FigureType = pair.getFigureClass();
        int color = pair.getColor();
        Position position = randomGenerator.getRandomPosition();
        Cell cell = randomGenerator.getRandomCell(freeCells);

        return createFigure(uuid, FigureType, color,
                position, cell);
    }

    public static Figure createFigure(UUID uuid, Class figureType, int color,
                               Position position, Cell cell){
        return getInstanceOfChildClass(uuid, figureType, color, position, cell);
    }

    public static Class<?> getClassOfFigure(String figureType) {
        if (figureType.equals(SEMISQUARE))
            return SemiSquare.class;
        if (figureType.equals(SEMICIRCLE))
            return SemiCircle.class;
        if (figureType.equals(SEMISTAR))
            return SemiStar.class;
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
        if (figure instanceof SemiStar)
            return SemiStar.getRes(color, position);
        else
            return 0;
    }

    public static String figureTypeToString(Figure figure) {
        if (figure instanceof SemiSquare)
            return SEMISQUARE;
        if (figure instanceof SemiCircle)
            return SEMICIRCLE;
        if (figure instanceof SemiStar)
            return SEMISTAR;
        else
            return null;
    }

    private static Figure getInstanceOfChildClass(UUID uuid, Class figureClass, int color,
                                           Position position, Cell cell){
        if (figureClass == SemiSquare.class)
            return new SemiSquare(uuid, color, position, cell);
        if (figureClass == SemiCircle.class)
            return new SemiCircle(uuid, color, position, cell);
        if (figureClass == SemiStar.class)
            return new SemiStar(uuid, color, position, cell);
        else
            return new Figure(uuid);
    }
}

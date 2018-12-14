package com.holygunner.halves_into_whole.figures;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.holygunner.halves_into_whole.game_mechanics.Cell;
import com.holygunner.halves_into_whole.game_mechanics.RandomGenerator;

import org.jetbrains.annotations.Contract;

import java.util.List;
import java.util.UUID;

public abstract class FigureFactory {
    private static final String SEMI_SQUARE = "SemiSquare";
    private static final String SEMI_CIRCLE = "SemiCircle";
    private static final String SEMI_STAR = "SemiStar";

    @NonNull
    public static Figure getRandomFigure(List<Cell> freeCells,
                                         @NonNull RandomGenerator randomGenerator){
        UUID uuid = UUID.randomUUID();
        FigureClassAndColorPair pair = randomGenerator.getRandomFigureTypeAndColorPair();
        Class<?> FigureType = pair.getFigureClass();
        int color = pair.getColor();
        Position position = randomGenerator.getRandomPosition();
        Cell cell = randomGenerator.getRandomCell(freeCells);

        return createFigure(uuid, FigureType, color,
                position, cell);
    }

    @NonNull
    public static Figure createFigure(UUID uuid, Class figureType, int color,
                                      Position position, Cell cell){
        return getInstanceOfChildClass(uuid, figureType, color, position, cell);
    }

    @Nullable
    public static Class<?> getClassOfFigure(@NonNull String figureType) {
        if (figureType.equals(SEMI_SQUARE))
            return SemiSquare.class;
        if (figureType.equals(SEMI_CIRCLE))
            return SemiCircle.class;
        if (figureType.equals(SEMI_STAR))
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

    @Nullable
    @Contract(pure = true)
    public static String figureTypeToString(Figure figure) {
        if (figure instanceof SemiSquare)
            return SEMI_SQUARE;
        if (figure instanceof SemiCircle)
            return SEMI_CIRCLE;
        if (figure instanceof SemiStar)
            return SEMI_STAR;
        else
            return null;
    }

    @NonNull
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

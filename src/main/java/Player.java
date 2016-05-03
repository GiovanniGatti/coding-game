import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;

/**
 * Auto-generated code below aims at helping you parseLine
 * the standard input according to the problem statement.
 **/
final class Player {

    static final int NUMBER_OF_COLUNMS = 6;
    static final int NUMBER_OF_ROWS = 12;

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        // game loop
        while (true) {
            List<Block> next = new ArrayList<>();

            for (int i = 0; i < 8; i++) {
                int colorA = in.nextInt();
                int colorB = in.nextInt();
                next.add(new Block(colorA, colorB));
            }

            // Opponent grid
            for (int i = 0; i < 12; i++) {
                String row = in.next();
            }

            // My current grid
            for (int i = 0; i < 12; i++) {
                String row = in.next();
            }

            // TODO

            System.out.println("0"); // "x": the column in which to drop your blocks
        }
    }

    // TODO: fix this method
    // 1- should not mutate current grid
    // 2- maybe make it part of the Grid class?

    static class ScoreEvaluation {
        private final Grid nextState;
        private final int score;

        ScoreEvaluation(Grid nextState, int score) {
            this.nextState = nextState;
            this.score = score;
        }

        public Grid getNextState() {
            return nextState;
        }

        public int getScore() {
            return score;
        }
    }

    static class Block {
        private final int a, b;

        Block(int a, int b) {
            this.a = a;
            this.b = b;
        }

        public int getA() {
            return a;
        }

        public int getB() {
            return b;
        }
    }

    static class Grid {
        final int numberOfLines;
        final int numberOfColumns;

        private final Map<Cell, Field> grid;
        private final Map<Field, Set<Cell>> mirrorGrid;

        Grid(int numberOfLines, int numberOfColumns) {
            this.numberOfLines = numberOfLines;
            this.numberOfColumns = numberOfColumns;
            this.grid = new HashMap<>();
            this.mirrorGrid = new HashMap<>();
        }

        Grid(Grid current) {
            this.numberOfLines = current.numberOfLines;
            this.numberOfColumns = current.numberOfColumns;
            this.grid = deepCopy(current.grid);
            this.mirrorGrid = mirrorDeepCopy(current.mirrorGrid);
        }

        void parseLine(int i, String line) {
            if (line.length() != numberOfColumns) {
                throw new IllegalStateException("Expected " + numberOfColumns + " columns, " +
                        "but found " + line.length() + " instead.");
            }

            if (i < 0 || i >= numberOfLines) {
                throw new IllegalStateException("Invalid index " + i + " for line <" + line + ">. " +
                        "Maximum number of lines allowed is " + numberOfLines);
            }

            for (int j = 0; j < numberOfColumns; j++) {
                Cell cell = new Cell(i, j);
                Field field = Field.of(line.charAt(j));
                grid.put(cell, field);
                if (mirrorGrid.containsKey(field)) {
                    mirrorGrid.get(field).add(cell);
                } else {
                    mirrorGrid.put(field, new HashSet<>(Collections.singletonList(cell)));
                }
            }
        }

        Field getField(int i, int j) {
            return grid.get(new Cell(i, j));
        }

        Set<Cell> getCells(char value) {
            return mirrorGrid.get(Field.of(value));
        }

        Cell nextAvailableCell(int column) {
            for (int i = 0; i < numberOfLines; i++) {
                if (grid.get(new Cell(i, column)).getType() != Field.Type.EMPTY) {
                    return new Cell(i - 1, column);
                }
            }

            return new Cell(numberOfLines - 1, column);
        }

        // TODO: deal with grouping
        // TODO: count score
        ScoreEvaluation place(Block block, int pos) {
            Grid next = new Grid(this);
            Cell cell = next.nextAvailableCell(pos);
            Cell upper = new Cell(cell.row - 1, cell.column);

            Field a = Field.of(block.getA());
            Field b = Field.of(block.getB());

            next.grid.put(cell, b);
            next.grid.put(upper, a);

            next.mirrorGrid.putIfAbsent(b, new HashSet<>());
            next.mirrorGrid.get(b).add(cell);

            next.mirrorGrid.putIfAbsent(a, new HashSet<>());
            next.mirrorGrid.get(a).add(upper);

            group(next);

            return new ScoreEvaluation(next, 1);
        }

        private static int group(Grid grid) {
            System.out.println(grid.mirrorGrid.get(Field.of('2')));
            return 0;
        }

        // TODO: solution may come from graph structure
        private static Set<Cell> findGroup(Set<Cell> maybeGroup) {

            return new HashSet<>();
        }

        private static Map<Cell, Field> deepCopy(Map<Cell, Field> actual) {
            Map<Cell, Field> copy = new HashMap<>();

            for (Entry<Cell, Field> entry : actual.entrySet()) {
                copy.put(entry.getKey(), entry.getValue());
            }

            return copy;
        }

        private static Map<Field, Set<Cell>> mirrorDeepCopy(Map<Field, Set<Cell>> actual) {
            Map<Field, Set<Cell>> copy = new HashMap<>();

            for (Entry<Field, Set<Cell>> entry : actual.entrySet()) {
                Field key = entry.getKey();
                copy.put(key, new HashSet<>());
                for (Cell cell : actual.get(key)) {
                    copy.get(key).add(cell);
                }
            }

            return copy;
        }
    }

    static class Cell {
        final int row;
        final int column;

        Cell(int row, int column) {
            this.row = row;
            this.column = column;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Cell cell = (Cell) o;
            return Objects.equals(row, cell.row) &&
                    Objects.equals(column, cell.column);
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, column);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Cell{");
            sb.append("row=").append(row);
            sb.append(", column=").append(column);
            sb.append('}');
            return sb.toString();
        }
    }

    static class Field {

        enum Type {
            COLOR, SKULL, EMPTY
        }

        private final char value;
        private final Type type;

        Field(char value, Type type) {
            this.value = value;
            this.type = type;
        }

        char getValue() {
            return value;
        }

        Type getType() {
            return type;
        }

        static Field of(int value) {
            if (value < 0 || value > 5) {
                throw new IllegalStateException("Illegal input value: " + value);
            }

            return of(String.valueOf(value).charAt(0));
        }

        static Field of(char value) {
            switch (value) {
            case '.':
                return new Field(value, Type.EMPTY);
            case '0':
                return new Field(value, Type.SKULL);
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
                return new Field(value, Type.COLOR);
            default:
                throw new IllegalArgumentException("Unknown value: " + value);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Field field = (Field) o;
            return value == field.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }
}

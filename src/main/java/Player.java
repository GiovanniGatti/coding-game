import java.util.*;
import java.util.Map.Entry;

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

        Grid getNextState() {
            return nextState;
        }

        int getScore() {
            return score;
        }
    }

    static class Block {
        private final int a, b;

        Block(int a, int b) {
            this.a = a;
            this.b = b;
        }

        int getA() {
            return a;
        }

        int getB() {
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
        // TODO: worst score if placing outside borders
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

        void remove(Cell cell) {
            //TODO: deal with skull blocks

            int column = cell.column;

            if (grid.get(cell).getType() == Field.Type.EMPTY) {
                throw new IllegalStateException("Cannot remove an empty cell " + cell);
            }

            // If removing topper cell
            if (cell.row == 0) {
                grid.put(new Cell(0, column), Field.empty());
            }

            for (int i = cell.row; i > 0; i--) {
                Cell current = new Cell(i, column);
                Cell upper = new Cell(i - 1, column);
                Field upperField = grid.get(upper);
                //Field is immutable
                grid.put(current, upperField);

                if (upperField.getType() == Field.Type.EMPTY) {
                    break;
                }
            }
        }

        private static int group(Grid grid) {
            Optional<Set<Cell>> maybeGroup = findGroup(new ArrayList<>(grid.mirrorGrid.get(Field.of('2'))));

            if (maybeGroup.isPresent()) {
                Set<Cell> cell = maybeGroup.get();

                return groupScore(cell.size());
            }

            return 0;
        }

        private static int groupScore(int groupSize) {
            if (groupSize < 4) {
                return 0;
            }

            return ((int) ((1 / 6) * Math.pow(groupSize, 2) + (1 / 3) * ((double) groupSize)));
        }

        private static Optional<Set<Cell>> findGroup(List<Cell> cells) {
            //Optimization when it is known that no group larger than 4 may exists
            if (cells.size() < 4) {
                return Optional.empty();
            }

            List<Set<Cell>> groups = new ArrayList<>();

            for (int i = 0; i < cells.size() - 1; i++) {
                Cell current = cells.get(i);

                for (int j = i; j < cells.size(); j++) {
                    Cell next = cells.get(j);

                    if (current.isNeighbor(next)) {

                        Optional<Set<Cell>> maybeInGroup =
                                groups.stream()
                                        .filter(g -> g.contains(current))
                                        .findFirst();

                        if (maybeInGroup.isPresent()) {
                            Set<Cell> group = maybeInGroup.get();
                            group.add(next);
                        } else {
                            Set<Cell> group = new HashSet<>();
                            group.add(current);
                            group.add(next);
                            groups.add(group);
                        }
                    }
                }
            }

            return groups.stream()
                    //Minimum number of inline elements to form a group
                    .filter(g -> g.size() >= 4)
                    .findFirst();
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

        boolean isNeighbor(Cell another) {
            if (another.column == column) {
                return another.row - 1 == row || another.row + 1 == row;
            }

            return another.row == row && (another.column - 1 == column || another.column + 1 == column);
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
            return "(" + row + ", " + column + ")";
        }
    }

    //Immutable class
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

        static Field empty() {
            return new Field('.', Field.Type.EMPTY);
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

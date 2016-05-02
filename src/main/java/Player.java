import java.util.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

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

    private static class Block {
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

        final int numberOfRows;
        final int numberOfColumns;

        private final Map<Cell, Field> grid = new HashMap<>();

        Grid(int numberOfRows, int numberOfColumns) {
            this.numberOfRows = numberOfRows;
            this.numberOfColumns = numberOfColumns;
        }

        void parse(List<String> lines) {
            if (lines.size() != numberOfRows) {
                throw new IllegalStateException("Expected " + numberOfRows + " rows, " +
                        "but found " + lines.size() + " instead.");
            }

            for (int i = 0; i < numberOfRows; i++) {
                String line = lines.get(i);

                if (line.length() != numberOfColumns) {
                    throw new IllegalStateException("Expected " + numberOfColumns + " columns, " +
                            "but found " + line.length() + " instead.");
                }

                for (int j = 0; j < numberOfColumns; j++) {
                    Cell cell = new Cell(i, j);
                    grid.put(cell, Field.of(line.charAt(j)));
                }
            }
        }

        Field getField(int i, int j) {
            return grid.get(new Cell(i, j));
        }
    }

    private static class Cell {
        final int row;
        final int column;

        Cell(int row, int column) {
            this.row = row;
            this.column = column;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Cell cell = (Cell) o;
            return Objects.equals(row, cell.row) &&
                    Objects.equals(column, cell.column);
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, column);
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

        static Field of(char value) {
            switch (value) {
                case '.':
                    return new Field(value, Type.EMPTY);
                case '0':
                    return new Field(value, Type.SKULL);
                default:
                    return new Field(value, Type.COLOR);
            }
        }
    }
}

package gattipg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

final class Player {

    private static final int NUMBER_OF_COLUMNS = 6;
    private static final int NUMBER_OF_ROWS = 12;

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        // game loop
        while (true) {
            List<Block> next = new ArrayList<>();
            Grid grid = new Grid(NUMBER_OF_ROWS, NUMBER_OF_COLUMNS);

            for (int i = 0; i < 8; i++) {
                int colorA = in.nextInt();
                int colorB = in.nextInt();
                next.add(new Block(colorA, colorB));
            }

            // My current grid
            for (int i = 0; i < 12; i++) {
                String row = in.next();
                grid.parseLine(i, row);
            }

            // Opponent grid
            for (int i = 0; i < 12; i++) {
                String row = in.next();
            }

            //Performance issues when computing more than 3 blocks
            List<Action> actions = GamePlanner.run(grid, next.subList(0, 4));

            //Game is lost, do anything...
            if (actions.isEmpty()) {
                System.out.println("0");
            }

            System.out.println(String.valueOf(actions.get(0).getPosition()));
        }
    }

    static class GamePlanner {

        //BFS - brute force best case scenario with single player
        static List<Action> run(Grid currentState, List<Block> incomingBlocks) {

            if (incomingBlocks.isEmpty()) {
                return new ArrayList<>();
            }

            List<List<Action>> simulations = new ArrayList<>();

            for (int j = 0; j < currentState.getNumberOfColumns(); j++) {
                ScoreEvaluation evaluation = currentState.place(incomingBlocks.get(0), j, 3);

                if (evaluation.getScore() != Integer.MIN_VALUE) {

                    List<Action> bestOutcome =
                            run(evaluation.getNextState(),
                                    incomingBlocks.subList(1, incomingBlocks.size()));

                    bestOutcome.add(0, new Action(incomingBlocks.get(0), j, evaluation));

                    simulations.add(bestOutcome);
                }
            }

            int highestScore = Integer.MIN_VALUE;
            List<Action> actionsToPerform = new ArrayList<>();

            for (List<Action> simulation : simulations) {
                int accumulatedScore = 0;

                for (Action action : simulation) {
                    accumulatedScore += action.getEvaluation().getScore();
                }

                if (accumulatedScore > highestScore) {
                    highestScore = accumulatedScore;
                    actionsToPerform = simulation;
                }
            }

            return actionsToPerform;
        }
    }

    static class Action {
        private final Block block;
        private final int position;
        private final ScoreEvaluation evaluation;

        Action(Block block, int position, ScoreEvaluation evaluation) {
            this.block = block;
            this.evaluation = evaluation;
            this.position = position;
        }

        ScoreEvaluation getEvaluation() {
            return evaluation;
        }

        int getPosition() {
            return position;
        }

        @Override
        public String toString() {
            return block + " -> " + String.valueOf(position);
        }
    }

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

        @Override
        public String toString() {
            return "(" + a + ", " + b + ')';
        }
    }

    static class Grid {
        private final int numberOfLines;
        private final int numberOfColumns;

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

        ScoreEvaluation place(Block block, int pos, int rot) {
            Grid next = new Grid(this);

            Cell target = next.nextAvailableCell(pos);
            Field targetField = grid.get(target);

            Cell attached;
            switch (rot) {
                case 0:
                    int col = target.getColumn() + 1;
                    if (col >= numberOfColumns) {
                        throw new IllegalStateException("Cannot place attached cell at pos=" + col);
                    }
                    attached = next.nextAvailableCell(col);
                    break;
                case 1:
                    attached = new Cell(target.getRow() - 1, target.getColumn());
                    break;
                case 2:
                    col = target.getColumn() - 1;
                    if (col < 0) {
                        throw new IllegalStateException("Cannot place attached cell at pos=" + col);
                    }
                    attached = next.nextAvailableCell(col);
                    break;
                case 3:
                    attached = target;
                    target = new Cell(attached.getRow() - 1, attached.getColumn());
                    targetField = grid.get(target);
                    break;
                default:
                    throw new IllegalStateException("Unknown rotation: " + rot);
            }

            Field attachedField = grid.get(attached);

            if (!target.isInsideGrid(this) || !attached.isInsideGrid(this)) {
                return new ScoreEvaluation(null, Integer.MIN_VALUE);
            }

            Field a = Field.of(block.getA());
            Field b = Field.of(block.getB());

            next.grid.put(target, a);
            next.grid.put(attached, b);

            next.mirrorGrid.putIfAbsent(b, new HashSet<>());
            next.mirrorGrid.get(targetField).remove(attached);
            next.mirrorGrid.get(b).add(attached);

            next.mirrorGrid.putIfAbsent(a, new HashSet<>());
            next.mirrorGrid.get(attachedField).remove(target);
            next.mirrorGrid.get(a).add(target);

            int score = group(next);

            return new ScoreEvaluation(next, score);
        }

        void remove(Cell cell) {
            int row = cell.getRow();
            int column = cell.getColumn();

            if (!cell.isInsideGrid(this)) {
                throw new IllegalStateException("Cell " + cell + " outside grid range.");
            }

            Field currentField = grid.get(cell);
            Field.Type currentType = currentField.getType();

            if (currentType == Field.Type.EMPTY) {
                throw new IllegalStateException("Cannot remove an empty cell " + cell);
            }

            // Kill surrounding skull blocks
            if (currentType == Field.Type.COLOR) {

                // kills upper cell
                if (row - 1 >= 0) {
                    Cell upper = new Cell(row - 1, column);
                    if (grid.get(upper).getType() == Field.Type.SKULL) {
                        remove(upper);
                    }
                }

                // kills lower cell
                if (row + 1 < numberOfLines) {
                    Cell lower = new Cell(row + 1, column);
                    if (grid.get(lower).getType() == Field.Type.SKULL) {
                        grid.put(lower, Field.empty());
                        mirrorGrid.get(Field.skull()).remove(lower);
                        mirrorGrid.putIfAbsent(Field.empty(), new HashSet<>());
                        mirrorGrid.get(Field.empty()).add(lower);
                    }
                }

                // kills left cell
                if (column - 1 >= 0) {
                    Cell left = new Cell(row, column - 1);
                    if (grid.get(left).getType() == Field.Type.SKULL) {
                        remove(left);
                    }
                }

                // kills left cell
                if (column + 1 < numberOfColumns) {
                    Cell right = new Cell(row, column + 1);
                    if (grid.get(right).getType() == Field.Type.SKULL) {
                        remove(right);
                    }
                }
            }

            for (int i = row; i >= 0; i--) {
                Cell current = new Cell(i, column);
                Cell upper = new Cell(i - 1, column);
                Field field = grid.get(current);
                Field upperField;

                if (!upper.isInsideGrid(this)) {
                    upperField = Field.empty();
                } else {
                    upperField = grid.get(upper);
                }

                // Field is immutable
                grid.put(current, upperField);
                mirrorGrid.get(field).remove(current);
                mirrorGrid.putIfAbsent(upperField, new HashSet<>());
                mirrorGrid.get(upperField).add(current);

                if (upperField.getType() == Field.Type.EMPTY) {
                    break;
                }
            }
        }

        private static int group(Grid grid) {

            for (char c : Field.COLORS) {
                Set<Cell> set = grid.mirrorGrid.get(Field.of(c));

                if (set != null) {
                    Optional<Set<Cell>> maybeGroup = findGroup(new ArrayList<>(set));

                    if (maybeGroup.isPresent()) {
                        Set<Cell> group = maybeGroup.get();

                        group.stream()
                                .sorted(Comparator.comparingInt(Cell::getRow)
                                        .thenComparing(Comparator.comparingInt(Cell::getColumn)))
                                .forEachOrdered(grid::remove);

                        // Test chaining effect
                        return groupScore(group.size()) + 2 * group(grid);
                    }
                }
            }

            return 0;
        }

        private static int groupScore(int groupSize) {
            if (groupSize < 4) {
                return 0;
            }

            return ((int) ((1.0 / 6) * Math.pow(groupSize, 2) + (1.0 / 3) * groupSize));
        }

        private static Optional<Set<Cell>> findGroup(List<Cell> cells) {
            // Optimization when it is known that no group larger than 4 may exists
            if (cells.size() < 4) {
                return Optional.empty();
            }

            List<Set<Cell>> groups = new ArrayList<>();

            for (int i = 0; i < cells.size() - 1; i++) {
                Cell current = cells.get(i);

                for (int j = i + 1; j < cells.size(); j++) {
                    Cell next = cells.get(j);

                    if (current.isNeighbor(next)) {

                        Optional<Set<Cell>> maybeInGroup =
                                groups.stream()
                                        .filter(g -> g.contains(current) || g.contains(next))
                                        .findFirst();

                        if (maybeInGroup.isPresent()) {
                            Set<Cell> group = maybeInGroup.get();
                            group.add(next);
                            group.add(current);
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
                    // Minimum number of inline elements to form a group
                    .filter(g -> g.size() >= 4)
                    .findFirst();
        }

        int getNumberOfColumns() {
            return numberOfColumns;
        }

        int getNumberOfLines() {
            return numberOfLines;
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
        private final int row;
        private final int column;

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

        boolean isInsideGrid(Grid grid) {
            return (row >= 0 && row < grid.numberOfLines)
                    && (column >= 0 && column < grid.numberOfColumns);
        }

        int getRow() {
            return row;
        }

        int getColumn() {
            return column;
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
            return Objects.equals(row, cell.getRow()) &&
                    Objects.equals(column, cell.getColumn());
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

    // Immutable class
    static class Field {

        enum Type {
            COLOR, SKULL, EMPTY
        }

        static Set<Character> COLORS = Collections.unmodifiableSet(
                new HashSet<>(
                        Arrays.asList('1', '2', '3', '4', '5')));

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
                    return empty();
                case '0':
                    return skull();
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

        static Field skull() {
            return new Field('0', Type.SKULL);
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

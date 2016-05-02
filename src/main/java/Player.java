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
            for (int i = 0; i < 8; i++) {
                int colorA = in.nextInt(); // color of the first block
                int colorB = in.nextInt(); // color of the attached block
            }
            for (int i = 0; i < 12; i++) {
                String row = in.next();
            }
            for (int i = 0; i < 12; i++) {
                String row = in.next(); // One line of the map ('.' = empty, '0' = skull block, '1' to '5' = colored block)
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            System.out.println("0"); // "x": the column in which to drop your blocks
        }
    }

    static class Table {

        Map<Cell, Field> table;

        void addLine(int row, String line){
            for(int i = 0; i < NUMBER_OF_COLUNMS; i++){
                table
            }
        }
    }

    static class Row {
        final int i;
        final List<Cell> cells;

        Row(int i, List<Cell> cells) {
            this.i = i;
            this.cells = cells;
        }
    }

    static class Column {
        final int j;
        final List<Cell> cells;

        Column(int j, List<Cell> cells){
            this.j = j;
            this.cells = cells;
        }
    }

    static class Cell {
        final Row row;
        final Column column;

        Cell(Row row, Column column){
            this.row = row;
            this.column = column;
        }
    }

    static class Field {

    }
}

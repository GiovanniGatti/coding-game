import org.assertj.core.api.AbstractAssert;

class GridAssert extends AbstractAssert<GridAssert, Player.Grid> {

    interface WithTableAssertions {
        default GridAssert assertThat(Player.Grid grid) {
            return new GridAssert(grid);
        }
    }

    private GridAssert(Player.Grid actual) {
        super(actual, GridAssert.class);
    }

    public void isEqualTo(String line) {
        isEqualTo(new String[]{line});
    }

    public void isEqualTo(String... lines) {
        isNotNull();

        for (int i = 0; i < actual.numberOfRows; i++) {
            for (int j = 0; j < actual.numberOfColumns; j++) {
                char expected = lines[i].charAt(j);
                Player.Field field = actual.getField(i, j);
                if (field.getValue() != expected) {
                    failWithMessage(messageOf(actual, lines));
                }
            }
        }
    }

    private static String messageOf(Player.Grid grid, String... lines) {
        StringBuilder builder = new StringBuilder("Expected grid\n");

        for (String line : lines) {
            builder.append(line).append('\n');
        }

        builder.append("but found\n");

        for (int i = 0; i < grid.numberOfRows; i++) {
            for (int j = 0; j < grid.numberOfColumns; j++) {
                builder.append(grid.getField(i, j));
            }
            builder.append('\n');
        }

        builder.append("instead");

        return builder.toString();
    }
}

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.assertj.core.api.WithAssertions;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;


@RunWith(HierarchicalContextRunner.class)
public class PlayerTest implements WithAssertions, GridAssert.WithTableAssertions {

    public class GridTest {

        @Test
        public void throw_ISE_when_exceeding_rows() {
            List<String> lines = Lists.newArrayList(".", ".");

            assertThatThrownBy(() -> new Player.Grid(1, 1).parse(lines))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Expected 1 rows, but found 2 instead.");
        }

        @Test
        public void throw_ISE_when_missing_rows() {
            List<String> lines = Lists.newArrayList(".");

            assertThatThrownBy(() -> new Player.Grid(2, 1).parse(lines))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Expected 2 rows, but found 1 instead.");
        }


        @Test
        public void throw_ISE_when_exceeding_columns() {
            List<String> lines = Lists.newArrayList("..");

            assertThatThrownBy(() -> new Player.Grid(1, 1).parse(lines))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Expected 1 columns, but found 2 instead.");
        }

        @Test
        public void throw_ISE_when_missing_columns() {
            List<String> lines = Lists.newArrayList(".");

            assertThatThrownBy(() -> new Player.Grid(1, 2).parse(lines))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Expected 2 columns, but found 1 instead.");
        }


        @Test
        public void load_table() {
            List<String> lines = Lists.newArrayList(
                    "....",
                    ".1..",
                    ".02.");

            Player.Grid grid = new Player.Grid(3, 4);
            grid.parse(lines);

            assertThat(grid).isEqualTo(
                    "....",
                    ".1..",
                    ".02.");
        }

    }

}
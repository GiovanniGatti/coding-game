import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(HierarchicalContextRunner.class)
public class PlayerTest implements WithAssertions, GridAssert.WithTableAssertions {

    public class GridTest {

        @Test
        public void throw_ISE_when_exceeding_columns() {
            assertThatThrownBy(() -> new Player.Grid(1).parseLine(0, ".."))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Expected 1 columns, but found 2 instead.");
        }

        @Test
        public void throw_ISE_when_missing_columns() {
            assertThatThrownBy(() -> new Player.Grid(2).parseLine(0, "."))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Expected 2 columns, but found 1 instead.");
        }


        @Test
        public void load_table() {
            Player.Grid grid = new Player.Grid(4);
            grid.parseLine(0, "..0.");
            grid.parseLine(1, ".1..");
            grid.parseLine(2, ".22.");

            assertThat(grid).isEqualTo(
                    "..0.",
                    ".1..",
                    ".22.");
        }

    }

}
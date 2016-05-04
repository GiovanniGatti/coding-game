import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(HierarchicalContextRunner.class)
public class PlayerTest implements WithAssertions, GridAssert.WithTableAssertions {

    public class GridTest {

        @Test
        public void throw_ISE_when_exceeding_columns() {
            assertThatThrownBy(() -> new Player.Grid(1, 1).parseLine(0, ".."))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Expected 1 columns, but found 2 instead.");
        }

        @Test
        public void throw_ISE_when_missing_columns() {
            assertThatThrownBy(() -> new Player.Grid(1, 2).parseLine(0, "."))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Expected 2 columns, but found 1 instead.");
        }

        @Test
        public void throw_ISE_when_negative_line_input() {
            assertThatThrownBy(() -> new Player.Grid(1, 1).parseLine(1, "."))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Invalid index 1 for line <.>. Maximum number of lines allowed is 1");
        }

        @Test
        public void throw_ISE_when_too_many_input_lines() {
            assertThatThrownBy(() -> new Player.Grid(1, 1).parseLine(1, "."))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Invalid index 1 for line <.>. Maximum number of lines allowed is 1");
        }

        @Test
        public void load_table() {
            Player.Grid grid = new Player.Grid(3, 4);
            grid.parseLine(0, "..0.");
            grid.parseLine(1, ".1..");
            grid.parseLine(2, ".22.");

            assertThat(grid).isEqualTo(
                    "..0.",
                    ".1..",
                    ".22.");
        }

        @Test
        public void load_mirrored_cells() {
            Player.Grid grid = new Player.Grid(3, 4);
            grid.parseLine(0, "..0.");
            grid.parseLine(1, ".1..");
            grid.parseLine(2, ".22.");

            assertThat(grid.getCells('0')).containsExactlyInAnyOrder(new Player.Cell(0, 2));
            assertThat(grid.getCells('1')).containsExactlyInAnyOrder(new Player.Cell(1, 1));
            assertThat(grid.getCells('2')).containsExactlyInAnyOrder(new Player.Cell(2, 1), new Player.Cell(2, 2));
        }

        @Test
        public void find_columns_next_available_cell_non_empty_cell() {
            Player.Grid grid = new Player.Grid(3, 1);
            grid.parseLine(0, ".");
            grid.parseLine(1, "1");
            grid.parseLine(2, "2");

            Player.Cell available = grid.nextAvailableCell(0);
            assertThat(available).isEqualTo(new Player.Cell(0, 0));
        }

        @Test
        public void find_columns_next_available_cell_on_empty_column() {
            Player.Grid grid = new Player.Grid(3, 1);
            grid.parseLine(0, ".");
            grid.parseLine(1, ".");
            grid.parseLine(2, ".");

            Player.Cell available = grid.nextAvailableCell(0);
            assertThat(available).isEqualTo(new Player.Cell(2, 0));
        }

        @Test
        public void throw_ISE_when_removing_an_empty_cell() {
            Player.Grid grid = new Player.Grid(1, 1);
            grid.parseLine(0, ".");

            assertThatThrownBy(() -> grid.remove(new Player.Cell(0, 0)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot remove an empty cell (0, 0)");
        }

        @Test
        public void remove_most_upper_cell() {
            Player.Grid grid = new Player.Grid(3, 4);
            grid.parseLine(0, "....");
            grid.parseLine(1, ".2..");
            grid.parseLine(2, ".222");

            grid.remove(new Player.Cell(1, 1));

            assertThat(grid).isEqualTo(
                    "....",
                    "....",
                    ".222");
        }

        @Test
        public void remove_lower_cell() {
            Player.Grid grid = new Player.Grid(3, 4);
            grid.parseLine(0, "....");
            grid.parseLine(1, ".2..");
            grid.parseLine(2, ".122");

            grid.remove(new Player.Cell(2, 1));

            assertThat(grid).isEqualTo(
                    "....",
                    "....",
                    ".222");
        }

        @Test
        public void remove_top_cell() {
            Player.Grid grid = new Player.Grid(3, 4);
            grid.parseLine(0, ".2..");
            grid.parseLine(1, ".2..");
            grid.parseLine(2, ".222");

            grid.remove(new Player.Cell(0, 1));

            assertThat(grid).isEqualTo(
                    "....",
                    ".2..",
                    ".222");
        }

        @Test
        public void clear_surrounding_skull_blocks_when_removing_cell() {
            Player.Grid grid = new Player.Grid(3, 4);
            grid.parseLine(0, "....");
            grid.parseLine(1, ".20.");
            grid.parseLine(2, "0222");

            grid.remove(new Player.Cell(2, 1));

            assertThat(grid).isEqualTo(
                    "....",
                    "..0.",
                    ".222");
        }

        @Test
        public void place_non_matching_block() {
            Player.Grid grid = new Player.Grid(3, 4);
            grid.parseLine(0, "....");
            grid.parseLine(1, "....");
            grid.parseLine(2, ".222");

            Player.Block block = new Player.Block(1, 1);

            Player.ScoreEvaluation score = grid.place(block, 0);

            assertThat(score.getNextState()).isEqualTo(
                    "....",
                    "1...",
                    "1222");
        }

        @Test
        public void group_block_after_placement() {
            Player.Grid grid = new Player.Grid(3, 4);
            grid.parseLine(0, "....");
            grid.parseLine(1, "..2.");
            grid.parseLine(2, "..2.");

            Player.Block block = new Player.Block(2, 2);

            Player.ScoreEvaluation score = grid.place(block, 1);

            assertThat(score.getNextState()).isEqualTo(
                    "....",
                    "....",
                    "....");
        }
    }
//
//    @Test
//    public void do_match_vertical_line_group() {
//        Player.Grid grid = new Player.Grid(4);
//        grid.parseLine(0, "..2.");
//        grid.parseLine(1, "..2.");
//        grid.parseLine(2, "..2.");
//        grid.parseLine(3, "..2.");
//    }
//
//    @Test
//    public void do_match_horizontal_line_group() {
//        Player.Grid grid = new Player.Grid(4);
//        grid.parseLine(0, "....");
//        grid.parseLine(1, "2222");
//    }
//
//    @Test
//    public void do_match_s_line_group() {
//        Player.Grid grid = new Player.Grid(4);
//        grid.parseLine(0, "....");
//        grid.parseLine(1, "....");
//        grid.parseLine(2, "....");
//        grid.parseLine(3, "2222");
//    }
}
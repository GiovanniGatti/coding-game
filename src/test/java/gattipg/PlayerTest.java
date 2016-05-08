package gattipg;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import gattipg.Player.Block;
import org.assertj.core.api.WithAssertions;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(HierarchicalContextRunner.class)
public class PlayerTest implements WithAssertions, GridAssert.WithTableAssertions {

    public class GridTest {

        public class GridParsing {
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
            public void do_not_recommend_to_rotate_block_before_left_border() {
                Player.Grid grid = new Player.Grid(3, 4);
                grid.parseLine(0, "....");
                grid.parseLine(1, "....");
                grid.parseLine(2, "....");

                List<Integer> rotations = grid.possibleRotations(new Block(1, 2), 0);

                assertThat(rotations).containsExactlyInAnyOrder(0, 1, 3);
            }

            @Test
            public void do_not_recommend_to_rotate_block_after_right_border() {
                Player.Grid grid = new Player.Grid(3, 4);
                grid.parseLine(0, "....");
                grid.parseLine(1, "....");
                grid.parseLine(2, "....");

                List<Integer> rotations = grid.possibleRotations(new Block(1, 2), 3);

                assertThat(rotations).containsExactlyInAnyOrder(1, 2, 3);
            }

            @Test
            public void recommend_any_rotation_when_inside_grid() {
                Player.Grid grid = new Player.Grid(3, 4);
                grid.parseLine(0, "....");
                grid.parseLine(1, "....");
                grid.parseLine(2, "....");

                List<Integer> rotations = grid.possibleRotations(new Block(1, 2), 2);

                assertThat(rotations).containsExactlyInAnyOrder(0, 1, 2, 3);
            }

            @Test
            public void do_not_recommend_all_rotations_when_blocks_of_same_color() {
                Player.Grid grid = new Player.Grid(3, 4);
                grid.parseLine(0, "....");
                grid.parseLine(1, "....");
                grid.parseLine(2, "....");

                List<Integer> rotations = grid.possibleRotations(new Block(2, 2), 2);

                assertThat(rotations).containsExactlyInAnyOrder(0, 1);
            }
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

        public class RemovingCells {

            @Test
            public void throw_ISE_when_removing_outside_of_range_cell() {
                Player.Grid grid = new Player.Grid(1, 1);
                grid.parseLine(0, ".");

                assertThatThrownBy(() -> grid.remove(new Player.Cell(1, 1)))
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessageContaining("Cell (1, 1) outside grid range.");
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

                assertThat(grid.getCells('.')).containsExactlyInAnyOrder(
                        new Player.Cell(0, 0), new Player.Cell(0, 1), new Player.Cell(0, 2), new Player.Cell(0, 3),
                        new Player.Cell(1, 0), new Player.Cell(1, 1), new Player.Cell(1, 2), new Player.Cell(1, 3),
                        new Player.Cell(2, 0));
                assertThat(grid.getCells('2')).containsExactlyInAnyOrder(
                        new Player.Cell(2, 1), new Player.Cell(2, 2), new Player.Cell(2, 3));
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

                assertThat(grid.getCells('.')).containsExactlyInAnyOrder(
                        new Player.Cell(0, 0), new Player.Cell(0, 1), new Player.Cell(0, 2), new Player.Cell(0, 3),
                        new Player.Cell(1, 0), new Player.Cell(1, 1), new Player.Cell(1, 2), new Player.Cell(1, 3),
                        new Player.Cell(2, 0));
                assertThat(grid.getCells('2')).containsExactlyInAnyOrder(
                        new Player.Cell(2, 1), new Player.Cell(2, 2), new Player.Cell(2, 3));
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

                assertThat(grid.getCells('.')).containsExactlyInAnyOrder(
                        new Player.Cell(0, 0), new Player.Cell(0, 1), new Player.Cell(0, 2), new Player.Cell(0, 3),
                        new Player.Cell(1, 0), new Player.Cell(1, 2), new Player.Cell(1, 3),
                        new Player.Cell(2, 0));
                assertThat(grid.getCells('2')).containsExactlyInAnyOrder(
                        new Player.Cell(1, 1),
                        new Player.Cell(2, 1), new Player.Cell(2, 2), new Player.Cell(2, 3));
            }

            @Test
            public void kill_left_skull_block_when_removing_cell() {
                Player.Grid grid = new Player.Grid(1, 2);
                grid.parseLine(0, "02");

                grid.remove(new Player.Cell(0, 1));

                assertThat(grid).isEqualTo("..");
                assertThat(grid.getCells('.')).containsExactlyInAnyOrder(new Player.Cell(0, 0), new Player.Cell(0, 1));
            }

            @Test
            public void kill_right_skull_block_when_removing_cell() {
                Player.Grid grid = new Player.Grid(1, 2);
                grid.parseLine(0, "20");

                grid.remove(new Player.Cell(0, 0));

                assertThat(grid).isEqualTo("..");
                assertThat(grid.getCells('.')).containsExactlyInAnyOrder(new Player.Cell(0, 0), new Player.Cell(0, 1));
            }

            @Test
            public void kill_upper_skull_block_when_removing_cell() {
                Player.Grid grid = new Player.Grid(2, 1);
                grid.parseLine(0, "0");
                grid.parseLine(1, "2");

                grid.remove(new Player.Cell(1, 0));

                assertThat(grid).isEqualTo(
                        ".",
                        ".");

                assertThat(grid.getCells('.')).containsExactlyInAnyOrder(new Player.Cell(0, 0), new Player.Cell(1, 0));
            }

            @Test
            public void kill_lower_skull_block_when_removing_cell() {
                Player.Grid grid = new Player.Grid(2, 1);
                grid.parseLine(0, "2");
                grid.parseLine(1, "0");

                grid.remove(new Player.Cell(0, 0));

                assertThat(grid).isEqualTo(
                        ".",
                        ".");

                assertThat(grid.getCells('.')).containsExactlyInAnyOrder(new Player.Cell(0, 0), new Player.Cell(1, 0));
            }

            @Test
            public void drop_upper_fields_when_killing_skull_block() {
                Player.Grid grid = new Player.Grid(2, 1);
                grid.parseLine(0, "2");
                grid.parseLine(1, "0");

                grid.remove(new Player.Cell(1, 0));

                assertThat(grid).isEqualTo(
                        ".",
                        "2");

                assertThat(grid.getCells('.')).containsExactlyInAnyOrder(new Player.Cell(0, 0));
                assertThat(grid.getCells('2')).containsExactlyInAnyOrder(new Player.Cell(1, 0));
            }
        }

        public class PlacingBlocks {

            @Test
            public void place_non_matching_block() {
                Player.Grid grid = new Player.Grid(3, 4);
                grid.parseLine(0, "....");
                grid.parseLine(1, "....");
                grid.parseLine(2, ".222");

                Block block = new Block(1, 1);

                Player.ScoreEvaluation score = grid.place(block, 0, 3);

                assertThat(score.getNextState()).isEqualTo(
                        "....",
                        "1...",
                        "1222");
            }

            @Test
            public void group_block_after_placement__square() {
                Player.Grid grid = new Player.Grid(3, 4);
                grid.parseLine(0, "....");
                grid.parseLine(1, "..2.");
                grid.parseLine(2, "..2.");

                Block block = new Block(2, 2);

                Player.ScoreEvaluation score = grid.place(block, 1, 3);

                assertThat(score.getNextState()).isEqualTo(
                        "....",
                        "....",
                        "....");
            }

            @Test
            public void group_block_after_placement__lineup() {
                Player.Grid grid = new Player.Grid(4, 1);
                grid.parseLine(0, ".");
                grid.parseLine(1, ".");
                grid.parseLine(2, "2");
                grid.parseLine(3, "2");

                Block block = new Block(2, 2);

                Player.ScoreEvaluation score = grid.place(block, 0, 3);

                assertThat(score.getNextState()).isEqualTo(
                        ".",
                        ".",
                        ".",
                        ".");
            }

            @Test
            public void group_block_after_placement__horizontal() {
                Player.Grid grid = new Player.Grid(2, 4);
                grid.parseLine(0, "....");
                grid.parseLine(1, ".222");

                Block block = new Block(2, 2);

                Player.ScoreEvaluation score = grid.place(block, 0, 3);

                assertThat(score.getNextState()).isEqualTo(
                        "....",
                        "....");
            }

            @Test
            public void group_block_after_placement__s() {
                Player.Grid grid = new Player.Grid(4, 4);
                grid.parseLine(0, "....");
                grid.parseLine(1, "..2.");
                grid.parseLine(2, "..2.");
                grid.parseLine(3, "..1.");

                Block block = new Block(2, 2);

                Player.ScoreEvaluation score = grid.place(block, 1, 3);

                assertThat(score.getNextState()).isEqualTo(
                        "....",
                        "....",
                        "....",
                        "..1.");
            }

            @Test
            public void group_block_after_placement__cross() {
                Player.Grid grid = new Player.Grid(4, 4);
                grid.parseLine(0, "....");
                grid.parseLine(1, "....");
                grid.parseLine(2, ".2.2");
                grid.parseLine(3, ".121");

                Block block = new Block(2, 2);

                Player.ScoreEvaluation score = grid.place(block, 2, 3);

                assertThat(score.getNextState()).isEqualTo(
                        "....",
                        "....",
                        "....",
                        ".1.1");
            }

            @Test
            public void group_block_after_placement_destroying_skull_blocks() {
                Player.Grid grid = new Player.Grid(4, 4);
                grid.parseLine(0, "....");
                grid.parseLine(1, "..2.");
                grid.parseLine(2, "0.20");
                grid.parseLine(3, "0.00");

                Block block = new Block(2, 2);

                Player.ScoreEvaluation score = grid.place(block, 1, 3);

                assertThat(score.getNextState()).isEqualTo(
                        "....",
                        "....",
                        "....",
                        "...0");
            }

            @Test
            public void group_chaining() {
                Player.Grid grid = new Player.Grid(4, 4);
                grid.parseLine(0, "....");
                grid.parseLine(1, ".1..");
                grid.parseLine(2, ".2..");
                grid.parseLine(3, "1211");

                Block block = new Block(2, 2);

                Player.ScoreEvaluation score = grid.place(block, 2, 3);

                assertThat(score.getNextState()).isEqualTo(
                        "....",
                        "....",
                        "....",
                        "....");
            }

            @Test
            public void lose_when_placing_outside_borders() {
                Player.Grid grid = new Player.Grid(4, 1);
                grid.parseLine(0, ".");
                grid.parseLine(1, "0");
                grid.parseLine(2, "1");
                grid.parseLine(3, "1");

                Block block = new Block(2, 2);

                Player.ScoreEvaluation score = grid.place(block, 0, 3);

                assertThat(score.getNextState()).isNull();
                assertThat(score.getScore()).isEqualTo(Integer.MIN_VALUE);
            }

            @Test
            public void place_rotated_to_left() {
                Player.Grid grid = new Player.Grid(3, 2);
                grid.parseLine(0, "..");
                grid.parseLine(1, "..");
                grid.parseLine(2, "..");

                Block block = new Block(2, 1);

                Player.ScoreEvaluation score = grid.place(block, 0, 0);

                assertThat(score.getNextState()).isEqualTo(
                        "..",
                        "..",
                        "21");
            }

            @Test
            public void place_rotated_to_right() {
                Player.Grid grid = new Player.Grid(3, 2);
                grid.parseLine(0, "..");
                grid.parseLine(1, "..");
                grid.parseLine(2, "..");

                Block block = new Block(2, 1);

                Player.ScoreEvaluation score = grid.place(block, 1, 2);

                assertThat(score.getNextState()).isEqualTo(
                        "..",
                        "..",
                        "12");
            }

            @Test
            public void place_normally() {
                Player.Grid grid = new Player.Grid(3, 1);
                grid.parseLine(0, ".");
                grid.parseLine(1, ".");
                grid.parseLine(2, ".");

                Block block = new Block(2, 1);

                Player.ScoreEvaluation score = grid.place(block, 0, 3);

                assertThat(score.getNextState()).isEqualTo(
                        ".",
                        "2",
                        "1");
            }

            @Test
            public void place_rotated_vertically() {
                Player.Grid grid = new Player.Grid(3, 1);
                grid.parseLine(0, ".");
                grid.parseLine(1, ".");
                grid.parseLine(2, ".");

                Block block = new Block(2, 1);

                Player.ScoreEvaluation score = grid.place(block, 0, 1);

                assertThat(score.getNextState()).isEqualTo(
                        ".",
                        "1",
                        "2");
            }

            @Test
            public void let_right_attached_cell_fall_to_highest_position_on_right_side() {
                Player.Grid grid = new Player.Grid(3, 3);
                grid.parseLine(0, "...");
                grid.parseLine(1, "...");
                grid.parseLine(2, "0..");

                Block block = new Block(2, 1);

                Player.ScoreEvaluation score = grid.place(block, 0, 0);

                assertThat(score.getNextState()).isEqualTo(
                        "...",
                        "2..",
                        "01.");
            }

            @Test
            public void let_left_attached_cell_fall_to_highest_position_on_right_side() {
                Player.Grid grid = new Player.Grid(3, 3);
                grid.parseLine(0, "...");
                grid.parseLine(1, "...");
                grid.parseLine(2, ".0.");

                Block block = new Block(2, 1);

                Player.ScoreEvaluation score = grid.place(block, 1, 2);

                assertThat(score.getNextState()).isEqualTo(
                        "...",
                        ".2.",
                        "10.");
            }

            @Test
            public void throw_ISE_if_placed_outside_left_border() {
                Player.Grid grid = new Player.Grid(3, 2);
                grid.parseLine(0, "..");
                grid.parseLine(1, "..");
                grid.parseLine(2, "..");

                Block block = new Block(2, 1);

                assertThatThrownBy(() -> grid.place(block, 0, 2))
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessageContaining("Cannot place attached cell at pos=-1");
            }

            @Test
            public void throw_ISE_if_placed_outside_right_border() {
                Player.Grid grid = new Player.Grid(3, 2);
                grid.parseLine(0, "..");
                grid.parseLine(1, "..");
                grid.parseLine(2, "..");

                Block block = new Block(2, 1);

                assertThatThrownBy(() -> grid.place(block, 1, 0))
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessageContaining("Cannot place attached cell at pos=2");
            }
        }

        public class GamePlannerTest {

            @Test
            public void place_blocks_together_when_only_four_blocks_of_same_color_is_provided() {
                Player.Grid grid = new Player.Grid(4, 3);
                grid.parseLine(0, "...");
                grid.parseLine(1, "...");
                grid.parseLine(2, "...");
                grid.parseLine(3, "...");

                List<Block> incoming = Lists.newArrayList(new Block(2, 2), new Block(2, 2));

                List<Player.Action> actions = Player.GamePlanner.run(grid, incoming);

                //How to check it?
                System.out.println(actions);
            }

            @Test
            public void do_not_follow_end_game_scenario() {
                Player.Grid grid = new Player.Grid(4, 3);
                grid.parseLine(0, "...");
                grid.parseLine(1, "00.");
                grid.parseLine(2, "00.");
                grid.parseLine(3, "00.");

                List<Block> incoming = Lists.newArrayList(new Block(2, 2), new Block(2, 2));

                List<Player.Action> actions = Player.GamePlanner.run(grid, incoming);

                //How to check it?
                System.out.println(actions);
            }
        }
    }
}
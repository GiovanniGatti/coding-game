# coding-game

 	The Goal

Defeat your opponent by grouping colored blocks.
 	Rules


Each player plays in their own grid 6 cells wide and 12 cells high. Every turn, both players are given two connected blocks, one on top of the other, they must place inside their grid. This is called a pair. The blocks work as follows:
Every pair of block has a random color. The colors are labeled 1 to 5.
You are given the next pairs of blocks to place 8 turns in advance.
Both players are given the same pairs of blocks.
The placement of blocks works as follows:
The blocks are dropped into the grid from above, and stop once they reach the bottom or another block in the same column.
When 4 or more blocks of the same color line up adjacent to each other, they disappear. Blocks connect horizontally or vertically, but not diagonally. The whole group need not be a straight line.
When all groups of the grid have cleared, the blocks above them all fall until they reach the bottom or another block. If this causes new groups to form, those groups will also disappear. This is called a chain.
Aiming for large chains increase your offensive power and lets you automatically attack your opponent.

Attacking works as follows:
As you create groups and perform chains, you will generate nuisance points. As soon as you have enough nuisance points, Skull blocks will appear in the opponent's grid.
Skull blocks act like colored blocks but they will not disappear when they form a group. They are only removed from the grid if a colored block is cleared next to them.
The Skull blocks are dropped into the player's grids in a line, 6 at a time, one in each column.
Skull blocks are labeled 0.

Victory Conditions
Survive your opponent or have a higher score than them after the time limit.

Lose Conditions
Your program provides incorrect output.
Your program times out.
You fail to place the pair of blocks into free cells of your grid.
 	Expert Rules

Groups and chains give score points and nuisance points.
The score points are used as a tie break if both players are still alive after 200 turns.

More information will be available in the next leagues, in which extra rules will be unlocked.
 	Note

The program must first read within an infinite loop, read the contextual data from the standard input and provide to the standard output the desired instructions.
 	Game Input

Input for one game turn
First 8 lines: 2 space separated integers colorA and colorB: the colors of the blocks you must place in your grid by order of appearance. In this league, colorA is always equal to colorB.
Next 12 lines: 6 characters representing one line of your grid, top to bottom.
Next 12 lines: 6 characters representing one line of the opponent's grid, top to bottom.

Each line of the grid can contain the characters:
. for an empty cell.
0 for a skull block.
An integer from 1 to 5 for a colored block.
Output for one game turn
A single line: 1 integer: x for the column in which to drop your pair of blocks. You may append text to your instructions, it will be displayed in the viewer.
Constraints
1 ≤ colorA ≤ 5
colorB = colorA
0 ≤ x < 6
Response time per turn ≤ 100ms
Townatropolis, rooftop of Flame Tower, present day...

The terrifying Doctor Darken is plotting to use the immense power of Artificial Intelligence to subdue and control the entire world with the help of super intelligent Kill-Bots.
Fortunately, Captain Wonderface has managed to intercept the villain just as he is about to trigger the start of his invasion.
A mighty battle for the triumph of Light begins as both Super Humans face each other...
package CodingINteview.HashSet;

import java.util.*;
    import java.util.stream.Collectors;

    public class verifySudokuBoard {

        public static boolean verifySudokuBoard(int[][] board) {
            // Create hash sets for each row, column, and subgrid
            Set<Integer>[] rowSets = new HashSet[9];
            Set<Integer>[] columnSets = new HashSet[9];
            Set<Integer>[][] subgridSets = new HashSet[3][3];

            // Initialize the arrays with empty HashSet instances
            for (int i = 0; i < 9; i++) {
                rowSets[i] = new HashSet<>();
                columnSets[i] = new HashSet<>();
            }
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    subgridSets[r][c] = new HashSet<>();
                }
            }

            // Iterate through every cell on the board
            for (int r = 0; r < 9; r++) {
                for (int c = 0; c < 9; c++) {
                    int num = board[r][c];

                    // Skip empty cells
                    if (num == 0) {
                        continue;
                    }

                    // Check if 'num' has been seen in the current row, column, or subgrid
                    if (rowSets[r].contains(num)) {
                        return false;
                    }
                    if (columnSets[c].contains(num)) {
                        return false;
                    }
                    if (subgridSets[r / 3][c / 3].contains(num)) {
                        return false;
                    }

                    // Mark this value as seen by adding it to its corresponding sets
                    rowSets[r].add(num);
                    columnSets[c].add(num);
                    subgridSets[r / 3][c / 3].add(num);
                }
            }
            return true;
        }


        public static void main(String[] args) {

            int[][] board = {
                    {3,0,6,0,5,8,4,0,0},
                    {5,2,0,0,0,0,0,0,0},
                    {0,8,7,0,0,0,0,3,1},

                    {1,0,2,5,0,0,3,2,0},
                    {9,0,0,8,6,3,0,0,5},
                    {0,5,0,0,9,0,6,0,0},

                    {0,3,0,0,0,8,2,5,0},
                    {0,1,0,0,0,0,0,7,4},
                    {0,0,5,2,0,6,0,0,0}
            };


            verifySudokuBoard(board);

        }



    }


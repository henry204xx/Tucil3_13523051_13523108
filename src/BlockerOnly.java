import java.util.List;

public class BlockerOnly implements Heuristic {

    @Override
    public int calculate(State state) {
        Board b = state.getCurrBoard();
        List<int[]> pos = b.getPrimaryPiecePosition();
        int[] exitPos = b.getExitPos();
        char[][] currBoard = b.generateGrid();

        int[] closest = pos.get(0);
        int minimalDistance = Integer.MAX_VALUE;

        for (int[] piecePos : pos) {
            int dist = Math.abs(piecePos[0] - exitPos[0]) + Math.abs(piecePos[1] - exitPos[1]);
            if (dist < minimalDistance) {
                minimalDistance = dist;
                closest = piecePos;
            }
        }

        String orientation = b.determineDirection(pos); 
        int blockers = 0;

        if ("horizontal".equals(orientation)) {
            int row = closest[0];
            int startCol = closest[1];
            int endCol = exitPos[1];
            int step = (endCol > startCol) ? 1 : -1;

            char currentChar = currBoard[row][startCol];
            for (int col = startCol + step; col != endCol + step; col += step) {
                if (col >= 0 && col < currBoard[0].length) {
                    if (currBoard[row][col] != '.' && currBoard[row][col] != currentChar) {
                        blockers++;
                    }
                }
            }

        } else if ("vertical".equals(orientation)) {
            int col = closest[1];
            int startRow = closest[0];
            int endRow = exitPos[0];
            int step = (endRow > startRow) ? 1 : -1;

            char currentChar = currBoard[startRow][col];
            for (int row = startRow + step; row != endRow + step; row += step) {
                if (row >= 0 && row < currBoard.length) {
                    if (currBoard[row][col] != '.' && currBoard[row][col] != currentChar) {
                        blockers++;
                    }
                }
            }
        }

        return blockers;
    }
}

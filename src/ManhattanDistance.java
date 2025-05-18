import java.util.List;

public class ManhattanDistance implements Heuristic {

    @Override
    public int calculate(State state) {
        Board b = state.getCurrBoard();
        List<int[]> pos = b.getPrimaryPiecePosition();
        int[] exitPos = b.getExitPos();

        int minimalDistance = Integer.MAX_VALUE;
        for (int[] piecePos : pos) {
            int dist = Math.abs(piecePos[0] - exitPos[0]) + Math.abs(piecePos[1] - exitPos[1]);
            minimalDistance = Math.min(minimalDistance, dist);
        }

        return minimalDistance;
    }
}

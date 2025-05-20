public class IDS extends Solver {

    private static final int FOUND = 1;
    private static final int CUTOFF = 0;
    private static final int FAILURE = -1;

    public IDS() {
        super();
    }

    @Override
    protected void solve(State root, int[] counter, int mode) {
        int depthLimit = 0;

        while (true) {
            int result = depthLimitedSearch(root, depthLimit, 0, counter);
            if (result == FOUND) {
                break;
            }
            if (result == FAILURE) {
                System.out.println("All nodes explored. No solution.");
                break;
            }
            depthLimit++;
        }
    }


    private int depthLimitedSearch(State current, int limit, int depth, int[] counter) {
        counter[0]++; // count node

        if (current.isGoalState()) {
            buildPath(current.removePrimaryPieceState());
            return FOUND;
        }

        if (depth == limit) {
            return CUTOFF;
        }

        boolean anyCutoff = false;
        for (State succ : current.getSuccessors()) {
            succ.setPrevState(current);
            int result = depthLimitedSearch(succ, limit, depth + 1, counter);
            if (result == FOUND) {
                return FOUND;
            }
            if (result == CUTOFF) {
                anyCutoff = true;
            }
        }

        return anyCutoff ? CUTOFF : FAILURE;
    }

    // public static void main(String[] args) {
    //     Board board = new Board();
    //     board.readInputFromFile();
    //     board.printBoard();

    //     IDS idsAlgo = new IDS();
    //     Result result = idsAlgo.run(board);

    //     for (Board e : result.solutionStep) {
    //         System.err.println();
    //         e.printBoard();
    //         System.err.println();
    //     }

    //     System.out.println("Waktu eksekusi: " + result.time + " ms");
    //     System.out.println("Banyak node: " + result.nodes);
    // }
}

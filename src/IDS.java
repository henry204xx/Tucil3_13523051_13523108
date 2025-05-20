import java.util.Stack;

public class IDS extends Solver {
    public IDS() {
        super();
    }

    @Override
    protected void solve(State root, int[] counter, int mode) {
        int depthLimit = 0;
        int prevVisitedSize = -1;

        while (true) {
            visited.clear();
            boolean found = depthLimitedSearch(root, depthLimit, counter);
            if (found) {
                break;
            }

            if (visited.size() == prevVisitedSize) {
                break;
            }

            prevVisitedSize = visited.size();
            depthLimit++;
        }
    }

    private boolean depthLimitedSearch(State root, int limit, int[] counter) {
        Stack<State> stack = new Stack<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            State current = stack.pop();

            if (isVisited(current.getCurrBoard())) continue;
            addToVisited(current.getCurrBoard());
            counter[0] += 1;

            if (current.isGoalState()) {
                State finalState = current.removePrimaryPieceState();
                buildPath(finalState);
                return true;
            }

            if (current.getCountSteps() < limit) {
                for (State succ : current.getSuccessors()) {
                    succ.setPrevState(current);
                    stack.push(succ);
                }
            }
        }

        return false;
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

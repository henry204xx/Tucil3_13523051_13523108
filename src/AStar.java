public class AStar extends Solver {
    public AStar() {
        super();
    }

    @Override
    protected void solve(State root, int[] counter, int mode) {
        Heuristic heuristic;
        switch (mode) {
            case 1 -> heuristic = new BlockerOnly();
            case 2 -> heuristic = new ManhattanDistance();
            case 3 -> heuristic = new CombinedHeuristic();
            default -> throw new IllegalArgumentException("Invalid mode: " + mode);
        }

        PrioQueue queue = new PrioQueue(100);
        root.setTotalCost(heuristic.calculate(root) + root.getCountSteps());
        queue.enqueue(root);

        while (!queue.isEmpty()) {
            State current = queue.dequeue();

            if (isVisited(current.getCurrBoard())) continue;
            addToVisited(current.getCurrBoard());
            counter[0] += 1;

            if (current.isGoalState()) {
                State finalState = current.removePrimaryPieceState();
                buildPath(finalState);
                return;
            }

            for (State succ : current.getSuccessors()) {
                succ.setTotalCost(heuristic.calculate(succ) + succ.getCountSteps());
                queue.enqueue(succ);
            }
        }
    }

    // public static void main(String[] args) {
    //     Board board = new Board();
    //     board.readInputFromFile();
    //     board.printBoard();

    //     int mode = 3;

    //     AStar aStarAlgo = new AStar();
    //     Result result = aStarAlgo.run(board, mode);

    //     for (State e : result.solutionStep) {
    //         System.err.println();
    //         e.getCurrBoard().printBoard();
    //         System.err.println();
    //     }

    //     System.out.println("Waktu eksekusi: " + result.time + " ms");
    //     System.out.println("Banyak node: " + result.nodes);
    // }
}

public class UCS extends Solver{
    public UCS() {
        super();
    }

    @Override
    protected void solve(State root, int[] counter, int modeIngnored) {
        
        PrioQueue queue = new PrioQueue(100);
        root.setTotalCost(root.getCountSteps());
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
                succ.setTotalCost(succ.getCountSteps());
                queue.enqueue(succ);
            }
        }
    }

    public static void main(String[] args) {
        Board board = new Board();
        board.readInputFromFile();
        board.printBoard();

        UCS ucsAlgo = new UCS();
        Result result = ucsAlgo.run(board, -1); 
        System.out.println("Length of path: " + result.solutionStep.size());

        for (Board e : result.solutionStep) {
            System.err.println();
            e.printBoard();
            System.err.println();
        }

        System.out.println("Waktu eksekusi: " + result.time + " ms");
        System.out.println("Banyak node: " + result.nodes);
    }
}

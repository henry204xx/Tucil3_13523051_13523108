import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public abstract class Solver {
    protected Set<Board> visited = new HashSet<>();
    protected LinkedList<State> finalPath = new LinkedList<>();

    public LinkedList<State> getFinalPath() {
        return finalPath;
    }

    public void addToVisited(Board b) {
        visited.add(b);
    }

    public boolean isVisited(Board b) {
        return visited.contains(b);
    }

    protected void buildPath(State goal) {
        State current = goal;
        while (current != null) {
            finalPath.addFirst(current);
            current = current.getPrevState();
        }
    }

    public Result run(Board board) {
        return run(board, -1); 
    }
    
    public Result run(Board board, int mode) {
        State root = new State(board);
        int[] counter = new int[1];

        long startTime = System.nanoTime();
        solve(root, counter, mode);
        long endTime = System.nanoTime();

        double durationMs = (endTime - startTime) / 1_000_000.0;
        return new Result(durationMs, counter[0], getFinalPath());
    }

    protected abstract void solve(State root, int[] counter, int mode);
}

import java.util.LinkedList;

public class Result {
    public LinkedList<Board> solutionStep;
    public double time;
    public int nodes;

    public Result(double time, int nodes, LinkedList<Board> solutionStep) {
        this.time = time;
        this.nodes = nodes;
        this.solutionStep = solutionStep;
    }
}

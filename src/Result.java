import java.util.LinkedList;

public class Result {
    public LinkedList<State> solutionStep;
    public double time;
    public int nodes;

    public Result(double time, int nodes, LinkedList<State> solutionStep) {
        this.time = time;
        this.nodes = nodes;
        this.solutionStep = solutionStep;
    }
}

public class CombinedHeuristic implements Heuristic {

    private final ManhattanDistance md = new ManhattanDistance();
    private final BlockerOnly bo = new BlockerOnly();

    @Override
    public int calculate(State state) {
        return md.calculate(state) + bo.calculate(state);
    }
}

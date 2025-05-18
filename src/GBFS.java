import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
public class GBFS {

    private Set<Board> visited = new HashSet<>();  
    private LinkedList<Board> finalPath  = new LinkedList<>();
    
    public GBFS(){
        //empty
    }

    public LinkedList<Board> getFinalPath(){
        return finalPath;
    }

    public void addToVisited(Board b) {
        visited.add(b);
    }

    public boolean isVisited(Board b) {
        return visited.contains(b);
    }

    public int countHeuristic(Board b, int mode) {
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
        int distance = 0;

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
                    distance++;
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
                    distance++;
                }
            }
        }


        switch (mode) {
            case 1: return blockers;              
            case 2: return distance;             
            case 3: return blockers + distance;   
            default: return blockers;            
        }
    }

    public void runGBFS(State root, int mode) {
        PrioQueue queue = new PrioQueue(100);
        root.setTotalCost(countHeuristic(root.getCurrBoard(), mode));
        queue.enqueue(root);

        while (!queue.isEmpty()) {
            State current = queue.dequeue();

            if (visited.contains(current.getCurrBoard())) continue;
            addToVisited(current.getCurrBoard());

            if (isGoalState(current)) {
                buildPath(current); 
                return;
            }

            for (State succ : current.getSuccessors()) {
                succ.setTotalCost(countHeuristic(succ.getCurrBoard(),mode));
                queue.enqueue(succ);
            }
        }
    }


    private void buildPath(State goal) {
        State current = goal;
        while (current != null) {
            finalPath.addFirst(current.getCurrBoard());
            current = current.getPrevState();
        }
    }


    public boolean isGoalState(State state) {
        Board b = state.getCurrBoard();
        int[] exit = b.getExitPos();
        List<int[]> primaryPos = b.getPrimaryPiecePosition();
        for (int[] pos : primaryPos) {
            if (pos[0] == exit[0] && pos[1] == exit[1]) {
                return true;
            }
        }
        return false;
    }

    public static  void main(String[] args) {
        // Heuristik 1 (Penghalang saja)
        // Menghitung jumlah kendaraan yang menghalangi primary piece ke goal.

        // Heuristik 2 (Jarak saja)
        // Menghitung jarak Manhattan primary piece ke goal.

        // Heuristik 3 (Gabungan)
        // Menggabungkan penghalang + jarak sebagai penilaian total cost.


        Board board = new Board();
        board.readInputFromFile();
        board.printBoard();
        State root = new State(board);
        GBFS gbfsAlgo = new GBFS();
        int mode = 3;
        gbfsAlgo.runGBFS(root, mode);



        for(Board e : gbfsAlgo.getFinalPath()){
            System.err.println();
            e.printBoard();
            System.err.println();
        }
    }


}
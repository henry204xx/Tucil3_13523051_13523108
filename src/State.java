import java.util.List;

public class State {
    private Board prevBoard;
    private Board currBoard;
    private String moveDirection;  // "Up", "Down", "Left", "Right"
    private char movedPiece;       // 'A', 'B', etc.
    private int totalCost;
    private int countSteps;     

    public State(Board prevBoard, Board currBoard, String moveDirection, char movedPiece, int countSteps) {
        this.prevBoard = prevBoard;
        this.currBoard = currBoard;
        this.moveDirection = moveDirection;
        this.movedPiece = movedPiece;
        this.totalCost = 0;
        this.countSteps = countSteps;
    }

    public Board getPrevBoard() {
        return prevBoard;
    }

    public void setPrevBoard(Board prevBoard) {
        this.prevBoard = prevBoard;
    }

    public Board getCurrBoard() {
        return currBoard;
    }

    public void setCurrBoard(Board currBoard) {
        this.currBoard = currBoard;
    }

    public String getMoveDirection() {
        return moveDirection;
    }

    public void setMoveDirection(String moveDirection) {
        this.moveDirection = moveDirection;
    }

    public char getMovedPiece() {
        return movedPiece;
    }

    public void setMovedPiece(char movedPiece) {
        this.movedPiece = movedPiece;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(int totalCost) {
        this.totalCost = totalCost;
    }

    public int getCountSteps() {
        return countSteps;
    }

    public void setCountSteps(int countSteps) {
        this.countSteps = countSteps;
    }

    public boolean isGoalState() {
        Board board = this.currBoard;
        if (board == null) return false;

        int[] exitPos = board.getExitPos();
        if (exitPos == null) return false;

        List<int[]> primaryPositions = board.getPrimaryPiecePosition();
        for (int[] position : primaryPositions) {
            if (position[0] == exitPos[0] && position[1] == exitPos[1]) {
                return true; // Goal state reached
            }
        }
        return false;
    }
}

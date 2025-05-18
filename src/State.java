import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    public List<State> getSuccessors() {
        Board currentBoard = this.currBoard;
        if (currentBoard == null) return Collections.emptyList();

        Map<Character, Piece> pieces = currentBoard.getPieces();
        List<State> successors = new ArrayList<>();

        for (Map.Entry<Character, Piece> entry : pieces.entrySet()) {
            char pieceSymbol = entry.getKey();
            Piece piece = entry.getValue();
            String pieceDirection = piece.getDirection();

            String[] directionsToTry;
            if (pieceDirection == null) {
                directionsToTry = new String[]{"up", "down", "left", "right"};
            } else {
                switch (pieceDirection.toLowerCase()) {
                    case "horizontal" -> directionsToTry = new String[]{"left", "right"};
                    case "vertical" -> directionsToTry = new String[]{"up", "down"};
                    default -> directionsToTry = new String[]{"up", "down", "left", "right"};
                }
            }

            for (String direction : directionsToTry) {
                if (currentBoard.canMovePiece(pieceSymbol, direction)) {
                    Board newBoard = currentBoard.movePiece(pieceSymbol, direction);
                    if (newBoard != null) {
                        State newState = new State(
                            currentBoard,
                            newBoard,
                            direction,
                            pieceSymbol,
                            this.countSteps + 1
                        );
                        successors.add(newState);
                    }
                }
            }
        }
        return successors;
    }


}

import java.util.ArrayList;
import java.util.List;

public class Piece {
    private char symbol;
    private String direction;
    private int[] pivot; // [row, col] of the smallest coordinate
    private int length;

    public Piece(char symbol, String direction, int[] pivot, int length) {
        this.symbol = symbol;
        this.direction = direction;
        this.pivot = pivot;
        this.length = length;
    }

    public char getSymbol() {
        return symbol;
    }

    public String getDirection() {
        return direction;
    }

    public int[] getPivot() {
        return pivot;
    }

    public int getLength() {
        return length;
    }

    public List<int[]> getPositions() {
        List<int[]> positions = new ArrayList<>();
        if (direction.equals("horizontal")) {
            for (int i = 0; i < length; i++) {
                positions.add(new int[]{pivot[0], pivot[1] + i});
            }
        } else { // vertical
            for (int i = 0; i < length; i++) {
                positions.add(new int[]{pivot[0] + i, pivot[1]});
            }
        }
        return positions;
    }

    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void setPivot(int[] pivot) {
        this.pivot = pivot;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void moveUp() {
        pivot[0] -= 1;
    }

    public void moveDown() {
        pivot[0] += 1;
    }

    public void moveLeft() {
        pivot[1] -= 1;
    }

    public void moveRight() {
        pivot[1] += 1;
    }
}
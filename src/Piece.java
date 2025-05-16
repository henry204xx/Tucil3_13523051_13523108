
import java.util.List;

public class Piece {
    private char symbol;
    private String direction;
    private List<int[]> positions;

    public Piece(char symbol, String direction, List<int[]> positions) {
        this.symbol = symbol;
        this.direction = direction;
        this.positions = positions;
    }

    public char getSymbol() {
        return symbol;
    }

    public String getDirection() {
        return direction;
    }

    public List<int[]> getPositions() {
        return positions;
    }
    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
    public void setPositions(List<int[]> positions) {
        this.positions = positions;
    }

    public void moveUp() {
        for (int[] position : positions) {
            position[0] -= 1; 
        }
    }

    public void moveDown() {
        for (int[] position : positions) {
            position[0] += 1; 
        }
    }

    public void moveLeft() {
        for (int[] position : positions) {
            position[1] -= 1;
        }
    }

    public void moveRight() {
        for (int[] position : positions) {
            position[1] += 1;
        }
    }

    
}

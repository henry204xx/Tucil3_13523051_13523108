import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Board {
    private int rows;      // A - number of rows
// A - number of rows
    private int columns;
    // N - Number of non-primary pieces
     // B - number of columns
    
    private char[][] grid;
    private final Map<Character, Piece> pieces = new HashMap<>();
    
    public static final char PRIMARY_PIECE = 'P';
    public static final char EXIT = 'K';
    private int[] exitPos = null; // store exit position (row, col)
    
    private static final String[] COLORS = {
        "#FF0000", "#00FF00", "#0000FF", "#FFFF00", "#FF00FF", "#00FFFF", "#800000", "#808000",
        "#008080", "#800080", "#000080", "#FFA500", "#A52A2A", "#8A2BE2","#7FFF00", "#D2691E", 
        "#DC143C", "#FF8C00", "#8B008B", "#556B2F", "#9932CC", "#8B0000","#E9967A", "#9400D3"
    };
    
    public Board() {
        // Empty constructor
    }
    
    public void readInputFromFile() {
        try (Scanner inputScanner = new Scanner(System.in)) {
            System.out.print("Masukkan nama file: ");
            String filePath = "../test/" + inputScanner.nextLine().trim();
            
            try {
                Scanner fileScanner = new Scanner(new File(filePath));
                
                if (!fileScanner.hasNextLine()) {
                    System.out.println("Error: File kosong atau tidak valid.");
                    return;
                }
                
                String[] dimensions = fileScanner.nextLine().split(" ");
                if (dimensions.length != 2) {
                    System.out.println("Error: Baris pertama harus terdiri dari 2 bilangan bulat (A B).");
                    return;
                }
                
                rows = Integer.parseInt(dimensions[0]);
                columns = Integer.parseInt(dimensions[1]);
                
                if (!fileScanner.hasNextLine()) {
                    System.out.println("Error: Baris kedua harus ada untuk menyatakan jumlah kendaraan.");
                    return;
                }
                
                Integer.valueOf(fileScanner.nextLine().trim());
                
                grid = new char[rows + 1][columns + 1];
                
                // Initialize board with spaces
                for (int i = 0; i <= rows; i++) {
                    for (int j = 0; j <= columns; j++) {
                        grid[i][j] = ' ';
                    }
                }
                
                String line = fileScanner.nextLine();
                Map<Character, List<int[]>> pieceCoordinates = new HashMap<>();
                Map<Character, String> letterColors = new HashMap<>();
                
                processLine(line, 0, fileScanner, pieceCoordinates, letterColors);
                
                if (!verifyBoardConstraints()) {
                    System.out.println("Verifikasi papan gagal. Proses dihentikan.");
                    return;
                }
                
                normalizeCoordinates(pieceCoordinates);
                
                if (!validateNormalizedCoordinates(pieceCoordinates)) {
                    System.out.println("Error: Koordinat tidak valid setelah normalisasi.");
                    return;
                }
                
                createPieces(pieceCoordinates, letterColors);
                rebuildBoard();
                
                System.out.println("Input berhasil diproses.");
                if (exitPos != null)
                    System.out.println("Posisi pintu keluar 'K': [" + exitPos[0] + "," + exitPos[1] + "]");
                else
                    System.out.println("Pintu keluar 'K' tidak ditemukan.");
                
            } catch (FileNotFoundException e) {
                System.out.println("File tidak ditemukan.");
            } catch (NumberFormatException e) {
                System.out.println("Error parsing angka. Pastikan format file benar.");
            }
        }
    }
    
    private boolean verifyBoardConstraints() {
        // Check that 'K' exists exactly once and is on the border
        int countK = 0;
        int kRow = -1, kCol = -1;
        
        // Check for P existence
        boolean hasP = false;
        
        for (int i = 0; i <= rows; i++) {
            for (int j = 0; j <= columns; j++) {
                char ch = grid[i][j];
                if (ch == EXIT) {
                    countK++;
                    kRow = i;
                    kCol = j;
                }
                if (ch == PRIMARY_PIECE) {
                    hasP = true;
                }
            }
        }
        
        if (countK != 1) {
            System.out.println("Error: Harus ada tepat satu 'K' pada papan, ditemukan: " + countK);
            return false;
        }
        
        if (!(kRow == 0 || kRow == rows || kCol == 0 || kCol == columns)) {
            System.out.println("Error: 'K' harus berada di baris 0 atau " + rows + ", atau kolom 0 atau " + columns);
            return false;
        }
        
        if (!hasP) {
            System.out.println("Error: Papan harus mengandung setidaknya satu 'P'");
            return false;
        }
        
        return true;
    }
    
    private boolean processLine(String line, int row, Scanner fileScanner, 
                             Map<Character, List<int[]>> pieceCoordinates, 
                             Map<Character, String> letterColors) {
                               
        if (row > rows) {
            System.out.println("Error: Jumlah baris dalam file melebihi batas A + 1 (" + (rows + 1) + ").");
            return false;
        }
        
        if (line.length() > columns + 1) {
            System.out.println("Error: Panjang baris ke-" + row + " melebihi batas B + 1 (" + (columns + 1) + ").");
            return false;
        }
        
        int col = 0;
        
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            
            grid[row][col] = ch;
            
            if (ch != ' ') {
                if (ch != '.') {
                    // Only store if not '.' and not ' '
                    pieceCoordinates.computeIfAbsent(ch, _ -> new ArrayList<>()).add(new int[]{row, col});
                    if (!letterColors.containsKey(ch)) {
                        switch (ch) {
                            case PRIMARY_PIECE -> letterColors.put(ch, "#FF0000"); // Red for 'P'
                            case EXIT -> letterColors.put(ch, "#00FF00"); // Green for 'K'
                            default -> {
                                // Assign from remaining COLORS, skipping reserved red and green
                                int colorIndex = letterColors.size();
                                // Adjust index if P or K were already assigned
                                if (letterColors.containsKey(PRIMARY_PIECE)) colorIndex--;
                                if (letterColors.containsKey(EXIT)) colorIndex--;
                                letterColors.put(ch, COLORS[(colorIndex + 2) % COLORS.length]);
                            }
                        }
                    }
                }
                
                if (ch == EXIT) {
                    exitPos = new int[]{row, col};
                }
            }
            col++;
        }
        
        if (fileScanner.hasNextLine()) {
            return processLine(fileScanner.nextLine(), row + 1, fileScanner, pieceCoordinates, letterColors);
        }
        
        return true;
    }
    
    private void normalizeCoordinates(Map<Character, List<int[]>> pieceCoordinates) {
        if (exitPos == null) {
            System.out.println("Pintu keluar tidak ditemukan. Tidak bisa menormalisasi.");
            return;
        }
        
        int exitRow = exitPos[0];
        int exitCol = exitPos[1];
        
        for (Map.Entry<Character, List<int[]>> entry : pieceCoordinates.entrySet()) {
            char piece = entry.getKey();
            if (piece == EXIT) continue;
            
            List<int[]> coords = entry.getValue();
            
            // Update coordinates depending on exit position
            if (exitRow == 0) {
                // Exit at top row: shift all pieces up by 1
                for (int[] coord : coords) {
                    coord[0] -= 1;
                }
            } else if (exitRow == rows) {
                // Exit at bottom row: no change to pieces
                // But move exit position up by 1
                exitPos[0] -= 1;
            } else if (exitCol == 0) {
                // Exit at leftmost column: shift all pieces left by 1
                for (int[] coord : coords) {
                    coord[1] -= 1;
                }
            } else if (exitCol == columns) {
                // Exit at rightmost column: no change to pieces
                // But move exit position left by 1
                exitPos[1] -= 1;
            }
        }
        
        System.out.println("Koordinat dinormalisasi berdasarkan posisi pintu keluar.");
    }
    
    private boolean validateNormalizedCoordinates(Map<Character, List<int[]>> pieceCoordinates) {
        for (Map.Entry<Character, List<int[]>> entry : pieceCoordinates.entrySet()) {
            char piece = entry.getKey();
            if (piece == EXIT) continue;
            
            List<int[]> coords = entry.getValue();
            for (int[] coord : coords) {
                int r = coord[0];
                int c = coord[1];
                if (r < 0 || r >= rows || c < 0 || c >= columns) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private void createPieces(Map<Character, List<int[]>> pieceCoordinates, Map<Character, String> letterColors) {
        for (Map.Entry<Character, List<int[]>> entry : pieceCoordinates.entrySet()) {
            char symbol = entry.getKey();
            if (symbol == EXIT) continue;
            
            List<int[]> positions = entry.getValue();
            String color = letterColors.get(symbol);
            String direction = determineDirection(positions);
            
            Piece piece = new Piece(symbol, color, direction, positions);
            pieces.put(symbol, piece);
        }
    }
    
    private String determineDirection(List<int[]> coords) {
        if (coords.size() == 1) {
            return "horizontal"; // Default for single position pieces
        }
        
        boolean allSameRow = true;
        int firstRow = coords.get(0)[0];
        for (int[] pos : coords) {
            if (pos[0] != firstRow) {
                allSameRow = false;
                break;
            }
        }
        
        if (allSameRow) {
            return "horizontal";
        }
        
        boolean allSameCol = true;
        int firstCol = coords.get(0)[1];
        for (int[] pos : coords) {
            if (pos[1] != firstCol) {
                allSameCol = false;
                break;
            }
        }
        
        if (allSameCol) {
            return "vertical";
        } else {
            System.out.println("Warning: Piece has non-linear shape.");
            return "unknown";
        }
    }
    
    public void rebuildBoard() {
        // Resize to normalized dimensions after removing the exit
        grid = new char[rows][columns];
        
        // Fill the board with dots (empty spaces)
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                grid[i][j] = '.';
            }
        }
        
        // Place each piece on the board
        for (Piece piece : pieces.values()) {
            for (int[] pos : piece.getPositions()) {
                int r = pos[0];
                int c = pos[1];
                if (r >= 0 && r < rows && c >= 0 && c < columns) {
                    grid[r][c] = piece.getSymbol();
                }
            }
        }
        
        System.out.println("Board berhasil dibangun ulang (tanpa pintu keluar).");
    }
    
    public void printBoard() { //exclude exit position
        System.out.println("Board (" + rows + "x" + columns + "):");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                char symbol = grid[i][j];
                if (symbol == '.') {
                    // Print empty space with default color
                    System.out.print(symbol);
                } else {
                    // Print the piece with its color
                    Piece piece = pieces.get(symbol);
                    if (piece != null) {
                        String hexColor = piece.getColor();
                        printColoredSymbol(symbol, hexColor);
                    } else {
                        // If for some reason the piece is not in the map
                        System.out.print(symbol);
                    }
                }
            }
            System.out.println();
        }
    }
    
   private void printColoredSymbol(char symbol, String hexColor) {
    String ansiColor = convertHexToAnsiTrueColor(hexColor);
    System.out.print(ansiColor + symbol + "\u001B[0m");
    }

    private String convertHexToAnsiTrueColor(String hexColor) {
        if (hexColor.startsWith("#")) {
            hexColor = hexColor.substring(1);
        }

        int r = Integer.parseInt(hexColor.substring(0, 2), 16);
        int g = Integer.parseInt(hexColor.substring(2, 4), 16);
        int b = Integer.parseInt(hexColor.substring(4, 6), 16);

        return String.format("\u001B[38;2;%d;%d;%dm", r, g, b);
    }

    public void printPieces() {
        System.out.println("\nDaftar Piece, Koordinat, dan Arah:");
        for (Piece piece : pieces.values()) {
            System.out.print(piece.getSymbol() + " (" + piece.getColor() + ") - Direction: " + piece.getDirection() + ": ");
            for (int[] coord : piece.getPositions()) {
                System.out.print("[" + coord[0] + "," + coord[1] + "] ");
            }
            System.out.println();
        }
    }
    
    // Movement methods
    public boolean canMovePiece(char symbol, String direction) {
        Piece piece = pieces.get(symbol);
        if (piece == null) return false;
        
        // Check if the direction is valid for the piece orientation
        String pieceDirection = piece.getDirection();
        if (pieceDirection.equals("horizontal") && (direction.equalsIgnoreCase("up") || direction.equalsIgnoreCase("down"))) {
            return false; // Horizontal pieces can only move left or right
        }
        if (pieceDirection.equals("vertical") && (direction.equalsIgnoreCase("left") || direction.equalsIgnoreCase("right"))) {
            return false; // Vertical pieces can only move up or down
        }
        
        List<int[]> positions = piece.getPositions();
        
        // Check board boundaries and other pieces
        switch (direction.toLowerCase()) {
            case "up" -> {
                // Check if any part of the piece would move out of bounds
                for (int[] pos : positions) {
                    int newRow = pos[0] - 1;
                    if (newRow < 0) {
                        return false; // Would move out of bounds
                    }
                    
                    // Check if the new position is occupied by another piece
                    if (grid[newRow][pos[1]] != '.' && grid[newRow][pos[1]] != piece.getSymbol()) {
                        return false; // Position is occupied by another piece
                    }
                }
                return true;
            }
                
            case "down" -> {
                for (int[] pos : positions) {
                    int newRow = pos[0] + 1;
                    if (newRow >= rows) {
                        return false; // Would move out of bounds
                    }
                    
                    if (grid[newRow][pos[1]] != '.' && grid[newRow][pos[1]] != piece.getSymbol()) {
                        return false;
                    }
                }
                return true;
            }
                
            case "left" -> {
                for (int[] pos : positions) {
                    int newCol = pos[1] - 1;
                    if (newCol < 0) {
                        return false; // Would move out of bounds
                    }
                    
                    if (grid[pos[0]][newCol] != '.' && grid[pos[0]][newCol] != piece.getSymbol()) {
                        return false;
                    }
                }
                return true;
            }
                
            case "right" -> {
                for (int[] pos : positions) {
                    int newCol = pos[1] + 1;
                    if (newCol >= columns) {
                        return false; // Would move out of bounds
                    }
                    
                    if (grid[pos[0]][newCol] != '.' && grid[pos[0]][newCol] != piece.getSymbol()) {
                        return false;
                    }
                }
                return true;
            }
                
            default -> {
                return false;
            }
        }
    }
    
    public boolean movePiece(char symbol, String direction) {
        if (!canMovePiece(symbol, direction)) return false;
        
        Piece piece = pieces.get(symbol);
        switch (direction.toLowerCase()) {
            case "up" -> piece.moveUp();
            case "down" -> piece.moveDown();
            case "left" -> piece.moveLeft();
            case "right" -> piece.moveRight();
            default -> {
                return false;
            }
        }
        
        // Update the board after moving the piece
        rebuildBoard();
        return true;
    }
    
    public Map<Character, Piece> getPieces() {
        return pieces;
    }
    
    public char[][] getGrid() {
        return grid;
    }
    
    public int getRows() {
        return rows;
    }
    
    public int getColumns() {
        return columns;
    }
    
    public int[] getExitPos() {
        return exitPos;
    }
    
    public static void main(String[] args) {
        Board board = new Board();
        board.readInputFromFile();
        board.printBoard();
        board.printPieces();
    }
}
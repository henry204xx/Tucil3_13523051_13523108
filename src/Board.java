import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Board {
    private int rows;
    private int columns;
    private int numPieces; // Number of pieces specified in the input file
    private final Map<Character, Piece> pieces = new HashMap<>();

    public static final char PRIMARY_PIECE = 'P';
    public static final char EXIT = 'K';
    private int[] exitPos = null;

    public Board() {
        // Empty constructor
    }

    public boolean readInputFromFile() {
        try (Scanner inputScanner = new Scanner(System.in)) {
            System.out.print("Masukkan nama file: ");
            String filePath = "../test/" + inputScanner.nextLine().trim();

            try {
                try (Scanner fileScanner = new Scanner(new File(filePath))) {
                    if (!fileScanner.hasNextLine()) {
                        System.out.println("Error: File kosong atau tidak valid.");
                        return false;
                    }

                    String[] dimensions = fileScanner.nextLine().split(" ");
                    if (dimensions.length != 2) {
                        System.out.println("Error: Baris pertama harus terdiri dari dimensi papan dalam bilangan bulat (A B).");
                        return false;
                    }

                    rows = Integer.parseInt(dimensions[0]);
                    columns = Integer.parseInt(dimensions[1]);

                    if (!fileScanner.hasNextLine()) {
                        System.out.println("Error: Baris kedua harus ada untuk menyatakan jumlah kendaraan.");
                        return false;
                    }

                    numPieces = Integer.parseInt(fileScanner.nextLine().trim());

                    Map<Character, List<int[]>> pieceCoordinates = new HashMap<>();

                    int currentRow = 0;
                    boolean validLine = true;
                    while (fileScanner.hasNextLine() && validLine) {
                        String line = fileScanner.nextLine();
                        validLine = processLine(line, currentRow, pieceCoordinates);
                        currentRow++;
                    }

                    if (!verifyBoardConstraints(pieceCoordinates)) {
                        System.out.println("Papan tidak sesuai. Periksa file input");
                        return false;
                    }

                    normalizeCoordinates(pieceCoordinates);

                    if (!validateNormalizedCoordinates(pieceCoordinates)) {
                        System.out.println("Error: Papan melebihi dimensi yang ditulis.");
                        return false;
                    }

                    createPieces(pieceCoordinates);
                }

                if ((pieces.size() - 1) != numPieces) {
                    System.out.println("Warning: Jumlah kendaraan dalam file (" + 
                        (pieces.size() - 1) + 
                        ") tidak sesuai dengan yang dinyatakan (" + numPieces + ")");
                }

                System.out.println("Input berhasil diproses.");
                if (exitPos != null)
                    System.out.println("Posisi pintu keluar 'K': [" + exitPos[0] + "," + exitPos[1] + "]");
                else
                    System.out.println("Pintu keluar 'K' tidak ditemukan.");

            } catch (FileNotFoundException e) {
                System.out.println("File tidak ditemukan.");
            } catch (NumberFormatException e) {
                System.out.println("Error parsing angka. Pastikan format file benar.");
            } catch (Exception e) {
                // Catch all other unexpected exceptions
                System.out.println("Terjadi kesalahan tak terduga: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan input dari pengguna: " + e.getMessage());
        }
        return true; 
}


    private boolean processLine(String line, int row, Map<Character, List<int[]>> pieceCoordinates) {
        // if (row > rows) {
        //     System.out.println("Error: Jumlah baris dalam file melebihi batas (" + (rows + 1) + ").");
        //     return false;
        // }

        // if (line.length() > columns + 1) {
        //     System.out.println("Error: Panjang baris ke-" + row + " melebihi batas (" + (columns + 1) + ").");
        //     return false;
        // }

        for (int col = 0; col < line.length(); col++) {
            char ch = line.charAt(col);

            if (ch != ' ' && ch != '.') {
                pieceCoordinates.computeIfAbsent(ch, k -> new ArrayList<>()).add(new int[]{row, col});
                if (ch == EXIT) {
                    exitPos = new int[]{row, col};
                }
            }
        }
        return true;
    }

    private boolean verifyBoardConstraints(Map<Character, List<int[]>> pieceCoordinates) {
        // Verify there is exactly one exit 'K'
        List<int[]> exitCoords = pieceCoordinates.getOrDefault(EXIT, Collections.emptyList());
        if (exitCoords.size() != 1) {
            System.out.println("Error: Harus ada tepat satu 'K' pada papan, ditemukan: " + exitCoords.size());
            return false;
        }
        
        int[] exit = exitCoords.get(0);
        int kRow = exit[0];
        int kCol = exit[1];
        
        // Verify exit is on the border
        if (!(kRow == 0 || kRow == rows || kCol == 0 || kCol == columns)) {
            System.out.println("Error: 'K' harus berada di baris 0 atau " + rows + ", atau kolom 0 atau " + columns);
            return false;
        }

        // Verify there is at least one primary piece 'P'
        if (!pieceCoordinates.containsKey(PRIMARY_PIECE)) {
            System.out.println("Error: Papan harus mengandung setidaknya satu 'P'");
            return false;
        }

        return true;
    }

    private boolean hasPieceAt(Map<Character, List<int[]>> pieceCoordinates, int targetRow, int targetCol) {
        for (Map.Entry<Character, List<int[]>> entry : pieceCoordinates.entrySet()) {
            if (entry.getKey() == 'K') continue; 
            for (int[] coord : entry.getValue()) {
                if (coord[0] == targetRow && coord[1] == targetCol) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getExitDirection() {
        int[] epos = exitPos; 
        int[] ppos = pieces.get('P').getPivot(); 
        String direction = pieces.get('P').getDirection();

        if (direction.equals("horizontal")) {
            if (epos[0] == ppos[0]) { 
                if (epos[1] < ppos[1]) {
                    return "LEFT";
                } else if (epos[1] > ppos[1]) {
                    return "RIGHT";
                }
            }
        } else if (direction.equals("vertical")) {
            if (epos[1] == ppos[1]) { 
                if (epos[0] < ppos[0]) {
                    return "UP";
                } else if (epos[0] > ppos[0]) {
                    return "DOWN";
                }
            }
        }

        return "UNKNOWN"; 
    }


    public String getExitDirection(Map<Character, List<int[]>> pieceCoordinates) {
        List<int[]> kCoords = pieceCoordinates.get('K');
        if (kCoords == null || kCoords.isEmpty()) return "K not found";

        for (int[] coord : kCoords) {
            int r = coord[0];
            int c = coord[1];

            // (0,0)
            if (r == 0 && c == 0) {
                if (hasPieceAt(pieceCoordinates, 0, 1)) return "left";
                else return "up";
            }
            // (0, 1 up to columns-1)
            if (r == 0 && c > 0 && c < columns) {
                return "up";
            }

            // (0, columns)
            if (r == 0 && c == columns) {
                return "right";
               
            }

            if (c == 0 && r > 0 && r < rows) {
                return "left";
            }

            // (rows, 0)
            if (c == 0 && r == rows) {
                return "bottom";
            }

            // (rows, 1 up to columns-1)
            if (c > 0 && c < columns && r == rows) {
                return "bottom";
            }

            // (rows-1, columns)
            if (c == columns && r == rows - 1) {
                return "right";
            }
        }

        return "right";
    }


    private void normalizeCoordinates(Map<Character, List<int[]>> pieceCoordinates) {
        if (exitPos == null) {
            System.out.println("Pintu keluar tidak ditemukan. Tidak bisa menormalisasi.");
            return;
        }

        // int exitRow = exitPos[0];
        // int exitCol = exitPos[1];

        String exitDirection = getExitDirection(pieceCoordinates);
      
        // Adjust all piece coordinates
        for (Map.Entry<Character, List<int[]>> entry : pieceCoordinates.entrySet()) {
            char piece = entry.getKey();
            if (piece == EXIT) continue;

            //coord[0] = row
            //coord[1] = column

            List<int[]> coords = entry.getValue();
            
            if (exitDirection.equals("left")) { //left
                for (int[] coord : coords) {
                    coord[1] -= 1;
                }
            } else if (exitDirection.equals("up")) { //up
                for (int[] coord : coords) {
                    coord[0] -= 1;
                }
            }
        }

        // Adjust exitPos once
        if (exitDirection.equals("bottom")) { // bottom
            exitPos[0] -= 1;
        } else if (exitDirection.equals("right")) { // right
            exitPos[1] -= 1;
        }
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

    private void createPieces(Map<Character, List<int[]>> pieceCoordinates) {
        for (Map.Entry<Character, List<int[]>> entry : pieceCoordinates.entrySet()) {
            char symbol = entry.getKey();
            if (symbol == EXIT) continue;

            List<int[]> positions = entry.getValue();
            String direction = determineDirection(positions);
            
            // Find the length and pivot (smallest coordinate)
            int length = positions.size();
            int[] pivot = findPivot(positions, direction);
            
            Piece piece = new Piece(symbol, direction, pivot, length);
            pieces.put(symbol, piece);
        }
    }

    private int[] findPivot(List<int[]> positions, String direction) {
        if (positions.isEmpty()) {
            return new int[]{0, 0};
        }
        
        int[] pivot = positions.get(0).clone();
        
        for (int[] pos : positions) {
            if (direction.equals("horizontal")) {
                if (pos[1] < pivot[1]) {
                    pivot = pos.clone();
                }
            } else { // vertical
                if (pos[0] < pivot[0]) {
                    pivot = pos.clone();
                }
            }
        }
        
        return pivot;
    }

    public String determineDirection(List<int[]> coords) {
        if (coords.size() == 1) {
            return "horizontal"; // Default for single pieces
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

    // Generate grid only when needed
    public char[][] generateGrid() {
        char[][] grid = new char[rows][columns];

        // Initialize grid with empty spaces
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                grid[i][j] = '.';
            }
        }

        // Place pieces on the grid
        for (Piece piece : pieces.values()) {
            List<int[]> positions = piece.getPositions();
            for (int[] pos : positions) {
                int r = pos[0];
                int c = pos[1];
                if (r >= 0 && r < rows && c >= 0 && c < columns) {
                    grid[r][c] = piece.getSymbol();
                }
            }
        }

        return grid;
    }

    public void printBoard() {
        char[][] grid = generateGrid();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                System.out.print(grid[i][j]);
            }
            System.out.println();
        }
    }

    public void printPieces() {
        System.out.println("\nDaftar Piece, Koordinat, dan Arah:");
        for (Piece piece : pieces.values()) {
            System.out.print(piece.getSymbol() + " - Direction: " + piece.getDirection() + 
                    ", Pivot: [" + piece.getPivot()[0] + "," + piece.getPivot()[1] + 
                    "], Length: " + piece.getLength() + ": ");
            
            List<int[]> positions = piece.getPositions();
            for (int[] coord : positions) {
                System.out.print("[" + coord[0] + "," + coord[1] + "] ");
            }
            System.out.println();
        }
    }

    public boolean canMovePiece(char symbol, String direction) {
        Piece piece = pieces.get(symbol);
        if (piece == null) return false;

        String pieceDirection = piece.getDirection();
        if (pieceDirection.equals("horizontal") && (direction.equalsIgnoreCase("up") || direction.equalsIgnoreCase("down"))) {
            return false;
        }
        if (pieceDirection.equals("vertical") && (direction.equalsIgnoreCase("left") || direction.equalsIgnoreCase("right"))) {
            return false;
        }

        // Generate current grid to check for collisions
        char[][] grid = generateGrid();
        List<int[]> positions = piece.getPositions();

        switch (direction.toLowerCase()) {
            case "up" -> {
                for (int[] pos : positions) {
                    int newRow = pos[0] - 1;
                    if (newRow < 0) {
                        return false;
                    }
                    if (grid[newRow][pos[1]] != '.' && grid[newRow][pos[1]] != piece.getSymbol()) {
                        return false;
                    }
                }
                return true;
            }
            case "down" -> {
                for (int[] pos : positions) {
                    int newRow = pos[0] + 1;
                    if (newRow >= rows) {
                        return false;
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
                        return false;
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
                        return false;
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

    public Board movePiece(char symbol, String direction) {
        if (!this.canMovePiece(symbol, direction)) {
            System.out.println("Tidak bisa memindahkan kendaraan " + symbol + " ke arah " + direction);
            return null;
        }
        
        // Create a new board with the same dimensions and properties
        Board newBoard = new Board();
        newBoard.rows = this.rows;
        newBoard.columns = this.columns;
        newBoard.numPieces = this.numPieces;
        newBoard.exitPos = this.exitPos != null ? new int[]{this.exitPos[0], this.exitPos[1]} : null;
        
        // Copy all pieces except the one to be moved
        for (Map.Entry<Character, Piece> entry : this.pieces.entrySet()) {
            char pieceSymbol = entry.getKey();
            Piece originalPiece = entry.getValue();
            
            if (pieceSymbol != symbol) {
                // Deep copy of the piece
                int[] newPivot = originalPiece.getPivot().clone();
                Piece newPiece = new Piece(
                    pieceSymbol, 
                    originalPiece.getDirection(), 
                    newPivot, 
                    originalPiece.getLength()
                );
                newBoard.pieces.put(pieceSymbol, newPiece);
            } else {
                // Create a new piece with updated position
                int[] newPivot = originalPiece.getPivot().clone();
                
                // Update pivot based on direction
                switch (direction.toLowerCase()) {
                    case "up" -> newPivot[0] -= 1;
                    case "down" -> newPivot[0] += 1;
                    case "left" -> newPivot[1] -= 1;
                    case "right" -> newPivot[1] += 1;
                }
                
                Piece newPiece = new Piece(
                    symbol, 
                    originalPiece.getDirection(), 
                    newPivot, 
                    originalPiece.getLength()
                );
                newBoard.pieces.put(symbol, newPiece);
            }
        }
        
        return newBoard;
    }

    public Map<Character, Piece> getPieces() {
        return pieces;
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

    public int getNumPieces() {
        return numPieces;
    }

    public List<int[]> getPrimaryPiecePosition() {
        Piece primary = pieces.get(PRIMARY_PIECE);
        if (primary != null) {
            return primary.getPositions();
        }
        return Collections.emptyList();
    }

    public Board removePrimaryPiece() {
        Board newBoard = new Board();
        newBoard.rows = this.rows;
        newBoard.columns = this.columns;
        newBoard.numPieces = this.numPieces - 1;
        newBoard.exitPos = this.exitPos != null ? new int[]{this.exitPos[0], this.exitPos[1]} : null;

        for (Map.Entry<Character, Piece> entry : this.pieces.entrySet()) {
            char pieceSymbol = entry.getKey();
            if (pieceSymbol != PRIMARY_PIECE) {
                Piece originalPiece = entry.getValue();
                int[] newPivot = originalPiece.getPivot().clone();
                Piece newPiece = new Piece(
                    pieceSymbol, 
                    originalPiece.getDirection(), 
                    newPivot, 
                    originalPiece.getLength()
                );
                newBoard.pieces.put(pieceSymbol, newPiece);
            }
        }

        return newBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        return Arrays.deepEquals(this.generateGrid(), board.generateGrid());
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(generateGrid());
    }

    public void readInputFromFileGUI(String filePath) throws FileNotFoundException {
        try (Scanner fileScanner = new Scanner(new File(filePath))) {
            if (!fileScanner.hasNextLine()) {
                throw new IllegalArgumentException("Error: File kosong atau tidak valid.");
            }

            String[] dimensions = fileScanner.nextLine().split(" ");
            if (dimensions.length != 2) {
                throw new IllegalArgumentException("Error: Baris pertama harus terdiri dari dimensi papan (A B).");
            }

            try {
                rows = Integer.parseInt(dimensions[0]);
                columns = Integer.parseInt(dimensions[1]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Error: Dimensi papan harus berupa bilangan bulat.");
            }

            if (!fileScanner.hasNextLine()) {
                throw new IllegalArgumentException("Error: Jumlah kendaraan tidak ada atau tidak sesuai format.");
            }

            try {
                numPieces = Integer.parseInt(fileScanner.nextLine().trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Error: Jumlah kendaraan tidak ada atau tidak sesuai format.");
            }

            Map<Character, List<int[]>> pieceCoordinates = new HashMap<>();
            int currentRow = 0;

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                try {
                    boolean validLine = processLine(line, currentRow, pieceCoordinates);
                    if (!validLine) {
                        throw new IllegalArgumentException("Error: Format baris ke-" + currentRow + " tidak valid.");
                    }
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Error saat memproses baris ke-" + currentRow + ": " + e.getMessage());
                }
                currentRow++;
            }

            try {
                if (!verifyBoardConstraints(pieceCoordinates)) {
                    throw new IllegalArgumentException("Papan tidak sesuai format. Periksa file input.");
                }
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Papan tidak sesuai format. Periksa file input.");
            }

            try {
                String exitDirection = getExitDirection(pieceCoordinates);
                String primaryDir = determineDirection(pieceCoordinates.get(PRIMARY_PIECE));
                boolean match = (primaryDir.equals("vertical") && (exitDirection.equals("up") || exitDirection.equals("bottom")))
                             || (primaryDir.equals("horizontal") && (exitDirection.equals("left") || exitDirection.equals("right")));
                if (!match) {
                    throw new IllegalArgumentException("Arah kendaraan utama 'P' (" +
                        primaryDir +
                        ") tidak sesuai dengan arah pintu keluar (" + exitDirection + ").");
                }

                // if horizontal, all 'P' must be on the same row as exit; if vertical, same column
                List<int[]> primaryCoords = pieceCoordinates.get(PRIMARY_PIECE);
                int[] exit = pieceCoordinates.get(EXIT).get(0);
                if (primaryDir.equals("horizontal")) {
                    for (int[] coord : primaryCoords) {
                        if (coord[0] != exit[0]) {
                            throw new IllegalArgumentException("Kendaraan utama 'P' harus berada pada baris yang sama dengan pintu keluar.");
                        }
                    }
                } else if (primaryDir.equals("vertical")) {
                    for (int[] coord : primaryCoords) {
                        if (coord[1] != exit[1]) {
                            throw new IllegalArgumentException("Kendaraan utama 'P' harus berada pada kolom yang sama dengan pintu keluar.");
                        }
                    }
                }

            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(e.getMessage());
            }

            try {
                normalizeCoordinates(pieceCoordinates);
            } catch (Exception e) {
                throw new IllegalArgumentException("Error: Posisi piece di luar board.");
            }

            try {
                if (!validateNormalizedCoordinates(pieceCoordinates)) {
                    throw new IllegalArgumentException("Error: Posisi piece di luar board.");
                }
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Error: Posisi piece di luar board.");
            }

            try {
                createPieces(pieceCoordinates);
            } catch (Exception e) {
                throw new IllegalArgumentException("Error saat membuat pieces");
            }

            if ((pieces.size() - 1) != numPieces) {
                throw new IllegalArgumentException("Jumlah kendaraan dalam file (" +
                    (pieces.size() - 1) + ") tidak sesuai dengan yang dinyatakan (" + numPieces + ")");
            }

            if (exitPos == null) {
                throw new IllegalArgumentException("Pintu keluar 'K' tidak ditemukan.");
            }

        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("File tidak ditemukan: " + filePath);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }


    // public static void main(String[] args) {
    //     Board board = new Board();
    //     board.readInputFromFile();
    //     board.printBoard();
    //     board.printPieces();
    //     Board movedBoard = board.movePiece('I', "left");
    //     Board board1 = board.movePiece('I', "left");

    //     if (movedBoard != null) {
    //         movedBoard.printBoard();
    //     } else {
    //         System.out.println("Tidak bisa memindahkan kendaraan.");
    //     }

    //     //test primary piece position
    //     List<int[]> primaryPositions = board.getPrimaryPiecePosition();
    //     System.out.println("Posisi kendaraan utama 'P': ");
    //     for (int[] pos : primaryPositions) {
    //         System.out.print("[" + pos[0] + "," + pos[1] + "] ");
    //     }
    // }
}
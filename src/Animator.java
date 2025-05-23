import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import javax.swing.*;
import javax.swing.border.Border;

public class Animator extends JFrame {
    private int rows = 6;
    private int cols = 6;
    private Board curBoard;
    private int[] exitPos = null;

    private JSlider speedSlider;
    private JPanel boardPanel;
    private JLabel[][] cellLabels;
    private JButton startButton, replayButton, loadFileButton;
    private JButton saveButton;
    private JComboBox<String> algorithmSelector;
    private JLabel resultLabel;
    private JLabel titleLabel;
    private JPanel headerPanel;
    private JPanel footerPanel;
    private JComboBox<String> heuristicSelector;
    private JLabel nodesLabel;
    private JLabel timeLabel;
    
    private LinkedList<State> solutionSteps;
    private int currentStep = 0;
    private Timer animationTimer;
    private int countNode = 0;
    private double execTime = 0;
    
    private static final Color[] VEHICLE_COLORS = {
        new Color(0, 0, 255),      
        new Color(0, 128, 0),       
        new Color(255, 255, 0),     
        new Color(0, 255, 255),    
        new Color(128, 0, 128),     
        new Color(255, 192, 203),   
        new Color(165, 42, 42),     
        new Color(0, 255, 0),       
        new Color(70, 130, 180),   
        new Color(255, 215, 0),     
        new Color(0, 128, 128),    
        new Color(75, 0, 130),     
        new Color(240, 230, 140),   
        new Color(32, 178, 170),    
        new Color(218, 112, 214),   
        new Color(50, 205, 50),     
        new Color(147, 112, 219),   
        new Color(210, 105, 30),    
        new Color(0, 206, 209),     
        new Color(60, 179, 113),   
        new Color(70, 130, 180),  
        new Color(100, 149, 237),   
        new Color(0, 191, 255),    
        new Color(106, 90, 205),   
        new Color(154, 205, 50),    
        new Color(95, 158, 160)     

    };

    public Animator() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Rush Hour Solver");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 240, 240));
        
        createHeaderPanel();
        createBoardPanel(this.rows, this.cols); 
        createControlPanel();
        createFooterPanel();
        
        pack();
        setLocationRelativeTo(null); 
        setVisible(true);
    }

    private void createHeaderPanel() {
        headerPanel = new JPanel();
        headerPanel.setBackground(new Color(100, 149, 237));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        titleLabel = new JLabel("RUSH HOUR SOLVER", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);
    }

    private void createBoardPanel(int rows, int cols) {
        boardPanel = new JPanel(new GridLayout(rows, cols, 2, 2));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        boardPanel.setBackground(Color.DARK_GRAY);

        cellLabels = new JLabel[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                cellLabels[i][j] = new JLabel("", SwingConstants.CENTER);
                cellLabels[i][j].setOpaque(true);
                cellLabels[i][j].setBackground(Color.LIGHT_GRAY);
                cellLabels[i][j].setPreferredSize(new Dimension(60, 60));
                cellLabels[i][j].setFont(new Font("Arial", Font.BOLD, 16));

                Border finalBorder;

                if (exitPos != null && i == exitPos[0] && j == exitPos[1]) {
                    String direction = (curBoard != null) ? curBoard.getExitDirection() : "UNKNOWN";
                    Border normalBorder = BorderFactory.createLineBorder(Color.BLACK);
                    Border exitMarker;

                    switch (direction) {
                        case "UP":
                            exitMarker = BorderFactory.createMatteBorder(10, 0, 0, 0, Color.RED);
                            break;
                        case "DOWN":
                            exitMarker = BorderFactory.createMatteBorder(0, 0, 10, 0, Color.RED);
                            break;
                        case "LEFT":
                            exitMarker = BorderFactory.createMatteBorder(0, 10, 0, 0, Color.RED);
                            break;
                        case "RIGHT":
                            exitMarker = BorderFactory.createMatteBorder(0, 0, 0, 10, Color.RED);
                            break;
                        default:
                            exitMarker = BorderFactory.createEmptyBorder();
                    }

                    finalBorder = BorderFactory.createCompoundBorder(exitMarker, normalBorder);
                } else {
                    finalBorder = BorderFactory.createLineBorder(Color.BLACK);
                }

                cellLabels[i][j].setBorder(finalBorder);
                boardPanel.add(cellLabels[i][j]);
            }
        }

        add(boardPanel, BorderLayout.CENTER);
    }


    private void createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        controlPanel.setBackground(Color.WHITE);
        
        loadFileButton = createStyledButton("Load Puzzle", new Color(70, 130, 180));
        loadFileButton.addActionListener(k -> loadPuzzleFile());
        
        algorithmSelector = new JComboBox<>(new String[]{"GBFS", "UCS", "A*", "IDS"});
        algorithmSelector.setMaximumSize(new Dimension(200, 30));
        algorithmSelector.setAlignmentX(Component.CENTER_ALIGNMENT);
        algorithmSelector.addActionListener(e ->{
            replayButton.setEnabled(false);
            saveButton.setEnabled(false);}
        );

        heuristicSelector = new JComboBox<>(new String[]{
            "Heuristic 1 - Blocking Vehicles",
            "Heuristic 2 - Manhattan Distance",
            "Heuristic 3 - Combined"
        });
        heuristicSelector.setMaximumSize(new Dimension(200, 30));
        heuristicSelector.setAlignmentX(Component.CENTER_ALIGNMENT);
        heuristicSelector.addActionListener(e ->{
            replayButton.setEnabled(false);
            saveButton.setEnabled(false);}
        );

        this.speedSlider = new JSlider(JSlider.HORIZONTAL, 50, 1000, 500); // min=50, max=1000, initial=500
        speedSlider.setMajorTickSpacing(250);
        speedSlider.setMinorTickSpacing(50);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel replaySpeed = new JLabel("Animation Speed");
        replaySpeed.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        startButton = createStyledButton("Solve", new Color(34, 139, 34));
        startButton.setEnabled(false);
        startButton.addActionListener(k -> solvePuzzle());

        saveButton = createStyledButton("Save Solution", new Color(255, 140, 0));
        saveButton.setEnabled(false); 
        saveButton.addActionListener(e -> saveSolutionToFile());
        
        replayButton = createStyledButton("Replay Solution", new Color(138, 43, 226));
        replayButton.setEnabled(false);
        replayButton.addActionListener(k -> replaySolution());
        
        controlPanel.add(loadFileButton);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        controlPanel.add(algorithmSelector);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        controlPanel.add(heuristicSelector);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        controlPanel.add(startButton);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        controlPanel.add(replayButton);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        controlPanel.add(replaySpeed);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        controlPanel.add(speedSlider);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        controlPanel.add(saveButton);

        add(controlPanel, BorderLayout.EAST);
    }

    private void createFooterPanel() {
        footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        footerPanel.setBackground(new Color(220, 220, 220));
        
        resultLabel = new JLabel("Ready to solve!");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        nodesLabel = new JLabel("Nodes explored: 0");
        nodesLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        timeLabel = new JLabel("Time: 0 ms");
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        footerPanel.add(resultLabel);
        footerPanel.add(nodesLabel);
        footerPanel.add(timeLabel);
        
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 35));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }


    private void loadPuzzleFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();
            startButton.setEnabled(false);
            replayButton.setEnabled(false);
            try {
                this.curBoard = new Board();
                this.curBoard.readInputFromFileGUI(filePath);

                this.rows = curBoard.getRows();
                this.cols = curBoard.getColumns();
                this.exitPos = curBoard.getExitPos();

                resetBoard(this.rows, this.cols);
                updateBoard(this.curBoard);
                System.out.println("File path: " + filePath); 
                resultLabel.setText("Puzzle loaded!");
                replayButton.setEnabled(false);
                startButton.setEnabled(true);
                saveButton.setEnabled(false);
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(this,
                        "File tidak ditemukan:\n" + e.getMessage(),
                        "File Error",
                        JOptionPane.ERROR_MESSAGE);
                saveButton.setEnabled(false);
                this.exitPos = null;
                remove(this.boardPanel);   
                createBoardPanel(6, 6);
                revalidate();
                repaint();
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this,
                        "Gagal memuat file puzzle:\n" + e.getMessage(),
                        "Format Error",
                        JOptionPane.ERROR_MESSAGE);
                saveButton.setEnabled(false);
                this.exitPos = null;
                remove(this.boardPanel);            
                createBoardPanel(6, 6);
                revalidate();
                repaint();
            }
        }
    }

    private void solvePuzzle() {
        String algorithm = (String) algorithmSelector.getSelectedItem();
        resultLabel.setText("Solving using " + algorithm + "...");
        
        String heuristic = (String) heuristicSelector.getSelectedItem();
        int mode = 1;
        switch (heuristic) {
            case "Heuristic 1 - Blocking Vehicles" -> mode = 1;
            case "Heuristic 2 - Manhattan Distance" -> mode = 2;
            case "Heuristic 3 - Combined" -> mode = 3;
            default -> {
            }
        }
        
        switch (algorithm) {
            case "GBFS":
                GBFS g = new GBFS();
                Result res = g.run(this.curBoard, mode);
                this.solutionSteps = res.solutionStep;
                this.countNode = res.nodes;
                this.execTime = res.time;
                // this.curBoard = solutionSteps.getLast();
                replaySolution();
                break;
            case "A*":
                {
                    AStar aStarAlgo = new AStar();
                    Result result = aStarAlgo.run(this.curBoard, mode);
                    this.solutionSteps = result.solutionStep;
                    this.countNode = result.nodes;
                    this.execTime = result.time;
                    replaySolution();
                    break;
                }
            case "UCS":
                {
                    UCS ucsAlgo = new UCS();
                    Result result = ucsAlgo.run(this.curBoard, -1);
                    this.solutionSteps = result.solutionStep;
                    this.countNode = result.nodes;
                    this.execTime = result.time;
                    replaySolution();
                    break;
                }
            case "IDS":
                { 
                    IDS idsAlgo = new IDS();
                    Result result = idsAlgo.run(this.curBoard);
                    this.solutionSteps = result.solutionStep;
                    this.countNode = result.nodes;
                    this.execTime = result.time;
                    replaySolution();
                    break;
                }
            default:
                break;
        }
        
                
        SwingUtilities.invokeLater(() -> {
            int numberofSteps = solutionSteps.size();
            if (numberofSteps > 1) {
                numberofSteps -= 2; // remove initial and final state
            }
            if(this.solutionSteps.isEmpty()){
                resultLabel.setText("No Solution found!");
            }
            else{
                resultLabel.setText("Solution found in "+numberofSteps+" steps!");
            }
            nodesLabel.setText("Nodes explored: " + this.countNode);
            timeLabel.setText("Time: "+this.execTime+" ms");
            replayButton.setEnabled(true);
            saveButton.setEnabled(true);
            
        });

    }

    private void replaySolution() {
        if (solutionSteps == null || solutionSteps.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No solution to replay", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        currentStep = 0;
        int delay = this.speedSlider.getValue();
        //Ubah delay kalo kelambatan
        animationTimer = new Timer(delay, k -> {
            if (currentStep < solutionSteps.size()) {
                updateBoard(solutionSteps.get(currentStep).getCurrBoard());
                currentStep++;
            } else {
                animationTimer.stop();
            }
        });
        animationTimer.start();
    }


    private void updateBoard(Board boardInState) {
        char[][] grid = boardInState.generateGrid();
        int[] exitPos = boardInState.getExitPos();

        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                char cellValue = grid[i][j];
                if (cellValue != '.') {
                    if (cellValue == 'P') {
                        cellLabels[i][j].setBackground(Color.RED); 
                    } else {
                        int vehicleId = cellValue - 'A';
                        cellLabels[i][j].setBackground(VEHICLE_COLORS[vehicleId % VEHICLE_COLORS.length]);
                    }
                    cellLabels[i][j].setText(String.valueOf(cellValue));
                } else {
                    cellLabels[i][j].setBackground(Color.LIGHT_GRAY);
                    if (i == exitPos[0] && j == exitPos[1]) {
                        cellLabels[i][j].setText("EXIT"); 
                    }
                    else {
                        cellLabels[i][j].setText("");
                    }
                }
            }
        }

    }

    private void resetBoard(int newRows, int newCols) {
        if (boardPanel != null) {
            remove(boardPanel);
        }        
        createBoardPanel(newRows, newCols);

        revalidate();
        repaint();
    }

    private String getSolutionInString(){
        String res = "";

        for(int k = 0; k < this.solutionSteps.size(); k++){
            State currState = this.solutionSteps.get(k);
            Board b = currState.getCurrBoard();
            if(k==0){
                res+= "Kondisi awal papan \n";
            }
            else{
                res= res + "Gerakan " + k + ": "+ currState.getMovedPiece() 
                + "-" + currState.getMoveDirection() + "\n";
            }
            char[][] grid = b.generateGrid();
            for (int i = 0; i < this.rows; i++) {
                for (int j = 0; j < this.cols; j++) {
                    res+=grid[i][j];
                }
                res+="\n";
            }

            res+="\n";
        }
       
        return res;
    }

    private void saveSolutionToFile() {
        if (solutionSteps.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No solution available to save.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String fileName = JOptionPane.showInputDialog(this, "Enter the filename (without extension):", "Save Solution", JOptionPane.PLAIN_MESSAGE);
        if (fileName == null || fileName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Filename cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!fileName.toLowerCase().endsWith(".txt")) {
            fileName += ".txt";
        }

        JFileChooser folderChooser = new JFileChooser();
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = folderChooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = folderChooser.getSelectedFile();
            File outputFile = new File(selectedFolder, fileName);

            try (PrintWriter writer = new PrintWriter(outputFile)) {
                writer.print(getSolutionInString());
                JOptionPane.showMessageDialog(this, "Solution saved to " + outputFile.getAbsolutePath(), "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Failed to save solution: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new Animator();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
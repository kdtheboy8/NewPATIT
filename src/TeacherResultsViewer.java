import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * TeacherResultsViewer - A class for teachers to view all student quiz results
 * @author kudamlambo
 */
public class TeacherResultsViewer extends JFrame {
    
    private JTextField searchField;
    private JComboBox<String> filterComboBox;
    private JButton btnRefresh;
    private JButton btnBack;
    private JPanel mainPanel;
    private JTextArea resultsArea;
    private JScrollPane scrollPane;
    
    // Data storage
    private ArrayList<String[]> allResults;
    
    public TeacherResultsViewer() {
        initializeUI();
        loadResults();
        setVisible(true);
    }
    
    /**
     * Initialize the user interface
     */
    private void initializeUI() {
        setTitle("Teacher Results Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(255, 153, 51));
        
        // Create header panel
        createHeaderPanel();
        
        // Create results display panel
        createResultsPanel();
        
        // Create control panel
        createControlPanel();
        
        add(mainPanel);
    }
    
    /**
     * Create the header panel
     */
    private void createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 153, 51));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel titleLabel = new JLabel("Student Quiz Results - Teacher View");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        btnBack = new JButton("Back to Menu");
        btnBack.setPreferredSize(new Dimension(120, 35));
        btnBack.addActionListener(e -> goBack());
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(btnBack, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
    }
    
    /**
     * Create the results display panel
     */
    private void createResultsPanel() {
        resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultsArea.setBackground(Color.WHITE);
        
        scrollPane = new JScrollPane(resultsArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Create the control panel
     */
    private void createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        controlPanel.setBackground(new Color(240, 240, 240));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Search field
        JLabel searchLabel = new JLabel("Search Email:");
        searchField = new JTextField(20);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterResults();
            }
        });
        
        // Filter combo box
        JLabel filterLabel = new JLabel("Filter by Topic:");
        filterComboBox = new JComboBox<>();
        filterComboBox.addItem("All Topics");
        filterComboBox.addItem("G8-Industrial Revolution");
        filterComboBox.addItem("G8-Mineral Revolution");
        filterComboBox.addItem("G8-Scramble for Africa");
        filterComboBox.addItem("G8-World War I");
        filterComboBox.addItem("G9-Cold War");
        filterComboBox.addItem("G9-World War II");
        filterComboBox.addItem("G9-Turning Points");
        filterComboBox.addItem("G10-Colonial Expansion");
        filterComboBox.addItem("G10-French Revolution");
        filterComboBox.addItem("G10-15th and 16th Centuries");
        filterComboBox.addItem("G10-Transformations");
        filterComboBox.addItem("G11-Apartheid");
        filterComboBox.addItem("G11-Capitalism");
        filterComboBox.addItem("G11-Communism");
        filterComboBox.addItem("G11-Ideas of Race");
        filterComboBox.addItem("G12-Civil Resistance");
        filterComboBox.addItem("G12-Civil Society Protests");
        
        filterComboBox.addActionListener(e -> filterResults());
        
        // Buttons
        btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadResults());
        
        controlPanel.add(searchLabel);
        controlPanel.add(searchField);
        controlPanel.add(filterLabel);
        controlPanel.add(filterComboBox);
        controlPanel.add(btnRefresh);
        
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Load results from the QuizResults.txt file
     */
    private void loadResults() {
        allResults = new ArrayList<>();
        
        try {
            File file = new File("TextFiles/QuizResults.txt");
            if (!file.exists()) {
                resultsArea.setText("No results file found.");
                return;
            }
            
            Scanner scanner = new Scanner(file);
            
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split("#");
                    if (parts.length >= 2) {
                        String email = parts[0];
                        
                        // Process each result for this email
                        for (int i = 1; i < parts.length; i++) {
                            String result = parts[i];
                            String[] resultParts = result.split("-");
                            
                            if (resultParts.length >= 3) {
                                // Format: G8-Industrial Revolution-8/10
                                String grade = resultParts[0];
                                String topic = resultParts[1];
                                String score = resultParts[2];
                                
                                String quizTopic = grade + "-" + topic;
                                
                                // Calculate percentage
                                String[] scoreParts = score.split("/");
                                if (scoreParts.length == 2) {
                                    try {
                                        int correct = Integer.parseInt(scoreParts[0]);
                                        int total = Integer.parseInt(scoreParts[1]);
                                        double percentage = (double) correct / total * 100;
                                        
                                        String[] rowData = {
                                            email,
                                            quizTopic,
                                            score,
                                            String.format("%.1f%%", percentage)
                                        };
                                        
                                        allResults.add(rowData);
                                    } catch (NumberFormatException e) {
                                        // Skip invalid score format
                                    }
                                }
                            }
                        }
                    }
                }
            }
            scanner.close();
            
            displayResults();
            JOptionPane.showMessageDialog(this, "Loaded " + allResults.size() + " results.", "Data Loaded", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading results: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Display results in the text area
     */
    private void displayResults() {
        resultsArea.setText("");
        resultsArea.append(String.format("%-30s %-25s %-15s %-15s\n", "Email", "Quiz Topic", "Score", "Percentage"));
        resultsArea.append("=".repeat(85) + "\n");
        
        for (String[] result : allResults) {
            resultsArea.append(String.format("%-30s %-25s %-15s %-15s\n", 
                result[0], result[1], result[2], result[3]));
        }
    }
    
    /**
     * Filter results based on search text and selected topic
     */
    private void filterResults() {
        String searchText = searchField.getText().toLowerCase();
        String selectedTopic = (String) filterComboBox.getSelectedItem();
        
        resultsArea.setText("");
        resultsArea.append(String.format("%-30s %-25s %-15s %-15s\n", "Email", "Quiz Topic", "Score", "Percentage"));
        resultsArea.append("=".repeat(85) + "\n");
        
        int count = 0;
        for (String[] result : allResults) {
            boolean matchesSearch = result[0].toLowerCase().contains(searchText);
            boolean matchesTopic = selectedTopic.equals("All Topics") || result[1].equals(selectedTopic);
            
            if (matchesSearch && matchesTopic) {
                resultsArea.append(String.format("%-30s %-25s %-15s %-15s\n", 
                    result[0], result[1], result[2], result[3]));
                count++;
            }
        }
        
        if (count == 0) {
            resultsArea.append("No results match the current filter.\n");
        }
    }
    
    /**
     * Go back to main menu
     */
    private void goBack() {
        this.setVisible(false);
        new HistoryED().setVisible(true);
    }
    
    /**
     * Main method to run the teacher results viewer
     */
    public static void main(String[] args) {
        new TeacherResultsViewer();
    }
} 
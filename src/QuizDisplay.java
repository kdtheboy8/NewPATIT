import java.io.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author kudamlambo
 */
public class QuizDisplay extends javax.swing.JFrame 
{
    private int currentQuestion = 1;
    private int totalQuestions = 0;
    private String currentGrade;
    private String selectedQuiz;
    private QuizInterfaceClass qic;
    private ArrayList<String> userAnswers = new ArrayList<>();
    private ArrayList<String> correctAnswers = new ArrayList<>();
    private int score = 0;
    private String userEmail = "";

    /**
     * Creates new form Menu
     * @param grade
     * @param selectedQuiz
     */
    
    public QuizDisplay(String grade, String selectedQuiz) {
        this.currentGrade = grade;
        this.selectedQuiz = selectedQuiz;
        
        initComponents();
        lblGrade.setText("Grade: " + currentGrade + " - Quiz: " + selectedQuiz);
        
        // Prompt for user email
        promptForEmail();
        
        // Initialize quiz interface with QuizQuestions directory
        qic = new QuizInterfaceClass(grade, "QuizQuestions/" + selectedQuiz);
        qic.addToArray();
        totalQuestions = qic.numberOfQuestions();
        
        // Check if quiz loaded successfully
        if (totalQuestions == 0) {
            JOptionPane.showMessageDialog(this, 
                "Error: Could not load quiz '" + selectedQuiz + "'. Please check if the quiz file exists.", 
                "Quiz Error", 
                JOptionPane.ERROR_MESSAGE);
            this.dispose();
            return;
        }
        
        // Initialize user answers array
        for (int i = 0; i < totalQuestions; i++) {
            userAnswers.add("");
        }
        
        // Load first question
        loadQuestion(currentQuestion);
        updateNavigationButtons();
    }

    // Convenience constructor for running directly without parameters
    public QuizDisplay() {
        this("8", "IndustrialRevolution");
    }

    /**
     * Loads a specific question and displays it
     */
    private void loadQuestion(int questionNumber) {
        if (questionNumber >= 1 && questionNumber <= totalQuestions) {
            currentQuestion = questionNumber;
            
            // Get question and answers
            String questionData = qic.getQuestion(questionNumber);
            String[] parts = questionData.split("#");
            
            if (parts.length >= 6) {
                // Display question with progress
                lblQuestion.setText("Question " + questionNumber + " of " + totalQuestions + ": " + parts[0]);
                
                // Display answer options
                LBLOption1.setText("A - " + parts[1]);
                LBLOption2.setText("B - " + parts[2]);
                LBLOption3.setText("C - " + parts[3]);
                LBLOption4.setText("D - " + parts[4]);
                
                // Store correct answer
                if (correctAnswers.size() < questionNumber) {
                    correctAnswers.add(parts[5]); // Correct answer is at index 5
                }
                
                // Set up combo box with answer options
                cbAnswer.removeAllItems();
                cbAnswer.addItem("A - " + parts[1]);
                cbAnswer.addItem("B - " + parts[2]);
                cbAnswer.addItem("C - " + parts[3]);
                cbAnswer.addItem("D - " + parts[4]);
                
                // Set previously selected answer if exists
                if (!userAnswers.get(questionNumber - 1).isEmpty()) {
                    cbAnswer.setSelectedItem(userAnswers.get(questionNumber - 1));
                } else {
                    cbAnswer.setSelectedIndex(0);
                }
            } else {
                // Handle malformed question data
                lblQuestion.setText("Error: Invalid question format");
                LBLOption1.setText("A - Error");
                LBLOption2.setText("B - Error");
                LBLOption3.setText("C - Error");
                LBLOption4.setText("D - Error");
            }
        }
    }
    
    /**
     * Updates navigation buttons based on current question
     */
    private void updateNavigationButtons() {
        btnBack.setEnabled(currentQuestion > 1);
        if (currentQuestion == totalQuestions) {
            btnNext.setText("Finish Quiz");
        } else {
            btnNext.setText("Next");
        }
        
        // Update button colors to show answered questions
        updateAnswerStatus();
    }
    
    /**
     * Updates the visual status of answered questions
     */
    private void updateAnswerStatus() {
        // Count answered questions
        int answeredCount = 0;
        for (String answer : userAnswers) {
            if (!answer.isEmpty()) {
                answeredCount++;
            }
        }
        
        // Update the grade label to show progress
        lblGrade.setText("Grade: " + currentGrade + " - Progress: " + answeredCount + "/" + totalQuestions + " answered");
    }
    
    /**
     * Saves the current answer
     */
    private void saveCurrentAnswer() {
        String selectedAnswer = (String) cbAnswer.getSelectedItem();
        if (selectedAnswer != null) {
            userAnswers.set(currentQuestion - 1, selectedAnswer);
        }
    }
    
    /**
     * Prompt user for their email address
     */
    private void promptForEmail() {
        userEmail = JOptionPane.showInputDialog(this, 
            "Please enter your email address:", 
            "Student Email", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (userEmail == null || userEmail.trim().isEmpty()) {
            userEmail = "anonymous@example.com";
        }
    }
    
    /**
     * Write quiz result to QuizResults.txt file
     */
    private void writeResultToFile() {
        try {
            File file = new File("TextFiles/QuizResults.txt");
            ArrayList<String> lines = new ArrayList<>();
            
            // Read existing content
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
                reader.close();
            }
            
            // Find if user already has results
            boolean userFound = false;
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).startsWith(userEmail + "#")) {
                    // User exists, append new result
                    String existingLine = lines.get(i);
                    String newResult = selectedQuiz + "-" + score + "/" + totalQuestions;
                    lines.set(i, existingLine + "#" + newResult);
                    userFound = true;
                    break;
                }
            }
            
            // If user not found, create new line
            if (!userFound) {
                String newLine = userEmail + "#" + selectedQuiz + "-" + score + "/" + totalQuestions;
                lines.add(newLine);
            }
            
            // Write back to file
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            writer.close();
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving results: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Calculates and displays the final score
     */
    private void calculateScore() {
        score = 0;
        for (int i = 0; i < totalQuestions; i++) {
            if (i < correctAnswers.size() && i < userAnswers.size()) {
                String userAnswer = userAnswers.get(i);
                String correctAnswer = correctAnswers.get(i);
                
                // Extract the letter from user answer (e.g., "A - Option" -> "A")
                if (userAnswer != null && !userAnswer.isEmpty() && userAnswer.contains(" - ")) {
                    String userLetter = userAnswer.substring(0, 1);
                    if (userLetter.equals(correctAnswer)) {
                        score++;
                    }
                }
            }
        }
        
        double percentage = (double) score / totalQuestions * 100;
        String resultMessage = "Quiz Complete!\n\n";
        resultMessage += "Your Score: " + score + "/" + totalQuestions + "\n";
        resultMessage += "Percentage: " + String.format("%.1f", percentage) + "%\n\n";
        
        if (percentage >= 80) {
            resultMessage += "Excellent! Well done!";
        } else if (percentage >= 60) {
            resultMessage += "Good job! Keep studying!";
        } else if (percentage >= 40) {
            resultMessage += "You need more practice. Review the material!";
        } else {
            resultMessage += "Please review the material thoroughly!";
        }
        
        JOptionPane.showMessageDialog(this, resultMessage, "Quiz Results", JOptionPane.INFORMATION_MESSAGE);
        
        // Write result to file
        writeResultToFile();
        
        // Ask if user wants to view their results
        int choice = JOptionPane.showConfirmDialog(this, 
            "Would you like to view your results?", 
            "View Results", 
            JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            this.setVisible(false);
            new StudentResultsViewer(userEmail).setVisible(true);
        } else {
            this.setVisible(false);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        LBLlogo = new javax.swing.JLabel();
        BtnHelp = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        BtnNotes = new javax.swing.JButton();
        BtnQuiz = new javax.swing.JButton();
        BtnFAQs = new javax.swing.JButton();
        BtnLogOut = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        lblGrade = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel5 = new javax.swing.JPanel();
        lblQuestion = new javax.swing.JLabel();
        cbAnswer = new javax.swing.JComboBox<>();
        btnBack = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        LBLOption1 = new javax.swing.JLabel();
        LBLOption2 = new javax.swing.JLabel();
        LBLOption3 = new javax.swing.JLabel();
        LBLOption4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 153, 51));
        jPanel1.setForeground(new java.awt.Color(255, 153, 51));

        LBLlogo.setFont(new java.awt.Font("Apple Chancery", 3, 48)); // NOI18N
        LBLlogo.setText("Quiz");

        BtnHelp.setBackground(new java.awt.Color(255, 153, 51));
        BtnHelp.setText("?");
        BtnHelp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                BtnHelpMouseClicked(evt);
            }
        });
        BtnHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnHelpActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(BtnHelp)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(LBLlogo, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(288, 288, 288))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(LBLlogo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(BtnHelp))
        );

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));

        BtnNotes.setBackground(new java.awt.Color(0, 0, 0));
        BtnNotes.setForeground(new java.awt.Color(255, 255, 255));
        BtnNotes.setText("Notes");

        BtnQuiz.setBackground(new java.awt.Color(0, 0, 0));
        BtnQuiz.setForeground(new java.awt.Color(255, 255, 255));
        BtnQuiz.setText("Quiz");

        BtnFAQs.setBackground(new java.awt.Color(0, 0, 0));
        BtnFAQs.setForeground(new java.awt.Color(255, 255, 255));
        BtnFAQs.setText("FAQs");

        BtnLogOut.setBackground(new java.awt.Color(0, 0, 0));
        BtnLogOut.setForeground(new java.awt.Color(255, 255, 255));
        BtnLogOut.setText("Back to Menu");
        BtnLogOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnLogOutActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(BtnNotes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(BtnQuiz, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
            .addComponent(BtnFAQs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(BtnLogOut, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(BtnNotes, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(BtnQuiz, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(BtnFAQs, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(BtnLogOut, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(255, 153, 51));

        lblGrade.setFont(new java.awt.Font("Helvetica Neue", 1, 48)); // NOI18N
        lblGrade.setText("Grade (Placeholder)");

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        lblQuestion.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        lblQuestion.setText("Question: Placeholder");

        cbAnswer.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "A", "B", "C", "D" }));
        cbAnswer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbAnswerActionPerformed(evt);
            }
        });

        btnBack.setText("Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        btnNext.setText("Next");
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });

        LBLOption1.setText("A -");

        LBLOption2.setText("B -");

        LBLOption3.setText("C-");

        LBLOption4.setText("D-");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(245, 245, 245)
                        .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(124, 124, 124)
                        .addComponent(btnNext, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblQuestion, javax.swing.GroupLayout.PREFERRED_SIZE, 765, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(LBLOption4)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(LBLOption3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(LBLOption2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE)
                                .addComponent(LBLOption1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(336, 336, 336)
                        .addComponent(cbAnswer, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(406, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(lblQuestion, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LBLOption1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(LBLOption2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LBLOption3)
                .addGap(18, 18, 18)
                .addComponent(LBLOption4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addComponent(cbAnswer, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNext, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(70, 70, 70))
        );

        jScrollPane1.setViewportView(jPanel5);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 876, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblGrade, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(lblGrade)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void BtnHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHelpActionPerformed
        String helpText = "Quiz Help:\n\n";
        helpText += "1. Read each question carefully\n";
        helpText += "2. Select your answer from the dropdown menu\n";
        helpText += "3. Use 'Back' to review previous questions\n";
        helpText += "4. Use 'Next' to proceed to the next question\n";
        helpText += "5. Click 'Finish Quiz' on the last question to submit\n";
        helpText += "6. You can change your answers before finishing\n\n";
        helpText += "Good luck!";
        JOptionPane.showMessageDialog(this, helpText, "Quiz Help", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_BtnHelpActionPerformed

    private void BtnHelpMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_BtnHelpMouseClicked
        
    }//GEN-LAST:event_BtnHelpMouseClicked

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        // Save current answer before going back
        saveCurrentAnswer();
        
        // Go to previous question
        if (currentQuestion > 1) {
            loadQuestion(currentQuestion - 1);
            updateNavigationButtons();
        }
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        // Save current answer
        saveCurrentAnswer();
        
        if (currentQuestion == totalQuestions) 
        {
            // Ask for confirmation before finishing
            int choice = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to finish the quiz?\nYou cannot change your answers after submitting.", 
                "Finish Quiz", 
                JOptionPane.YES_NO_OPTION);
            
            if (choice == JOptionPane.YES_OPTION) {
                calculateScore();
            }
        } else {
            // Go to next question
            loadQuestion(currentQuestion + 1);
            updateNavigationButtons();
        }
    }//GEN-LAST:event_btnNextActionPerformed

    private void cbAnswerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbAnswerActionPerformed
        // Save answer when user changes selection
        saveCurrentAnswer();
    }//GEN-LAST:event_cbAnswerActionPerformed

    private void BtnLogOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnLogOutActionPerformed
        // Back to Menu
        this.setVisible(false);
        new HistoryED().setVisible(true);
    }//GEN-LAST:event_BtnLogOutActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Notes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Notes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Notes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Notes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                  new QuizDisplay().setVisible(true);
            }
        });
    }

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnFAQs;
    private javax.swing.JButton BtnHelp;
    private javax.swing.JButton BtnNotes;
    private javax.swing.JButton BtnQuiz;
    private javax.swing.JButton BtnLogOut;
    private javax.swing.JLabel LBLOption1;
    private javax.swing.JLabel LBLOption2;
    private javax.swing.JLabel LBLOption3;
    private javax.swing.JLabel LBLOption4;
    private javax.swing.JLabel LBLlogo;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnNext;
    private javax.swing.JComboBox<String> cbAnswer;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblGrade;
    private javax.swing.JLabel lblQuestion;
    // End of variables declaration//GEN-END:variables
   
}


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author kudamlambo
 */
public class QuizInterfaceClass {

    private String currentGrade;
    private String currentQuiz;

    ArrayList<String> quiz = new ArrayList<>();

    public QuizInterfaceClass(String inCG, String inCQ) {
        currentGrade = inCG;
        currentQuiz = inCQ;
    }

    public int numberOfQuestions() {
        int count = 0;
        try {
            Scanner scFile = new Scanner(new File(currentQuiz + ".txt"));

            while (scFile.hasNextLine()) {
                String line = scFile.nextLine();
                if (!line.trim().isEmpty()) {
                    count++;
                }
            }
            scFile.close();

        } catch (FileNotFoundException ex) {
            System.out.println("File Not Found: " + currentQuiz + ".txt");
        }
        return count;
    }

    public void addToArray() {
        try {
            Scanner scFile = new Scanner(new File(currentQuiz + ".txt"));

            while (scFile.hasNextLine()) {
                String line = scFile.nextLine();
                if (!line.trim().isEmpty()) {
                    quiz.add(line);
                }
            }
            scFile.close();

        } catch (FileNotFoundException ex) {
            System.out.println("File Not Found: " + currentQuiz + ".txt");
        }
    }
    
    public String qetQuestion(int questionNumber) {
        if (questionNumber > 0 && questionNumber <= quiz.size()) {
            return quiz.get(questionNumber - 1);
        }
        return "Question not found";
    }
    
    public String getAnswers(int questionNumber) 
    {
        if (questionNumber > 0 && questionNumber <= quiz.size()) {
            Scanner scLine = new Scanner(quiz.get(questionNumber - 1)).useDelimiter("#");
            String output = "";
            while (scLine.hasNext()) {
                output += scLine.next() + ", ";
            }
            return output;
        }
        return "Answers not found";
    }
    
    public String getCorrectAnswer(int questionNumber) {
        if (questionNumber > 0 && questionNumber <= quiz.size()) {
            String[] parts = quiz.get(questionNumber - 1).split("#");
            if (parts.length >= 6) {
                return parts[5]; // Assuming correct answer is at index 5
            }
        }
        return "";
    }
    
}

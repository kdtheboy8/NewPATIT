
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
                Scanner scLine = new Scanner(scFile.nextLine()).useDelimiter("#");
                count++;
                scFile.nextLine();
            }
            scFile.close();

        } catch (FileNotFoundException ex) {
            System.out.println("File Not Found!");
        }
        return count;
    }

    public void addToArray() {
        try {
            Scanner scFile = new Scanner(new File(currentQuiz + ".txt"));

            while (scFile.hasNextLine()) {
                quiz.add(scFile.nextLine());
            }
            scFile.close();

        } catch (FileNotFoundException ex) {
            System.out.println("File Not Found!");
        }
    }
    
    public String qetQuestion(int questionNumber) {
        return quiz.get(questionNumber-1);
    }
    
    public String getAnswers(int questionNumber) {
        Scanner scLine = new Scanner(quiz.get(questionNumber-1)).useDelimiter("#");
        String output = "";
        while (scLine.hasNext()) {
            output += scLine.next() + ", ";
        }
        return output;
    }
    
}

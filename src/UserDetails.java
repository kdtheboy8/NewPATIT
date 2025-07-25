
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author kudamlambo
 */
public class UserDetails {
    // Fields based on text file
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role;
    private String phoneNumber;
    public String filepath = "UserDetails";
    
    // Constructor
    public UserDetails(String firstName, String lastName, String email, String password, String role, String phoneNumber) 
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.phoneNumber = phoneNumber;
    }

    // Factory method to create a Student object from a line in the text file
    public void getDetails()
    {
        try
        {
            Scanner scFile = new Scanner(new File(filepath));
            Scanner scLine = new Scanner(scFile.nextLine()).useDelimiter("#");
            String fName= scLine.next();
            String lName=scLine.next();
            String emailInFile = scLine.next();
            scLine.next();
        }
        catch(FileNotFoundException ex)
        {
            System.out.println("File Not Found!");
        }
    }

    // Display method
    public void displayInfo() 
    {     
            System.out.println("Name: " + firstName + " " + lastName);
            System.out.println("Email: " + email);
            System.out.println("Role: " + role);      
    }

    // Getters (you can add setters if needed)
    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}

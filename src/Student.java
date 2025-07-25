/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author kudamlambo
 */
public class Student {
    // Fields based on text file
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role;
    private String phoneNumber;

    // Constructor
    public Student(String firstName, String lastName, String email, String password, String role, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.phoneNumber = phoneNumber;
    }

    // Factory method to create a Student object from a line in the text file
    public static Student fromTextLine(String line) {
        String[] parts = line.split("#");
        if (parts.length >= 6) {
            return new Student(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
        } else {
            System.out.println("Invalid data format: " + line);
            return null;
        }
    }

    // Display method
    public void displayStudent() {
        System.out.println("Name: " + firstName + " " + lastName);
        System.out.println("Email: " + email);
        System.out.println("Role: " + role);
        System.out.println("Phone: " + phoneNumber);
    }

    // Getters (you can add setters if needed)
    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}

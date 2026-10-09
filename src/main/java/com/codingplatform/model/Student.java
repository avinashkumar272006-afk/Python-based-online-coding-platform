package com.codingplatform.model;

/**
 * Student user model.
 */
public class Student extends User {
    
    public Student() {
        super();
        setRole("STUDENT");
    }

    public Student(int id, String name, String email, String password) {
        super(id, name, email, password, "STUDENT");
    }

    @Override
    public String getDashboardUrl() {
        return "/student/dashboard";
    }
}

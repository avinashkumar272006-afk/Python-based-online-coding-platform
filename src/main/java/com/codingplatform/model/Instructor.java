package com.codingplatform.model;

/**
 * Instructor user model.
 */
public class Instructor extends User {
    
    public Instructor() {
        super();
        setRole("INSTRUCTOR");
    }

    public Instructor(int id, String name, String email, String password) {
        super(id, name, email, password, "INSTRUCTOR");
    }

    @Override
    public String getDashboardUrl() {
        return "/instructor/dashboard";
    }
}

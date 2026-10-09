package com.codingplatform.model;

/**
 * Admin user model.
 */
public class Admin extends User {
    
    public Admin() {
        super();
        setRole("ADMIN");
    }

    public Admin(int id, String name, String email, String password) {
        super(id, name, email, password, "ADMIN");
    }

    @Override
    public String getDashboardUrl() {
        return "/admin/dashboard.jsp";
    }
}

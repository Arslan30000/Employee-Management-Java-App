package com.coresync.services;

public class UserSession {
    private static String loggedInEmployeeId;
    private static String fullName; // Added to store the employee's name

    public static String getLoggedInEmployeeId() {
        return loggedInEmployeeId;
    }

    public static void setLoggedInEmployeeId(String id) {
        loggedInEmployeeId = id;
    }

    // --- New Methods for Full Name ---
    public static String getFullName() {
        return fullName;
    }

    public static void setFullName(String name) {
        fullName = name;
    }
    // ---------------------------------
    
    public static void clear() {
        loggedInEmployeeId = null;
        fullName = null; // Clear the name on logout too
    }
}
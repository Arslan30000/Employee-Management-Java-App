package com.coresync.services;

public class UserSession {
    private static String loggedInEmployeeId;

    public static String getLoggedInEmployeeId() {
        return loggedInEmployeeId;
    }

    public static void setLoggedInEmployeeId(String id) {
        loggedInEmployeeId = id;
    }
    
    public static void clear() {
        loggedInEmployeeId = null;
    }
}
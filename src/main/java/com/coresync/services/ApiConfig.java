package com.coresync.services;

public final class ApiConfig {
    private ApiConfig() {
    }

    public static final String API_BASE_URL = "http://localhost/payroll_db";

    public static String endpoint(String path) {
        if (path == null || path.isEmpty()) {
            return API_BASE_URL;
        }
        if (path.startsWith("/")) {
            return API_BASE_URL + path;
        }
        return API_BASE_URL + "/" + path;
    }
}
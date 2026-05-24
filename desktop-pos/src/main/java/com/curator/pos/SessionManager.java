package com.curator.pos;

public class SessionManager {
    private static String loggedInUser = null;
    private static boolean isAdmin = false;

    public static void setSession(String username, boolean admin) {
        loggedInUser = username;
        isAdmin = admin;
    }

    public static String getLoggedInUser() {
        return loggedInUser;
    }

    public static boolean isAdmin() {
        return isAdmin;
    }

    public static void cleanSession() {
        loggedInUser = null;
        isAdmin = false;
    }

    public static boolean isLoggedIn() {
        return loggedInUser != null;
    }
}

package com.example.inovasiyanotebook.securety;

public class PermissionDeniedException extends Exception {
    public PermissionDeniedException(String message) {
        super(message);
    }
}

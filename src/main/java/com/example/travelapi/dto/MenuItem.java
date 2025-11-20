package com.example.travelapi.dto;

public record MenuItem(
        String id,
        String label,
        String icon
) {
    public MenuItem(String id, String label) {
        this(id, label, null);
    }
}

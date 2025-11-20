package com.example.travelapi.dto;

import com.example.travelapi.model.FeatureCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FeatureRequest(
        String id,
        @NotBlank String name,
        @NotBlank String description,
        @NotNull FeatureCategory category,
        @NotNull Boolean enabled
) {}

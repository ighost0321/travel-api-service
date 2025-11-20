package com.example.travelapi.model;

import java.time.Instant;
import java.util.UUID;

public class Feature {
    private String id;
    private String name;
    private String description;
    private FeatureCategory category;
    private boolean enabled;
    private Instant lastUpdated;

    public Feature(String name, String description, FeatureCategory category, boolean enabled) {
        this(UUID.randomUUID().toString(), name, description, category, enabled, Instant.now());
    }

    public Feature(String id, String name, String description, FeatureCategory category, boolean enabled) {
        this(id, name, description, category, enabled, Instant.now());
    }

    public Feature(String id, String name, String description, FeatureCategory category, boolean enabled, Instant lastUpdated) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.enabled = enabled;
        this.lastUpdated = lastUpdated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FeatureCategory getCategory() {
        return category;
    }

    public void setCategory(FeatureCategory category) {
        this.category = category;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}

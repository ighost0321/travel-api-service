package com.example.travelapi.controller;

import com.example.travelapi.dto.FeatureRequest;
import com.example.travelapi.model.Feature;
import com.example.travelapi.service.FeatureService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/features")
public class FeatureController {

    private final FeatureService featureService;

    public FeatureController(FeatureService featureService) {
        this.featureService = featureService;
    }

    @GetMapping
    public List<Feature> listFeatures() {
        return featureService.listFeatures();
    }

    @GetMapping("/overview")
    public Map<String, Object> overview() {
        return featureService.overview();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFeature(@PathVariable("id") String id) {
        if ("admin".equalsIgnoreCase(id)) {
            return ResponseEntity.ok(featureService.adminMenu());
        }
        return featureService.findFeature(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Feature> createFeature(@Valid @RequestBody FeatureRequest request) {
        try {
            Feature created = featureService.createFeature(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

}

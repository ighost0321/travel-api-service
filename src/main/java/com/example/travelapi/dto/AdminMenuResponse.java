package com.example.travelapi.dto;

import java.util.List;
import java.util.Map;

public record AdminMenuResponse(
        List<MenuItem> tier1,
        Map<String, List<MenuItem>> tier2,
        Map<String, List<MenuItem>> tier3
) {}

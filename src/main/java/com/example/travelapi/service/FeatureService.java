package com.example.travelapi.service;

import com.example.travelapi.dto.AdminMenuResponse;
import com.example.travelapi.dto.FeatureRequest;
import com.example.travelapi.dto.MenuItem;
import com.example.travelapi.model.Feature;
import com.example.travelapi.model.FeatureCategory;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class FeatureService {

    private final Map<String, Feature> featureStore = new ConcurrentHashMap<>();
    private static final AdminMenuResponse ADMIN_MENU = buildAdminMenu();

    private static AdminMenuResponse buildAdminMenu() {
        List<MenuItem> tier1 = List.of(
                new MenuItem("home", "首頁", "🏠"),
                new MenuItem("policies", "保單管理", "📋"),
                new MenuItem("claims", "理賠作業", "💼"),
                new MenuItem("agents", "業務員查詢", "👥"),
                new MenuItem("reports", "統計報表", "📊")
        );

        Map<String, List<MenuItem>> tier2 = Map.of(
                "policies", List.of(
                        new MenuItem("policies-new", "新增保單"),
                        new MenuItem("policies-list", "保單清單"),
                        new MenuItem("policies-edit", "編輯保單"),
                        new MenuItem("policies-delete", "刪除保單")
                ),
                "claims", List.of(
                        new MenuItem("claims-new", "新增理賠"),
                        new MenuItem("claims-list", "理賠清單"),
                        new MenuItem("claims-status", "理賠狀態")
                ),
                "agents", List.of(
                        new MenuItem("agents-list", "業務員清單"),
                        new MenuItem("agents-performance", "績效查詢"),
                        new MenuItem("agents-commission", "佣金管理")
                ),
                "reports", List.of(
                        new MenuItem("reports-sales", "銷售報表"),
                        new MenuItem("reports-claims", "理賠報表"),
                        new MenuItem("reports-revenue", "收入報表")
                )
        );

        Map<String, List<MenuItem>> tier3 = Map.of(
                "policies-new", List.of(
                        new MenuItem("policies-new-single", "單筆保單"),
                        new MenuItem("policies-new-batch", "批次匯入")
                ),
                "policies-edit", List.of(
                        new MenuItem("policies-edit-info", "編輯基本資訊"),
                        new MenuItem("policies-edit-coverage", "編輯保障內容")
                ),
                "claims-new", List.of(
                        new MenuItem("claims-new-medical", "醫療理賠"),
                        new MenuItem("claims-new-accident", "意外理賠")
                ),
                "reports-sales", List.of(
                        new MenuItem("reports-sales-daily", "日報表"),
                        new MenuItem("reports-sales-monthly", "月報表"),
                        new MenuItem("reports-sales-yearly", "年報表")
                )
        );

        return new AdminMenuResponse(tier1, tier2, tier3);
    }

    @PostConstruct
    void seedData() {
        createFeature(new FeatureRequest(
                null,
                "Dashboard Overview",
                "Shows at-a-glance KPIs for your travel insurance book.",
                FeatureCategory.DASHBOARD,
                true
        ));
        createFeature(new FeatureRequest(
                null,
                "Policy CRUD",
                "Create, edit, archive and search travel policies.",
                FeatureCategory.POLICY_MANAGEMENT,
                true
        ));
        createFeature(new FeatureRequest(
                null,
                "Claims Intake",
                "Guided wizard to register medical or trip-cancel claims.",
                FeatureCategory.CLAIMS,
                false
        ));
        createFeature(new FeatureRequest(
                null,
                "Agent Performance",
                "Leaderboard of agency KPIs and commission tracking.",
                FeatureCategory.AGENT_PORTAL,
                false
        ));
        createFeature(new FeatureRequest(
                null,
                "Revenue Analytics",
                "Month-over-month premium and loss ratio insights.",
                FeatureCategory.REPORTING,
                false
        ));
    }

    public List<Feature> listFeatures() {
        return new ArrayList<>(featureStore.values());
    }

    public AdminMenuResponse adminMenu() {
        return ADMIN_MENU;
    }

    public Optional<Feature> findFeature(String id) {
        return Optional.ofNullable(featureStore.get(id));
    }

    public Feature createFeature(FeatureRequest request) {
        String requestedId = StringUtils.hasText(request.id()) ? request.id().trim() : null;
        if (requestedId != null && featureStore.containsKey(requestedId)) {
            throw new IllegalArgumentException("Feature ID already exists: " + requestedId);
        }
        Feature feature = requestedId == null
                ? new Feature(
                        request.name(),
                        request.description(),
                        request.category(),
                        request.enabled()
                )
                : new Feature(
                        requestedId,
                        request.name(),
                        request.description(),
                        request.category(),
                        request.enabled()
                );
        featureStore.put(feature.getId(), feature);
        return feature;
    }

    public Map<String, Long> categorySummary() {
        return featureStore.values().stream()
                .collect(Collectors.groupingBy(f -> f.getCategory().name(), Collectors.counting()));
    }

    public Map<String, Long> statusSummary() {
        return featureStore.values().stream()
                .collect(Collectors.groupingBy(f -> f.isEnabled() ? "enabled" : "disabled", Collectors.counting()));
    }

    public Map<String, Object> overview() {
        return Map.of(
                "total", featureStore.size(),
                "status", statusSummary(),
                "byCategory", categorySummary()
        );
    }
}

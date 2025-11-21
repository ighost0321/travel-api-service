package com.example.travelapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
// 引入 OAuth2 相關的套件
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.List;

@Configuration
// 啟用方法級別的安全（@PreAuthorize, @PostAuthorize 等）
@EnableMethodSecurity 
public class SecurityConfig {

    /**
     * 配置安全過濾鏈，啟用 OAuth2 Resource Server。
     * @param http HttpSecurity 實例
     * @param corsConfigurationSource CORS 配置來源
     * @return 配置好的 SecurityFilterChain
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                // 禁用 CSRF，因為我們使用無狀態的 Bearer Token
                .csrf(csrf -> csrf.disable()) 
                // 將 Session 設置為無狀態 (STATELESS)，不使用 Session
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) 
                // 啟用 CORS 配置
                .cors(cors -> cors.configurationSource(corsConfigurationSource)) 
                
                // 1. 啟用 OAuth 2.0 資源伺服器來處理 JWT Bearer Token
                .oauth2ResourceServer(oauth2 -> oauth2
                        // 使用 JWT 進行驗證
                        .jwt(Customizer.withDefaults()) 
                        // 自定義未認證 (401) 時的錯誤回應
                        .authenticationEntryPoint((request, response, authException) -> { 
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.getWriter().write("{\"error\":\"unauthorized\"}");
                        })
                )
                
                // 2. 授權規則
                .authorizeHttpRequests(auth -> auth
                        // 允許健康檢查和資訊端點無需認證
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        // 允許 Swagger/OpenAPI 文件無需認證
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**"
                        ).permitAll()
                        // 允許 OPTIONS 預檢請求（CORS 所需）無需認證
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 其他所有請求都必須經過認證（需附帶有效 JWT）
                        .anyRequest().authenticated()
                );
                
                // 移除：.httpBasic(Customizer.withDefaults());

        return http.build();
    }
    
    // 移除：UserDetailsService userDetailsService() 函式 (不再使用記憶體使用者)
    // 移除：PasswordEncoder passwordEncoder() 函式 (不再使用 Basic 認證)

    /**
     * CORS 配置。
     * @param allowedOrigins 允許的來源列表
     * @return CorsConfigurationSource 實例
     */
    @Bean
    @Primary
    CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origins:http://localhost:5173}") List<String> allowedOrigins
    ) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // 必須允許 Authorization Header 才能傳遞 Keycloak Token
        config.setAllowedHeaders(List.of("Authorization", "Content-Type")); 
        config.setAllowCredentials(false);
        config.setMaxAge(Duration.ofHours(1));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
package com.agriculture.common.config;

import com.agriculture.common.security.AccessDeniedHandlerImpl;
import com.agriculture.common.security.AuthEntryPoint;
import com.agriculture.common.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final AuthEntryPoint authEntryPoint;
    private final AccessDeniedHandlerImpl accessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(c -> c.configurationSource(corsSource()))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 认证接口
                        .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()
                        // 公开查询接口
                        .requestMatchers(HttpMethod.GET, "/api/public/**").permitAll()
                        // 文章/问答/讲座 GET 公开
                        .requestMatchers(HttpMethod.GET, "/api/knowledge/articles", "/api/knowledge/articles/**").permitAll()
                        .requestMatchers("/api/knowledge/questions", "/api/knowledge/questions/**").permitAll()
                        .requestMatchers("/api/question/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/knowledge/lectures", "/api/knowledge/lectures/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/knowledge/videos", "/api/knowledge/videos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/knowledge/rag/search", "/api/knowledge/rag/search/chunks", "/api/knowledge/rag/ask").permitAll()
                        .requestMatchers("/api/knowledge/videos/*/view", "/api/knowledge/videos/*/like", "/api/knowledge/videos/*/favorite", "/api/knowledge/videos/*/cancel-favorite", "/api/knowledge/videos/scan", "/api/knowledge/videos/upload").permitAll()
                        .requestMatchers("/api/knowledge/videos/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/knowledge/categories").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/knowledge/pests", "/api/knowledge/pests/**").permitAll()
                        // 作物字典公开
                        .requestMatchers(HttpMethod.GET, "/api/farm/crops").permitAll()
                        // 溯源产品公开查询
                        .requestMatchers(HttpMethod.GET, "/api/trace/products/public/**").permitAll()
                        // 溯源管理受权限控制（由@PreAuthorize控制）
                        // 文件静态资源
                        .requestMatchers("/files/**", "/videos/**").permitAll()
                        // Swagger
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // 其他需要登录
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration c) throws Exception {
        return c.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsSource() {
        CorsConfiguration conf = new CorsConfiguration();
        conf.setAllowedOriginPatterns(List.of("*"));
        conf.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        conf.setAllowedHeaders(List.of("*"));
        conf.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", conf);
        return source;
    }
}

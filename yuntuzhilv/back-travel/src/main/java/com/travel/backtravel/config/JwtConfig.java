package com.travel.backtravel.config;

import com.travel.backtravel.entity.User;
import com.travel.backtravel.mapper.UserMapper;
import com.travel.backtravel.util.JwtUtil;
import com.travel.backtravel.util.RedisUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class JwtConfig {

    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final UserMapper userMapper;

    private static final String[] WHITE_LIST = {
            "/user/register",
            "/user/login",
            "/user/sendCode",
            "/error",
            "/doc.html",
            "/webjars/**",
            "/v3/api-docs/**",
            "/v2/api-docs/**",
            "/swagger-resources/**",
            "/favicon.ico"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/attraction/list", "/attraction/detail",
                                         "/hotel/list", "/hotel/detail", "/traffic/list").permitAll()
                        .requestMatchers(WHITE_LIST).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, redisUtil, userMapper);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Slf4j
    public static class JwtAuthenticationFilter extends org.springframework.web.filter.OncePerRequestFilter {

        private final JwtUtil jwtUtil;
        private final RedisUtil redisUtil;
        private final UserMapper userMapper;

        private static final List<String> EXCLUDED_PATHS = List.of(
                "/user/register",
                "/user/login",
                "/user/sendCode",
                "/doc.html",
                "/webjars",
                "/v3/api-docs",
                "/v2/api-docs",
                "/swagger-resources",
                "/favicon.ico"
        );

        public JwtAuthenticationFilter(JwtUtil jwtUtil, RedisUtil redisUtil, UserMapper userMapper) {
            this.jwtUtil = jwtUtil;
            this.redisUtil = redisUtil;
            this.userMapper = userMapper;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
            String servletPath = request.getServletPath();
            String method = request.getMethod();

            boolean isPublicRead = "GET".equalsIgnoreCase(method) && (
                    servletPath.equals("/attraction/list") ||
                    servletPath.equals("/attraction/detail") ||
                    servletPath.equals("/hotel/list") ||
                    servletPath.equals("/hotel/detail") ||
                    servletPath.equals("/traffic/list")
            );

            boolean isExcluded = EXCLUDED_PATHS.stream()
                    .anyMatch(path -> servletPath.equals(path) || servletPath.startsWith(path + "/"));

            if (isPublicRead || isExcluded) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = resolveToken(request);

            if (token != null && !token.isEmpty()) {
                try {
                    if (redisUtil.isMemberOfSet("jwt:blacklist", token)) {
                        writeJson(response, 401, "Token已失效");
                        return;
                    }

                    if (jwtUtil.isTokenExpired(token)) {
                        writeJson(response, 401, "Token已过期");
                        return;
                    }

                    Long userId = jwtUtil.getUserIdFromToken(token);
                    String username = jwtUtil.getUsernameFromToken(token);

                    if (userId != null && username != null) {
                        // 从数据库查询真实角色，而不是信任token中的role声明
                        User user = userMapper.selectById(userId);
                        if (user == null || user.getStatus() == 0) {
                            writeJson(response, 401, "账号不存在或已禁用");
                            return;
                        }

                        String dbRole = user.getRole();
                        String upperRole = dbRole != null ? dbRole.toUpperCase() : "USER";

                        List<SimpleGrantedAuthority> authorities = List.of(
                                new SimpleGrantedAuthority("ROLE_" + upperRole)
                        );

                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(userId, null, authorities);
                        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(auth);

                        log.debug("JWT认证成功: userId={}, role={}, path={}", userId, upperRole, servletPath);
                    }
                } catch (Exception e) {
                    log.warn("JWT解析失败: {}", e.getMessage());
                    writeJson(response, 401, "Token无效");
                    return;
                }
            }

            filterChain.doFilter(request, response);
        }

        private String resolveToken(HttpServletRequest request) {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                return token.substring(7);
            }
            if (token == null || token.isEmpty()) {
                token = request.getHeader("token");
            }
            if (token == null || token.isEmpty()) {
                token = request.getParameter("token");
            }
            return token;
        }

        private void writeJson(HttpServletResponse response, int code, String msg) throws IOException {
            response.setStatus(code);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":"+code+",\"message\":\""+msg+"\"}");
        }
    }
}

package com.developlife.reviewtwits.config.security;


import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * @author ghdic
 * @since 2023/02/19
 */
// WebSecurityConfigurerAdapter deprecated -> SecurityFilterChain Bean등록해서 사용
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public WebSecurityConfig(JwtAccessDeniedHandler jwtAccessDeniedHandler, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }


    // 정적 자원에 대해서는 Security 설정을 적용하지 않음
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic().disable()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/users/admin").hasRole("ADMIN")
            .antMatchers("/users/me").hasRole("USER")
            .antMatchers("/users/change-detail-messages").hasRole("USER")
            .antMatchers("/users/save-profile-image").hasRole("USER")
            .antMatchers("/projects/**").hasRole("USER")
            .antMatchers(HttpMethod.POST,"/reviews/shopping").hasRole("USER")
            .antMatchers("/sns/review-reaction/**").hasRole("USER")
            .antMatchers(HttpMethod.POST, "/sns/comments/**").hasRole("USER")
            .antMatchers(HttpMethod.DELETE,"/sns/comments/**").hasRole("USER")
            .antMatchers(HttpMethod.PATCH, "/sns/comments/**").hasRole("USER")
            .antMatchers("/sns/reviews/**").hasRole("USER")
            .antMatchers("/sns/reviews").hasRole("USER")
            .antMatchers(HttpMethod.POST,"/reviews/shopping").hasRole("USER")
            .antMatchers(HttpMethod.DELETE,"/reviews/shopping/**").hasRole("USER")
            .antMatchers(HttpMethod.PATCH,"/reviews/shopping/**").hasRole("USER")
            .antMatchers(HttpMethod.PUT,"/reviews/shopping/**").hasRole("USER")
            .antMatchers("/sns/request-follow").hasRole("USER")
            .antMatchers("/sns/request-unfollow").hasRole("USER")
            .antMatchers("/sns/scrap/reviews").hasRole("USER")
            .antMatchers("/sns/scrap-reviews/**").hasRole("USER")
            .antMatchers("/sns/comments-like/**").hasRole("USER")
            .antMatchers("/products/register/**").hasRole("USER")
            .antMatchers("/review-management/approve").hasRole("USER")
            .anyRequest()
            .permitAll()
            .and()
            .exceptionHandling()
            .accessDeniedHandler(jwtAccessDeniedHandler)
                .authenticationEntryPoint(authenticationEntryPoint())
            .and()
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .cors(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:8080", "http://localhost:3000", "http://43.201.141.63:8080", "https://reviewtwits.mcv.kr",
            "https://localhost:8080", "https://localhost:3000", "https://43.201.141.63:8080","https://reviewtwits.shop",
                "http://reviewtwits.shop"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }

//    @Bean
//    public CorsFilter corsFilter() {
//        CorsFilter filter = new CorsFilter(corsConfigurationSource());
//        return filter;
//    }
}

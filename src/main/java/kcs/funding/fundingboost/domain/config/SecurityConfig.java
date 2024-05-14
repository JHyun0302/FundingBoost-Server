package kcs.funding.fundingboost.domain.config;

import kcs.funding.fundingboost.domain.security.entrypoint.JwtAuthenticationEntryPoint;
import kcs.funding.fundingboost.domain.security.handler.JwtAccessDeniedHandler;
import kcs.funding.fundingboost.domain.security.provider.JwtAuthenticationProvider;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CorsConfig corsConfig;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    public SecurityConfig(CorsConfig corsConfig, JwtAuthenticationProvider jwtAuthenticationProvider,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          JwtAccessDeniedHandler jwtAccessDeniedHandler) {
        this.corsConfig = corsConfig;
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler))
                .cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request -> request
                        .requestMatchers(PathRequest.toH2Console()).permitAll())
//                        .csrf(csrf -> csrf.ignoringRequestMatchers(PathRequest.toH2Console()))
                .headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/login",
                                "/api/v1/signup"
                        ).permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "v3/api-docs/**"
                        ).permitAll()
                        .anyRequest().authenticated())
                .with(new JwtSecurityConfig(jwtAuthenticationProvider), customizer -> {
                });
        return http.build();
    }
}

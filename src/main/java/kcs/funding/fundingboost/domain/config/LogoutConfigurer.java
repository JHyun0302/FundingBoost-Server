package kcs.funding.fundingboost.domain.config;

import kcs.funding.fundingboost.domain.security.fliter.JwtFilter;
import kcs.funding.fundingboost.domain.security.fliter.JwtLogoutFilter;
import kcs.funding.fundingboost.domain.security.handler.JwtLogoutHandler;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;

public class LogoutConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final JwtLogoutHandler jwtLogoutHandler;

    public LogoutConfigurer(JwtLogoutHandler jwtLogoutHandler) {
        this.jwtLogoutHandler = jwtLogoutHandler;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        JwtLogoutFilter logoutFilter = new JwtLogoutFilter(jwtLogoutHandler);
        http
                .addFilterBefore(logoutFilter, JwtFilter.class);
    }
}

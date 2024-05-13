package kcs.funding.fundingboost.domain.security.fliter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import kcs.funding.fundingboost.domain.security.handler.JwtLogoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;

@Slf4j
public class JwtLogoutFilter extends GenericFilterBean {

    private final JwtLogoutHandler jwtLogoutHandler;

    public JwtLogoutFilter(JwtLogoutHandler jwtLogoutHandler) {
        this.jwtLogoutHandler = jwtLogoutHandler;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        if (httpServletRequest.getHeader("access_token") != null
                && httpServletRequest.getHeader("refresh_token") != null) {
            jwtLogoutHandler.handle(httpServletRequest);
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}

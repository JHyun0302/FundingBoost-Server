package kcs.funding.fundingboost.domain.security.fliter;

import static kcs.funding.fundingboost.domain.security.utils.SecurityConst.LOG_OUT_URI;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import kcs.funding.fundingboost.domain.security.handler.JwtLogoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class JwtLogoutFilter extends OncePerRequestFilter {

    private final JwtLogoutHandler jwtLogoutHandler;

    public JwtLogoutFilter(JwtLogoutHandler jwtLogoutHandler) {
        this.jwtLogoutHandler = jwtLogoutHandler;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        log.info("requestURI in JwtLogoutFilter");
        if (requestURI.equals(LOG_OUT_URI) && request.getHeader("access_token") != null
                && request.getHeader("refresh_token") != null) {
            jwtLogoutHandler.handle(request);
            return;
        }
        filterChain.doFilter(request, response);
    }
}

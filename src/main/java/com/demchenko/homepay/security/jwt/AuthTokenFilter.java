package com.demchenko.homepay.security.jwt;

import com.demchenko.homepay.security.service.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public AuthTokenFilter(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                Cookie cookieAuth = new Cookie("Authentication", "");
                Cookie cookieRole = new Cookie("Role", "");
                cookieAuth.setMaxAge(0);
                cookieRole.setMaxAge(0);
                response.addCookie(cookieAuth);
                response.addCookie(cookieRole);
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: { }", e);
        }
        filterChain.doFilter(request, response);
    }


    private String parseJwt(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("Authorization")) {
                return cookie.getValue().substring(6);
            }
        }
        return null;
    }

}
